package com.example.redrestaurantapp.Models;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private long id;
    private String key;
    private String title;
    private String to;
    private String from;
    private String message;
    private long timestamp;
    private boolean seen;

    public Notification() {}

    public Notification(int id, String key, String title, String to, String from, String message, long timestamp, boolean seen) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.to = to;
        this.from = from;
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("to", to);
        result.put("from", from);
        result.put("message", message);
        result.put("timestamp", timestamp);
        result.put("seen", seen);

        return result;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", seen=" + seen +
                '}';
    }
}
