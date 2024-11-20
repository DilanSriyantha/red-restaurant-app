package com.example.redrestaurantapp.Models;

public class Notification {
    private int id;
    private String title;
    private String to;
    private String from;
    private String message;
    private long timestamp;
    private boolean seen;

    public Notification() {}

    public Notification(int id, String title, String to, String from, String message, long timestamp, boolean seen) {
        this.id = id;
        this.title = title;
        this.to = to;
        this.from = from;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", seen=" + seen +
                '}';
    }
}
