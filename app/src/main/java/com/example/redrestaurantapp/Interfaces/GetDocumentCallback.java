package com.example.redrestaurantapp.Interfaces;

public interface GetDocumentCallback <T> {
    void onSuccess(T object);
    void onFailure(Exception ex);
}
