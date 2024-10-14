package com.example.redrestaurantapp.Models;

public class CartRecord {
    private long id;
    private Product product;
    private long quantity;
    private double total;

    public CartRecord() {}

    public CartRecord(long id, Product product, long quantity, double total) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.total = total;
    }

    public CartRecord(Product product, long quantity, double total) {
        this.product = product;
        this.quantity = quantity;
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CartRecord{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", total=" + total +
                '}';
    }
}
