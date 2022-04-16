package com.proje.steganography;

public class Chat {
    private long timestamp;
    private boolean seen;
    public Chat() { }
    public Chat(long timestamp, boolean seen) {
        this.timestamp = timestamp;
        this.seen = seen;
    }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
