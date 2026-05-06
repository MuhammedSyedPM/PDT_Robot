// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurApiException extends Exception
{
    private static final long serialVersionUID = 6323517766203582348L;
    public int error;
    
    public NurApiException(final String message) {
        super(message);
        this.error = 16;
    }
    
    public NurApiException(final String function, final int error) {
        super(function + "; NUR API error " + error + ": " + NurApiErrors.getErrorMessage(error));
        this.error = error;
    }
    
    public NurApiException(final int error) {
        super("NUR API error " + error + ": " + NurApiErrors.getErrorMessage(error));
        this.error = error;
    }
}
