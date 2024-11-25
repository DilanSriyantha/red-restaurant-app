package com.example.redrestaurantapp.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Utils.ImageLoader;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountActivity extends AppCompatActivity {
    ImageButton mBtnBack;
    ImageButton mBtnNotifications;
    ImageButton mBtnCart;

    TextView mTxtTitle;

    ImageView mImgUser;

    TextView mTxtName;
    TextView mTxtPhoneNumber;
    TextView mTxtEmail;

    UserManager mUserManager;

    public ManageAccountActivity() {
        mUserManager = new UserManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBtnBack = findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(this::onBackClick);

        mBtnNotifications = findViewById(R.id.btnNotifications);
        mBtnNotifications.setVisibility(View.GONE);

        mBtnCart = findViewById(R.id.btnCart);
        mBtnCart.setVisibility(View.GONE);

        mTxtTitle = findViewById(R.id.txtAppbarTitle);
        mTxtTitle.setText("Manage Account");

        mImgUser = findViewById(R.id.imgUser);

        mTxtName = findViewById(R.id.txtUserName);
        mTxtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        mTxtEmail = findViewById(R.id.txtEmail);

        populateFields();
    }

    private void populateFields(){
        if(mUserManager == null) return;

        FirebaseUser user = mUserManager.getCurrentUser();
        if(user == null) return;

        ImageLoader.getInstance(this).loadImage(mImgUser, user.getPhotoUrl());

        if(user.getDisplayName() == null || user.getDisplayName().isEmpty())
            mTxtName.setText("Not found!");
        mTxtName.setText(user.getDisplayName());

        if(user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty())
            mTxtPhoneNumber.setText("Not found!");
        mTxtPhoneNumber.setText(user.getPhoneNumber());

        if(user.getEmail() == null || user.getEmail().isEmpty())
            mTxtEmail.setText("Not found!");
        mTxtEmail.setText(user.getEmail());
    }

    private void onBackClick(View v){
        finish();
    }
}