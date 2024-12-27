package org.example.models;

public class History {
    private String fileName;
    private String filePath;
    private String transferType;
    private String timestamp;

    public History(String fileName, String filePath, String transferType, String timestamp) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.transferType = transferType;
        this.timestamp = timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + transferType + "] " + fileName + " at " + timestamp;
    }
}
