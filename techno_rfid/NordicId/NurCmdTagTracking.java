// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdTagTracking extends NurCmd
{
    public static final int CMD = 69;
    private NurTagTrackingConfig mConfig;
    private int mQ;
    private int mRounds;
    private int mScanUntilNewTagsCount;
    private int mAntennaMask;
    private boolean mIsSimple;
    private boolean mStopTT;
    public static final int SZ_COMMAND_START = 5;
    public static final int SZ_PARAMS = 8;
    public static final int SZ_COMMAND_FILTER_PART = 568;
    
    private static int calculateParameterSize(final NurTagTrackingConfig cfg) {
        int requiredBytes = 5;
        if ((cfg.flags & 0x2) != 0x0) {
            requiredBytes += 8;
            requiredBytes += 9;
            requiredBytes += NurApi.bitLenToByteLen(cfg.selectMaskBitLength);
        }
        else {
            requiredBytes += 8;
            for (final NurInventoryExtendedFilter filter : cfg.complexFilters) {
                requiredBytes += 9 + NurApi.bitLenToByteLen(filter.maskBitLength);
            }
        }
        return requiredBytes;
    }
    
    public NurCmdTagTracking(final boolean stop) {
        super(69, stop ? 0 : 1);
        this.mConfig = null;
        this.mQ = 0;
        this.mRounds = 0;
        this.mScanUntilNewTagsCount = 0;
        this.mAntennaMask = 0;
        this.mIsSimple = false;
        this.mStopTT = false;
        this.mStopTT = stop;
    }
    
    public NurCmdTagTracking(final NurTagTrackingConfig cfg, final int Q, final int rounds, final int scanUntilNewTagsCount) {
        super(69, 0, calculateParameterSize(cfg));
        this.mConfig = null;
        this.mQ = 0;
        this.mRounds = 0;
        this.mScanUntilNewTagsCount = 0;
        this.mAntennaMask = 0;
        this.mIsSimple = false;
        this.mStopTT = false;
        this.mConfig = cfg;
        this.mQ = Q;
        this.mRounds = rounds;
        this.mScanUntilNewTagsCount = scanUntilNewTagsCount;
        this.mIsSimple = ((this.mConfig.flags & 0x2) != 0x0);
        if ((cfg.events & 0x18) != 0x0) {
            this.mAntennaMask = 3;
        }
        else {
            this.mAntennaMask = 1;
        }
    }
    
    private int serializeSimple(final byte[] data, final int offset) {
        int ptr = offset;
        ptr += NurPacket.PacketByte(data, ptr, this.mQ);
        ptr += NurPacket.PacketByte(data, ptr, 0);
        ptr += NurPacket.PacketByte(data, ptr, this.mRounds);
        ptr += NurPacket.PacketWord(data, ptr, 0);
        ptr += NurPacket.PacketByte(data, ptr, 3);
        ptr += NurPacket.PacketByte(data, ptr, 0);
        ptr += NurPacket.PacketByte(data, ptr, 1);
        ptr += NurPacket.PacketByte(data, ptr, 0);
        ptr += NurPacket.PacketByte(data, ptr, 4);
        ptr += NurPacket.PacketByte(data, ptr, 0);
        ptr += NurPacket.PacketByte(data, ptr, this.mConfig.selectBank);
        ptr += NurPacket.PacketDword(data, ptr, this.mConfig.selectAddress);
        ptr += NurPacket.PacketByte(data, ptr, this.mConfig.selectMaskBitLength);
        if (this.mConfig.selectMaskBitLength > 0) {
            ptr += NurPacket.PacketBytes(data, ptr, this.mConfig.selectMask, NurApi.bitLenToByteLen(this.mConfig.selectMaskBitLength));
        }
        return ptr - offset;
    }
    
    private int serializeFilter(final byte[] data, final int offset, final NurInventoryExtendedFilter filter) {
        int ptr = offset;
        ptr += NurPacket.PacketByte(data, ptr, filter.truncate ? 1 : 0);
        ptr += NurPacket.PacketByte(data, ptr, filter.targetSession);
        ptr += NurPacket.PacketByte(data, ptr, filter.action);
        ptr += NurPacket.PacketByte(data, ptr, filter.bank);
        ptr += NurPacket.PacketDword(data, ptr, filter.address);
        ptr += NurPacket.PacketByte(data, ptr, filter.maskBitLength);
        if (filter.maskBitLength > 0) {
            ptr += NurPacket.PacketBytes(data, ptr, filter.maskdata, NurApi.bitLenToByteLen(filter.maskBitLength));
        }
        return 568;
    }
    
    private int serializeConfig(final byte[] data, final int offset) {
        int ptr = offset;
        ptr += NurPacket.PacketDword(data, ptr, this.mAntennaMask);
        ptr += NurPacket.PacketByte(data, ptr, this.mScanUntilNewTagsCount);
        if (this.mIsSimple) {
            this.serializeSimple(data, ptr);
        }
        else {
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilterParams.Q);
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilterParams.session);
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilterParams.rounds);
            ptr += NurPacket.PacketWord(data, ptr, this.mConfig.complexFilterParams.transitTime);
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilterParams.inventorySelState);
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilterParams.inventoryTarget);
            ptr += NurPacket.PacketByte(data, ptr, this.mConfig.complexFilters.length);
            for (final NurInventoryExtendedFilter filter : this.mConfig.complexFilters) {
                ptr += this.serializeFilter(data, ptr, filter);
            }
        }
        return ptr - offset;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        if (this.mConfig != null) {
            offset += this.serializeConfig(data, offset);
        }
        else if (this.mStopTT) {
            offset += NurPacket.PacketByte(data, offset, 0);
        }
        else {
            offset += NurPacket.PacketByte(data, offset, 1);
        }
        return offset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        final int error = this.getStatus();
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
}
