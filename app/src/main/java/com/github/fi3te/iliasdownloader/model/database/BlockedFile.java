package com.github.fi3te.iliasdownloader.model.database;

/**
 * Created by wennier on 11.09.2015.
 */
public class BlockedFile {

    private final long refId;
    private final String filename;
    private final long fileSize;

    public BlockedFile(long refId, String filename, long fileSize) {
        this.refId = refId;
        this.filename = filename;
        this.fileSize = fileSize;
    }

    public long getRefId() {
        return refId;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

}
