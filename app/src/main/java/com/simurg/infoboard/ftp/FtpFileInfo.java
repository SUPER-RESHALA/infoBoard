package com.simurg.infoboard.ftp;

import java.util.Date;

public class FtpFileInfo {
    private long size;
    private  Date modificationTime;

    public FtpFileInfo(long size, Date modificationTime) {
        this.size = size;
        this.modificationTime = modificationTime;
    }

    public long getSize() {
        return size;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    @Override
    public String toString() {
        return "Size: " + size + " bytes, Modified: " + modificationTime;
    }
}
