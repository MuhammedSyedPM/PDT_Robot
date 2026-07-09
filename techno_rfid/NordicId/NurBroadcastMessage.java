// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBroadcastMessage
{
    public int bc_tag;
    public int bc_devtype;
    public int bc_company;
    public int f_type;
    public int f_op;
    public byte[] f_data;
    public int f_size;
    public int bc_cmd;
    public int bc_len;
    public byte[] bc_data;
    
    NurBroadcastMessage(final int bc_tag, final int bc_devtype, final int bc_company, final int f_type, final int f_op, final byte[] f_data, final int f_size, final int bc_cmd, final int bc_len, final byte[] bc_data) {
        this.f_data = new byte[16];
        this.bc_data = new byte[200];
        this.bc_tag = bc_tag;
        this.bc_devtype = bc_devtype;
        this.bc_company = bc_company;
        this.bc_cmd = bc_cmd;
        if (bc_data != null) {
            System.arraycopy(bc_data, 0, this.bc_data, 0, bc_len);
        }
        else {
            this.bc_data = null;
        }
        this.bc_len = bc_len;
        this.f_type = f_type;
        this.f_op = f_op;
        if (f_data != null) {
            System.arraycopy(f_data, 0, this.f_data, 0, f_size);
        }
        else {
            this.f_data = null;
        }
        this.f_size = f_size;
    }
    
    NurBroadcastMessage() {
        this.f_data = new byte[16];
        this.bc_data = new byte[200];
    }
}
