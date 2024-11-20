package com.example.redrestaurantapp.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.redrestaurantapp.R;

public class AlertBox extends DialogFragment {
    private Action mPositiveAction;
    private Action mNegativeAction;
    private final String mMessage;
    private final Type mType;

    public AlertBox(String message, Type type){
        mMessage = message;
        mType = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        int layoutId = getLayoutId();

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(layoutId, null);
        builder.setView(dialogView)
                .setPositiveButton(R.string.alert_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mPositiveAction != null)
                            mPositiveAction.onClick();
                    }
                })
                .setNegativeButton(R.string.alert_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mNegativeAction != null)
                            mNegativeAction.onClick();

                        dismiss();
                    }
                });

        builder.setCancelable(false);

        setMessage(dialogView);

        return builder.create();
    }

    public void show(@NonNull FragmentManager manager, @Nullable String tag, Action positiveAction, Action negativeAction) {
        mPositiveAction = positiveAction;
        mNegativeAction = negativeAction;

        super.show(manager, tag);
    }

    private int getLayoutId(){
        switch (mType){
            case INFO:
                return R.layout.alert_info;
            case WARNING:
                return R.layout.alert_warning;
            case ERROR:
                return R.layout.alert_error;
            default:
                return -1;
        }
    }

    private void setMessage(View dialogView){
        if(mMessage == null) return;

        TextView alertText = dialogView.findViewById(R.id.alertTxt);
        if(alertText == null) return;

        alertText.setText(mMessage);
    }

    public interface Action {
        void onClick();
    }

    public enum Type {
        ERROR,
        INFO,
        WARNING
    }
}