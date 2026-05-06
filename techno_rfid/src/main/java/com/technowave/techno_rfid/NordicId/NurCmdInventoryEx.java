// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInventoryEx extends NurCmd
{
    public static final int CMD = 59;
    private NurInventoryExtended mInvEx;
    private NurInventoryExtendedFilter[] mFilters;
    private boolean mStream;
    private boolean mRerun;
    private boolean mStop;
    private NurRespInventory mResp;
    
    public NurRespInventory getResponse() {
        return this.mResp;
    }
    
    public NurCmdInventoryEx(final NurInventoryExtended invEx, final NurInventoryExtendedFilter filter, final boolean stream) throws NurApiException {
        super(59, 0, (invEx == null) ? 0 : (9 + filter.getFilterByteSize()));
        this.mInvEx = null;
        this.mRerun = false;
        this.mStop = false;
        this.mResp = new NurRespInventory();
        if (invEx != null) {
            if (filter.maskBitLength > 255) {
                throw new NurApiException(5);
            }
            this.mInvEx = invEx;
            (this.mFilters = new NurInventoryExtendedFilter[1])[0] = filter;
            this.mStream = stream;
        }
    }
    
    static int calculatePayloadSize(final NurInventoryExtendedFilter[] filters) throws NurApiException {
        int ret = 9;
        if (filters == null || filters.length > 8) {
            throw new NurApiException(5);
        }
        for (final NurInventoryExtendedFilter filter : filters) {
            ret += filter.getFilterByteSize();
        }
        return ret;
    }
    
    public NurCmdInventoryEx(final NurInventoryExtended invEx, final NurInventoryExtendedFilter[] filters, final boolean stream) throws NurApiException {
        super(59, 0, (invEx == null) ? 0 : calculatePayloadSize(filters));
        this.mInvEx = null;
        this.mRerun = false;
        this.mStop = false;
        this.mResp = new NurRespInventory();
        if (invEx != null) {
            this.mInvEx = invEx;
            this.mFilters = filters;
            this.mStream = stream;
        }
    }
    
    public NurCmdInventoryEx(final boolean stream) throws NurApiException {
        super(59, 0, 1);
        this.mInvEx = null;
        this.mRerun = false;
        this.mStop = false;
        this.mResp = new NurRespInventory();
        this.mStream = stream;
        this.mRerun = true;
    }
    
    public NurCmdInventoryEx() throws NurApiException {
        super(59, 0, 1);
        this.mInvEx = null;
        this.mRerun = false;
        this.mStop = false;
        this.mResp = new NurRespInventory();
        this.mStop = true;
    }
    
    private int serializeFilter(final NurInventoryExtendedFilter filter, final byte[] data, final int offset) {
        int ptr = offset;
        ptr += NurPacket.PacketByte(data, ptr, filter.truncate ? 1 : 0);
        ptr += NurPacket.PacketByte(data, ptr, filter.targetSession);
        ptr += NurPacket.PacketByte(data, ptr, filter.action);
        ptr += NurPacket.PacketByte(data, ptr, filter.bank);
        ptr += NurPacket.PacketDword(data, ptr, filter.address);
        ptr += NurPacket.PacketByte(data, ptr, filter.maskBitLength);
        final int byteSize = NurApi.bitLenToByteLen(filter.maskBitLength);
        if (byteSize > 0) {
            ptr += NurPacket.PacketBytes(data, ptr, filter.maskdata, byteSize);
        }
        return ptr - offset;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        int ptr = offset;
        int cFlags = 0;
        if (this.mStop) {
            NurPacket.PacketByte(data, offset, 1);
            return 1;
        }
        if (this.mRerun) {
            NurPacket.PacketByte(data, offset, 2);
            return 1;
        }
        if (this.mInvEx == null) {
            return 0;
        }
        if (this.mStream) {
            cFlags |= 0x1;
        }
        ptr += NurPacket.PacketByte(data, ptr, cFlags);
        ptr += NurPacket.PacketByte(data, ptr, this.mInvEx.Q);
        ptr += NurPacket.PacketByte(data, ptr, this.mInvEx.session);
        ptr += NurPacket.PacketByte(data, ptr, this.mInvEx.rounds);
        ptr += NurPacket.PacketWord(data, ptr, this.mInvEx.transitTime);
        ptr += NurPacket.PacketByte(data, ptr, this.mInvEx.inventoryTarget);
        ptr += NurPacket.PacketByte(data, ptr, this.mInvEx.inventorySelState);
        ptr += NurPacket.PacketByte(data, ptr, this.mFilters.length);
        for (final NurInventoryExtendedFilter filter : this.mFilters) {
            ptr += this.serializeFilter(filter, data, ptr);
        }
        return ptr - offset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status != 0 && this.status != 32) {
            return;
        }
        if (this.status == 32) {
            this.status = 0;
        }
        this.mResp.numTagsFound = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.numTagsMem = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.roundsDone = NurPacket.BytesToByte(data, offset++);
        this.mResp.collisions = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.Q = NurPacket.BytesToByte(data, offset);
    }
}
