package com.example.redrestaurantapp.Interfaces;

import androidx.annotation.NonNull;

public interface OnCompleteListener<T> {
    void onSuccess(T unused);
    void onFailure(@NonNull Exception e);
}
