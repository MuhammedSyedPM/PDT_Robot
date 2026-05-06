// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdLoadSetup extends NurCmd
{
    public static final int CMD = 34;
    private NurSetup mSetup;
    private int mSetupFlags;
    private int mRetFlags;
    private NurApi mApi;
    
    public NurSetup getResponse() {
        return this.mSetup;
    }
    
    public int getSetupFlags() {
        return this.mSetupFlags;
    }
    
    public int getRetFlags() {
        return this.mRetFlags;
    }
    
    static int calculatePayloadSize(final int flags) {
        int ret = 4;
        if ((flags & 0x1) != 0x0) {
            ret += 4;
        }
        if ((flags & 0x2) != 0x0) {
            ++ret;
        }
        if ((flags & 0x4) != 0x0) {
            ++ret;
        }
        if ((flags & 0x8) != 0x0) {
            ++ret;
        }
        if ((flags & 0x10) != 0x0) {
            ++ret;
        }
        if ((flags & 0x20) != 0x0) {
            ++ret;
        }
        if ((flags & 0x40) != 0x0) {
            ++ret;
        }
        if ((flags & 0x80) != 0x0) {
            ++ret;
        }
        if ((flags & 0x100) != 0x0) {
            ++ret;
        }
        if ((flags & 0x200) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x400) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x800) != 0x0) {
            ++ret;
        }
        if ((flags & 0x1000) != 0x0) {
            ret += 4;
        }
        if ((flags & 0x2000) != 0x0) {
            ++ret;
        }
        if ((flags & 0x4000) != 0x0) {
            ++ret;
        }
        if ((flags & 0x8000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x10000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x20000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x40000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x80000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x100000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x200000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x400000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x800000) != 0x0) {
            ret += 4;
        }
        if ((flags & 0x1000000) != 0x0) {
            ret += 4;
        }
        if ((flags & 0x2000000) != 0x0) {
            ret += 4;
        }
        if ((flags & 0x4000000) != 0x0) {
            ret += 2;
        }
        if ((flags & 0x8000000) != 0x0) {
            ret += 32;
        }
        if ((flags & 0x10000000) != 0x0) {
            ++ret;
        }
        if ((flags & 0x20000000) != 0x0) {
            ++ret;
        }
        return ret;
    }
    
    public NurCmdLoadSetup(final NurSetup setup, final int flags) {
        super(34, 0, calculatePayloadSize(flags));
        this.mApi = null;
        this.mSetup = setup;
        this.mSetupFlags = flags;
    }
    
    public NurCmdLoadSetup(final NurSetup setup, final int flags, final NurApi api) {
        super(34, 0, calculatePayloadSize(flags));
        this.mApi = null;
        this.mSetup = setup;
        this.mSetupFlags = flags;
        this.mApi = api;
    }
    
    public NurCmdLoadSetup(final int flags) {
        this(null, flags);
    }
    
    public NurCmdLoadSetup() {
        this(null, 0);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        final int origOffset = offset;
        if (this.mSetupFlags == 0) {
            return offset - origOffset;
        }
        offset += NurPacket.PacketDword(data, offset, this.mSetupFlags);
        if (this.mSetup == null) {
            return offset - origOffset;
        }
        if ((this.mSetupFlags & 0x1) != 0x0) {
            offset += NurPacket.PacketDword(data, offset, this.mSetup.linkFreq);
        }
        if ((this.mSetupFlags & 0x2) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.rxDecoding);
        }
        if ((this.mSetupFlags & 0x4) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.txLevel);
        }
        if ((this.mSetupFlags & 0x8) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.txModulation);
        }
        if ((this.mSetupFlags & 0x10) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.regionId);
        }
        if ((this.mSetupFlags & 0x20) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryQ);
        }
        if ((this.mSetupFlags & 0x40) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventorySession);
        }
        if ((this.mSetupFlags & 0x80) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryRounds);
        }
        if ((this.mSetupFlags & 0x100) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.antennaMask);
        }
        if ((this.mSetupFlags & 0x200) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.scanSingleTriggerTimeout);
        }
        if ((this.mSetupFlags & 0x400) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.inventoryTriggerTimeout);
        }
        if ((this.mSetupFlags & 0x800) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.selectedAntenna);
        }
        if ((this.mSetupFlags & 0x1000) != 0x0) {
            offset += NurPacket.PacketDword(data, offset, this.mSetup.opFlags);
        }
        if ((this.mSetupFlags & 0x2000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryTarget);
        }
        if ((this.mSetupFlags & 0x4000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryEpcLength);
        }
        if ((this.mSetupFlags & 0x8000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.readRssiFilter.min);
            offset += NurPacket.PacketByte(data, offset, this.mSetup.readRssiFilter.max);
        }
        if ((this.mSetupFlags & 0x10000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.writeRssiFilter.min);
            offset += NurPacket.PacketByte(data, offset, this.mSetup.writeRssiFilter.max);
        }
        if ((this.mSetupFlags & 0x20000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryRssiFilter.min);
            offset += NurPacket.PacketByte(data, offset, this.mSetup.inventoryRssiFilter.max);
        }
        if ((this.mSetupFlags & 0x40000) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.readTimeout);
        }
        if ((this.mSetupFlags & 0x80000) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.writeTimeout);
        }
        if ((this.mSetupFlags & 0x100000) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.lockTimeout);
        }
        if ((this.mSetupFlags & 0x200000) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.killTimeout);
        }
        if ((this.mSetupFlags & 0x400000) != 0x0) {
            offset += NurPacket.PacketWord(data, offset, this.mSetup.periodSetup);
        }
        if ((this.mSetupFlags & 0x800000) != 0x0) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, this.mSetup.antPower[i]);
            }
        }
        if ((this.mSetupFlags & 0x1000000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.powerOffset);
            for (int i = 1; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        if ((this.mSetupFlags & 0x2000000) != 0x0) {
            offset += NurPacket.PacketDword(data, offset, this.mSetup.antennaMaskEx);
        }
        if ((this.mSetupFlags & 0x4000000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.autotune.mode);
            offset += NurPacket.PacketByte(data, offset, this.mSetup.autotune.thresholddBm);
        }
        if ((this.mSetupFlags & 0x8000000) != 0x0) {
            for (int i = 0; i < 32; ++i) {
                offset += NurPacket.PacketByte(data, offset, this.mSetup.antPowerEx[i]);
            }
        }
        if ((this.mSetupFlags & 0x10000000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.rxSensitivity);
        }
        if ((this.mSetupFlags & 0x20000000) != 0x0) {
            offset += NurPacket.PacketByte(data, offset, this.mSetup.rfProfile);
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        final int retFlags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        boolean setupSet = true;
        if (this.mSetup == null) {
            setupSet = false;
            this.mSetup = new NurSetup();
        }
        if ((retFlags & 0x1) != 0x0) {
            this.mSetup.linkFreq = NurPacket.BytesToDword(data, offset);
            offset += 4;
            final NurSetup mSetup = this.mSetup;
            mSetup.flags |= 0x1;
        }
        if ((retFlags & 0x2) != 0x0) {
            this.mSetup.rxDecoding = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup2 = this.mSetup;
            mSetup2.flags |= 0x2;
        }
        if ((retFlags & 0x4) != 0x0) {
            this.mSetup.txLevel = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup3 = this.mSetup;
            mSetup3.flags |= 0x4;
        }
        if ((retFlags & 0x8) != 0x0) {
            this.mSetup.txModulation = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup4 = this.mSetup;
            mSetup4.flags |= 0x8;
        }
        if ((retFlags & 0x10) != 0x0) {
            this.mSetup.regionId = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup5 = this.mSetup;
            mSetup5.flags |= 0x10;
        }
        if ((retFlags & 0x20) != 0x0) {
            this.mSetup.inventoryQ = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup6 = this.mSetup;
            mSetup6.flags |= 0x20;
        }
        if ((retFlags & 0x40) != 0x0) {
            this.mSetup.inventorySession = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup7 = this.mSetup;
            mSetup7.flags |= 0x40;
        }
        if ((retFlags & 0x80) != 0x0) {
            this.mSetup.inventoryRounds = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup8 = this.mSetup;
            mSetup8.flags |= 0x80;
        }
        if ((retFlags & 0x100) != 0x0) {
            this.mSetup.antennaMask = NurPacket.BytesToByte(data, offset);
            ++offset;
            final NurSetup mSetup9 = this.mSetup;
            mSetup9.flags |= 0x100;
        }
        if ((retFlags & 0x200) != 0x0) {
            this.mSetup.scanSingleTriggerTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
            final NurSetup mSetup10 = this.mSetup;
            mSetup10.flags |= 0x200;
        }
        if ((retFlags & 0x400) != 0x0) {
            this.mSetup.inventoryTriggerTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
            final NurSetup mSetup11 = this.mSetup;
            mSetup11.flags |= 0x400;
        }
        if ((retFlags & 0x800) != 0x0) {
            final byte antTmp = (byte)NurPacket.BytesToByte(data, offset);
            if (antTmp == 255) {
                this.mSetup.selectedAntenna = -1;
            }
            else {
                this.mSetup.selectedAntenna = antTmp;
            }
            ++offset;
            final NurSetup mSetup12 = this.mSetup;
            mSetup12.flags |= 0x800;
        }
        if ((retFlags & 0x1000) != 0x0) {
            this.mSetup.opFlags = NurPacket.BytesToDword(data, offset);
            offset += 4;
        }
        if ((retFlags & 0x2000) != 0x0) {
            this.mSetup.inventoryTarget = NurPacket.BytesToByte(data, offset);
            ++offset;
        }
        if ((retFlags & 0x4000) != 0x0) {
            this.mSetup.inventoryEpcLength = NurPacket.BytesToByte(data, offset);
            ++offset;
        }
        if ((retFlags & 0x8000) != 0x0) {
            this.mSetup.readRssiFilter.min = data[offset];
            this.mSetup.readRssiFilter.max = data[offset + 1];
            offset += 2;
        }
        if ((retFlags & 0x10000) != 0x0) {
            this.mSetup.writeRssiFilter.min = data[offset];
            this.mSetup.writeRssiFilter.max = data[offset + 1];
            offset += 2;
        }
        if ((retFlags & 0x20000) != 0x0) {
            this.mSetup.inventoryRssiFilter.min = data[offset];
            this.mSetup.inventoryRssiFilter.max = data[offset + 1];
            offset += 2;
        }
        if ((retFlags & 0x40000) != 0x0) {
            this.mSetup.readTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
        }
        if ((retFlags & 0x80000) != 0x0) {
            this.mSetup.writeTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
        }
        if ((retFlags & 0x100000) != 0x0) {
            this.mSetup.lockTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
        }
        if ((retFlags & 0x200000) != 0x0) {
            this.mSetup.killTimeout = NurPacket.BytesToWord(data, offset);
            offset += 2;
        }
        if ((retFlags & 0x400000) != 0x0) {
            this.mSetup.periodSetup = NurPacket.BytesToWord(data, offset);
            offset += 2;
        }
        if ((retFlags & 0x800000) != 0x0) {
            for (int i = 0; i < 4; ++i) {
                this.mSetup.antPower[i] = data[offset + i];
            }
            offset += 4;
        }
        if ((retFlags & 0x1000000) != 0x0) {
            final byte b = (byte)NurPacket.BytesToByte(data, offset);
            if (b == 255) {
                this.mSetup.powerOffset = -1;
            }
            else {
                this.mSetup.powerOffset = b;
            }
            offset += 4;
        }
        if ((retFlags & 0x2000000) != 0x0) {
            this.mSetup.antennaMaskEx = NurPacket.BytesToDword(data, offset);
            offset += 4;
        }
        if ((retFlags & 0x4000000) != 0x0) {
            this.mSetup.autotune = new AutotuneSetup(data[offset], data[offset + 1]);
            offset += 2;
        }
        if ((retFlags & 0x8000000) != 0x0) {
            if (this.mSetup.antPowerEx == null) {
                this.mSetup.antPowerEx = new int[32];
            }
            for (int i = 0; i < 32; ++i) {
                this.mSetup.antPowerEx[i] = data[offset + i];
            }
            offset += 32;
        }
        if ((retFlags & 0x10000000) != 0x0) {
            this.mSetup.rxSensitivity = data[offset++];
        }
        if ((retFlags & 0x20000000) != 0x0) {
            this.mSetup.rfProfile = data[offset++];
        }
        this.mRetFlags = retFlags;
        final NurSetup mSetup13 = this.mSetup;
        mSetup13.flags |= retFlags;
        if (this.mSetupFlags != 0 && this.mSetupFlags != this.mRetFlags) {
            this.mOwner.ELog("Could not " + (setupSet ? "set" : "get") + " all settings; flags " + this.mSetupFlags + "; returned flags " + this.mRetFlags);
        }
    }
}
