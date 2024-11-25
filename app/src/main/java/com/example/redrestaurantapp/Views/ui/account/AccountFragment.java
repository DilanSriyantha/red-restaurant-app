package com.example.redrestaurantapp.Views.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ImageLoader;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Views.ManageAccountActivity;
import com.example.redrestaurantapp.Views.SignInActivity;
import com.example.redrestaurantapp.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;

    private UserManager mUser;

    private ConstraintLayout mBtnManageAccount;
    private ConstraintLayout mBtnPrivacy;
    private ConstraintLayout mBtnAbout;
    private ConstraintLayout mBtnLogout;
    private ConstraintLayout mProgressOverlay;


    private TextView mTxtUserName;

    private ImageView mUserImage;

    private ImageLoader mImageLoader;

    private boolean FLAG_PROGRESSING = false;

    public AccountFragment() {
        mUser = new UserManager();
        mImageLoader = ImageLoader.getInstance(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!mUser.isValidUser())
            navigateToSignIn();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mProgressOverlay = root.findViewById(R.id.progressOverlay);

        mBtnManageAccount = root.findViewById(R.id.btnManageAccount);
        mBtnManageAccount.setOnClickListener(this::onManageAccountClick);

        mBtnPrivacy = root.findViewById(R.id.btnPrivacy);
        mBtnPrivacy.setOnClickListener(this::onPrivacyClick);

        mBtnAbout = root.findViewById(R.id.btnAbout);
        mBtnAbout.setOnClickListener(this::onAboutClick);

        mBtnLogout = root.findViewById(R.id.btnLogout);
        mBtnLogout.setOnClickListener(this::onLogoutClick);

        mTxtUserName = root.findViewById(R.id.txtUserName);
        mTxtUserName.setText(mUser.getCurrentUser().getDisplayName());
        mTxtUserName.setSelected(true);

        mUserImage = root.findViewById(R.id.imgUserImage);
        mImageLoader.loadImage(mUserImage, mUser.getCurrentUser().getPhotoUrl());

        return root;
    }

    private void navigateToSignIn() {
        Intent singInActivity = new Intent(getContext(), SignInActivity.class);
        startActivity(singInActivity);

        if(FLAG_PROGRESSING)
            setProgressing(false);
    }

    private void setProgressing(boolean state){
        FLAG_PROGRESSING = state;
        if(FLAG_PROGRESSING)
            mProgressOverlay.setVisibility(View.VISIBLE);
        else
            mProgressOverlay.setVisibility(View.GONE);
    }

    private void onManageAccountClick(View v){
        Intent manageAccountActivity = new Intent(getActivity(), ManageAccountActivity.class);
        startActivity(manageAccountActivity);
    }

    private void onLogoutClick(View v){
        setProgressing(true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth.getInstance().signOut();
                navigateToSignIn();
            }
        }, 1000);
    }

    private void onAboutClick(View v){
        Uri link = Uri.parse("https://pastebin.com/raw/VtUiVxrN");
        Intent viewLink = new Intent(Intent.ACTION_VIEW, link);
        startActivity(viewLink);
    }

    private void onPrivacyClick(View v){
        Uri link = Uri.parse("https://pastebin.com/raw/r83qPZRK");
        Intent viewLink = new Intent(Intent.ACTION_VIEW, link);
        startActivity(viewLink);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}