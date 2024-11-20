package com.example.redrestaurantapp.Utils;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.redrestaurantapp.Models.CartRecord;
import com.example.redrestaurantapp.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static final String TAG = "Cart";
    private static Cart mInstance;
    private static final List<CartRecord> mCartList = new ArrayList<>();
    private static final List<OnCartCountChange> mCountChangeListeners = new ArrayList<>();

    public Cart() {}

    public static Cart getInstance(){
        if(mInstance == null)
            mInstance = new Cart();

        return mInstance;
    }

    public void add(CartRecord record){
        int id = (int)(Math.random()*10000);
        record.setId(id);
        mCartList.add(record);
        notifyCountChangeListeners();
    }

    public void remove(int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCartList.removeIf((record -> record.getId() == id));
            notifyCountChangeListeners();
        }
    }

    public void remove(CartRecord record){
        mCartList.remove(record);
        notifyCountChangeListeners();
    }

    public int getCartCount() {
        return mCartList.size();
    }

    public CartRecord getRecord(int id) {
        for(CartRecord cr : mCartList){
            if(cr.getId() == id)
                return cr;
        }
        return null;
    }

    public List<CartRecord> getList() {
        return mCartList;
    }

    public void setQty(long id, long newQty){
        for(CartRecord cr : mCartList){
            if(cr.getId() == id){
                cr.setQuantity(newQty);
                cr.setTotal(cr.getProduct().getPrice() * newQty);

                break;
            }
        }
    }

    public void updateCartLabel(TextView cartLabel){
        if(cartLabel == null) return;

        if(getCartCount() == 0){
            cartLabel.setVisibility(View.GONE);
            return;
        }

        if(cartLabel.getVisibility() == View.GONE){
            cartLabel.setVisibility(View.VISIBLE);
            return;
        }

        cartLabel.setText(String.valueOf(getCartCount()));
    }

    public void clear() {
        mCartList.clear();
        notifyCountChangeListeners();
    }

    public void setOnCountChangeListener(OnCartCountChange listener){
        mCountChangeListeners.add(listener);
    }

    public void notifyCountChangeListeners() {
        for(OnCartCountChange listener : mCountChangeListeners){
            listener.onChange(getCartCount());
        }
    }

    public interface OnCartCountChange {
        void onChange(int count);
    }
}
