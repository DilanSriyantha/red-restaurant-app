package com.example.redrestaurantapp.Interfaces;

import java.util.List;

public interface GetCollectionCallback <T> {
    void onSuccess(List<T> resultList);
    void onFailure(Exception ex);
}
