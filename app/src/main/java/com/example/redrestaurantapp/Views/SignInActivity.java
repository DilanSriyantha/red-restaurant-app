package com.example.redrestaurantapp.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private final String TAG = "SignInActivity";
    private final int REQ_ONE_TAP = 0x00001;
    private boolean showOneTapUI = true;

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private CardView btnSignInWithGoogle;
    private ConstraintLayout mProgressOverlay;

    private boolean FLAG_PROGRESSING = false;

    public SignInActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        mProgressOverlay = findViewById(R.id.progressOverlay);

        btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);
        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOneTapSignIn();
            }
        });
    }

    private void displayOneTapSignIn() {
        setProgressing(true);

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        try{
                            startIntentSenderForResult(
                                    beginSignInResult.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0
                            );
                        }catch (Exception ex){
                            Log.d(TAG, "Couldn't start One Tap UI: " + ex.getLocalizedMessage());

                            AlertBox alertBox = new AlertBox(ex.getLocalizedMessage(), AlertBox.Type.ERROR);
                            alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                                @Override
                                public void onClick() {
                                    alertBox.dismiss();
                                }
                            }, null);

                            setProgressing(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure : " + e.getLocalizedMessage());

                        AlertBox alertBox = new AlertBox(e.getLocalizedMessage(), AlertBox.Type.ERROR);
                        alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                            @Override
                            public void onClick() {
                                alertBox.dismiss();
                            }
                        }, null);

                        setProgressing(false);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try{
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if(idToken != null){
                    Log.d(TAG, "Got ID token");

                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "signInWithCredential:success");

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        navigateToHome();
                                    }else{
                                        Log.d(TAG, "signInWithCredential:failure", task.getException());

                                        setProgressing(false);
                                    }
                                }
                            });
                }
            }catch (Exception ex){
                Log.d(TAG, "Exception: " + ex.getLocalizedMessage());

                AlertBox alertBox = new AlertBox(ex.getLocalizedMessage(), AlertBox.Type.ERROR);
                alertBox.show(getSupportFragmentManager(), TAG, new AlertBox.Action() {
                    @Override
                    public void onClick() {
                        alertBox.dismiss();
                    }
                }, null);

                setProgressing(false);
            }
        }
    }

    private void setProgressing(boolean state){
        FLAG_PROGRESSING = state;
        if(FLAG_PROGRESSING)
            mProgressOverlay.setVisibility(View.VISIBLE);
        else
            mProgressOverlay.setVisibility(View.GONE);
    }

    private void navigateToHome() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);

        setProgressing(false);
        finish();
    }
}