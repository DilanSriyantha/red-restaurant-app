package com.example.redrestaurantapp.Models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Order {
    private long id;
    private String userId;
    private String imageUrl;
    private boolean utensilsAllowed;
    private String note;
    private List<CartRecord> cartRecords;
    private double subtotal;
    private Timestamp timestamp;
    private String status;

    public Order() {}

    public Order(long id, String userId, String imageUrl, boolean utensilsAllowed, String note, List<CartRecord> cartRecords, double subtotal, Timestamp timestamp, String status) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.utensilsAllowed = utensilsAllowed;
        this.note = note;
        this.cartRecords = cartRecords;
        this.subtotal = subtotal;
        this.timestamp = timestamp;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isUtensilsAllowed() {
        return utensilsAllowed;
    }

    public void setUtensilsAllowed(boolean utensilsAllowed) {
        this.utensilsAllowed = utensilsAllowed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<CartRecord> getCartRecords() {
        return cartRecords;
    }

    public void setCartRecords(List<CartRecord> cartRecords) {
        this.cartRecords = cartRecords;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", utensilsAllowed=" + utensilsAllowed +
                ", note='" + note + '\'' +
                ", cartRecords=" + cartRecords +
                ", subtotal=" + subtotal +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
