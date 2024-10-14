package com.example.redrestaurantapp.ServiceLayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        if(mUser == null)
            mUser = mAuth.getCurrentUser();

        return mUser;
    }

    public boolean isValidUser() {
        return getCurrentUser() != null;
    }
}
