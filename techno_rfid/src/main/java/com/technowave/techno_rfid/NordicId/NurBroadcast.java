// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBroadcast
{
    public int bc_tag;
    public int bc_devtype;
    public int bc_company;
    public int f_type;
    public int f_op;
    public byte[] f_data;
    public int bc_cmd;
    public int bc_len;
    public byte[] bc_data;
    
    public NurBroadcast(final int bc_tag, final int bc_devtype, final int bc_company, final int f_type, final int f_op, final byte[] f_data, final int bc_cmd, final int bc_len, final byte[] bc_data) {
        this.f_data = new byte[16];
        this.bc_data = new byte[200];
        this.bc_tag = bc_tag;
        this.bc_devtype = bc_devtype;
        this.bc_company = bc_company;
        this.f_type = f_type;
        this.f_op = f_op;
        this.f_data = f_data;
        this.bc_cmd = bc_cmd;
        this.bc_len = bc_len;
        this.bc_data = bc_data;
    }
    
    public NurBroadcast() {
        this.f_data = new byte[16];
        this.bc_data = new byte[200];
    }
}
