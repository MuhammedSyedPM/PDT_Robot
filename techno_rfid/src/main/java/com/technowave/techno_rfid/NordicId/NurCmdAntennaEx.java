// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdAntennaEx extends NurCmd
{
    public static final int CMD = 37;
    private AntennaMapping[] mMap;
    
    NurCmdAntennaEx() {
        super(37);
        this.mMap = null;
    }
    
    public AntennaMapping[] getResponse() {
        return this.mMap;
    }
    
    public static final int parseMapping(final byte[] srcData, final int offset, final AntennaMapping mapping) {
        int newOffset = offset;
        final int id = srcData[newOffset++];
        int nameLen = srcData[newOffset++];
        if (id >= 0 && id < 32 && nameLen > 0 && nameLen <= 16) {
            mapping.antennaId = id;
            mapping.name = "";
            while (nameLen-- > 0) {
                mapping.name += (char)srcData[newOffset++];
            }
            return newOffset;
        }
        return -1;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mMap = null;
        if (dataLen < 4) {
            return;
        }
        final int nMappings = data[offset++];
        if (nMappings < 1 || nMappings > 32) {
            return;
        }
        final AntennaMapping[] tmpArr = new AntennaMapping[nMappings];
        int curOffset = offset;
        int i;
        for (i = 0; i < nMappings; ++i) {
            final AntennaMapping tmpMap = new AntennaMapping();
            curOffset = parseMapping(data, curOffset, tmpMap);
            if (curOffset < 0) {
                break;
            }
            tmpArr[i] = tmpMap;
        }
        this.mMap = new AntennaMapping[nMappings];
        for (int n = 0; n < i; ++n) {
            this.mMap[n] = tmpArr[n];
        }
    }
}
