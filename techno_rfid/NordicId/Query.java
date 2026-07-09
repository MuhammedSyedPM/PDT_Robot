// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Iterator;
import java.util.Collection;
import java.net.SocketTimeoutException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashSet;
import java.util.Set;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.locks.Lock;

class Query
{
    private final Service service;
    private final Domain domain;
    private final int browsingTimeout;
    private final Lock socketLock;
    private MulticastSocket socket;
    private InetAddress mdnsGroupIPv4;
    private InetAddress mdnsGroupIPv6;
    private boolean isUsingIPv4;
    private boolean isUsingIPv6;
    private Question initialQuestion;
    private Set<Question> questions;
    private Set<Instance> instances;
    private Set<Record> records;
    private boolean listenerStarted;
    private boolean listenerFinished;
    public static final String MDNS_IP4_ADDRESS = "224.0.0.251";
    public static final String MDNS_IP6_ADDRESS = "FF02::FB";
    public static final int MDNS_PORT = 5353;
    private static final int WAIT_FOR_LISTENER_MS = 10;
    static final InetAddress TEST_SUITE_ADDRESS;
    private static final int BROWSING_TIMEOUT = 750;
    
    public static Query createFor(final Service service, final Domain domain) {
        return new Query(service, domain, 750);
    }
    
    public static Query createWithTimeout(final Service service, final Domain domain, final int timeout) {
        return new Query(service, domain, timeout);
    }
    
    private Query(final Service service, final Domain domain, final int browsingTimeout) {
        this.service = service;
        this.domain = domain;
        this.browsingTimeout = browsingTimeout;
        this.questions = new HashSet<Question>();
        this.records = new HashSet<Record>();
        this.socketLock = new ReentrantLock();
    }
    
    public Set<Instance> runOnce() throws IOException {
        return this.runOnceOn(InetAddress.getLocalHost());
    }
    
    public Set<Instance> runOnceOn(final InetAddress localhost) throws IOException {
        this.initialQuestion = new Question(this.service, this.domain);
        this.instances = Collections.synchronizedSet(new HashSet<Instance>());
        try {
            Thread listener = null;
            if (localhost != Query.TEST_SUITE_ADDRESS) {
                this.openSocket(localhost);
                listener = this.listenForResponses();
                while (!this.isServerIsListening()) {}
            }
            this.ask(this.initialQuestion);
            if (listener != null) {
                try {
                    listener.join();
                }
                catch (InterruptedException ex) {}
            }
        }
        finally {
            this.closeSocket();
        }
        return this.instances;
    }
    
    private void ask(final Question question) throws IOException {
        if (this.questions.contains(question)) {
            return;
        }
        this.questions.add(question);
        if (this.isUsingIPv4) {
            question.askOn(this.socket, this.mdnsGroupIPv4);
        }
        if (this.isUsingIPv6) {}
    }
    
    private boolean isServerIsListening() {
        boolean retval;
        try {
            while (!this.socketLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                this.socketLock.notify();
            }
            if (this.listenerFinished) {
                throw new RuntimeException("Listener has already finished");
            }
            retval = this.listenerStarted;
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Server is not listening");
        }
        finally {
            this.socketLock.unlock();
        }
        return retval;
    }
    
    public void start() {
        throw new RuntimeException("Not implemented yet");
    }
    
    private void openSocket(final InetAddress localhost) throws IOException {
        this.mdnsGroupIPv4 = InetAddress.getByName("224.0.0.251");
        this.mdnsGroupIPv6 = InetAddress.getByName("FF02::FB");
        (this.socket = new MulticastSocket(5353)).setInterface(localhost);
        try {
            this.socket.joinGroup(this.mdnsGroupIPv4);
            this.isUsingIPv4 = true;
        }
        catch (SocketException ex) {}
        try {
            this.socket.joinGroup(this.mdnsGroupIPv6);
            this.isUsingIPv6 = true;
        }
        catch (SocketException ex2) {}
        if (!this.isUsingIPv4 && !this.isUsingIPv6) {
            throw new IOException("No usable network interfaces found");
        }
        this.socket.setTimeToLive(255);
        this.socket.setSoTimeout(this.browsingTimeout);
    }
    
    private Thread listenForResponses() {
        final Thread listener = new Thread() {
            @Override
            public void run() {
                Query.this.collectResponses();
            }
        };
        listener.start();
        return listener;
    }
    
    private Set<Instance> collectResponses() {
        long currentTime;
        final long startTime = currentTime = System.currentTimeMillis();
        this.socketLock.lock();
        this.listenerStarted = true;
        this.listenerFinished = false;
        this.socketLock.unlock();
        int timeouts = 0;
        while (timeouts == 0 && currentTime - startTime < this.browsingTimeout) {
            final byte[] responseBuffer = new byte[9000];
            final DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            try {
                this.socket.receive(responsePacket);
                currentTime = System.currentTimeMillis();
                try {
                    this.parseResponsePacket(responsePacket);
                }
                catch (IllegalArgumentException e) {
                    timeouts = 0;
                    continue;
                }
                timeouts = 0;
            }
            catch (SocketTimeoutException e2) {
                ++timeouts;
            }
            catch (IOException ex) {}
        }
        this.socketLock.lock();
        this.listenerFinished = true;
        this.socketLock.unlock();
        this.buildInstancesFromRecords();
        return this.instances;
    }
    
    void parseResponsePacket(final DatagramPacket packet) throws IOException {
        final Response response = Response.createFrom(packet);
        if (response.answers(this.questions)) {
            this.records.addAll(response.getRecords());
        }
    }
    
    private void querySrvRecordFor(final PtrRecord ptr) throws IOException {
        final Question question = new Question(ptr.getPtrName(), Question.QType.SRV, Question.QClass.IN);
        this.ask(question);
    }
    
    private void queryTxtRecordFor(final PtrRecord ptr) throws IOException {
        final Question question = new Question(ptr.getPtrName(), Question.QType.TXT, Question.QClass.IN);
        this.ask(question);
    }
    
    private void queryAddressesFor(final SrvRecord srv) throws IOException {
        Question question = new Question(srv.getTarget(), Question.QType.A, Question.QClass.IN);
        this.ask(question);
        question = new Question(srv.getTarget(), Question.QType.AAAA, Question.QClass.IN);
        this.ask(question);
    }
    
    void buildInstancesFromRecords() {
        for (final Record rec : this.records) {
            if (rec instanceof PtrRecord && this.initialQuestion.answeredBy(rec)) {
                this.instances.add(Instance.createFromRecords((PtrRecord)rec, this.records));
            }
        }
    }
    
    private void closeSocket() {
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
    }
    
    Set<Question> getQuestions() {
        return Collections.unmodifiableSet((Set<? extends Question>)this.questions);
    }
    
    Set<Instance> getInstances() {
        return Collections.unmodifiableSet((Set<? extends Instance>)this.instances);
    }
    
    static {
        TEST_SUITE_ADDRESS = null;
    }
}
