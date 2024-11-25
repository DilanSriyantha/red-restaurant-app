package com.example.redrestaurantapp.Views.BottomSheetDialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.redrestaurantapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BuildingTypesBottomSheet extends BottomSheetDialogFragment {
    private final String TAG = "BuildingTypesBottomSheet";

    ConstraintLayout mOptHouse;
    ConstraintLayout mOptApartment;
    ConstraintLayout mOptOffice;
    ConstraintLayout mOptHotel;
    ConstraintLayout mOptOther;

    ImageView mChkHouse;
    ImageView mChkApartment;
    ImageView mChkOffice;
    ImageView mChkHotel;
    ImageView mChkOther;

    String mSelectedOption = "Other";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.building_types_bottom_sheet, container, false);

        mOptHouse = v.findViewById(R.id.optHouse);
        mOptHouse.setOnClickListener(this::onOptHouseClick);

        mOptApartment = v.findViewById(R.id.optApartment);
        mOptApartment.setOnClickListener(this::onOptApartmentClick);

        mOptOffice = v.findViewById(R.id.optOffice);
        mOptOffice.setOnClickListener(this::onOptOfficeClick);

        mOptHotel = v.findViewById(R.id.optHotel);
        mOptHotel.setOnClickListener(this::onOptHotelClick);

        mOptOther = v.findViewById(R.id.optOther);
        mOptOther.setOnClickListener(this::onOptOtherClick);

        mChkHouse = v.findViewById(R.id.chkHouse);
        mChkApartment = v.findViewById(R.id.chkApartment);
        mChkOffice = v.findViewById(R.id.chkOffice);
        mChkHotel = v.findViewById(R.id.chkHotel);
        mChkOther = v.findViewById(R.id.chkOther);

        return v;
    }

    private void onOptHouseClick(View v){
        selectOption(Options.HOUSE);
    }

    private void onOptApartmentClick(View v){
        selectOption(Options.APARTMENT);
    }

    private void onOptOfficeClick(View v){
        selectOption(Options.OFFICE);
    }

    private void onOptHotelClick(View v){
        selectOption(Options.HOTEL);
    }

    private void onOptOtherClick(View v){
        selectOption(Options.OTHER);
    }

    private void selectOption(Options option) {
        switch (option){
            case HOUSE:
                mChkHouse.setVisibility(View.VISIBLE);
                mChkApartment.setVisibility(View.GONE);
                mChkOffice.setVisibility(View.GONE);
                mChkHotel.setVisibility(View.GONE);
                mChkOther.setVisibility(View.GONE);
                mSelectedOption = "House";
                break;
            case APARTMENT:
                mChkHouse.setVisibility(View.GONE);
                mChkApartment.setVisibility(View.VISIBLE);
                mChkOffice.setVisibility(View.GONE);
                mChkHotel.setVisibility(View.GONE);
                mChkOther.setVisibility(View.GONE);
                mSelectedOption = "Apartment";
                break;
            case OFFICE:
                mChkHouse.setVisibility(View.GONE);
                mChkApartment.setVisibility(View.GONE);
                mChkOffice.setVisibility(View.VISIBLE);
                mChkHotel.setVisibility(View.GONE);
                mChkOther.setVisibility(View.GONE);
                mSelectedOption = "Office";
                break;
            case HOTEL:
                mChkHouse.setVisibility(View.GONE);
                mChkApartment.setVisibility(View.GONE);
                mChkOffice.setVisibility(View.GONE);
                mChkHotel.setVisibility(View.VISIBLE);
                mChkOther.setVisibility(View.GONE);
                mSelectedOption = "Hotel";
                break;
            case OTHER:
                mChkHouse.setVisibility(View.GONE);
                mChkApartment.setVisibility(View.GONE);
                mChkOffice.setVisibility(View.GONE);
                mChkHotel.setVisibility(View.GONE);
                mChkOther.setVisibility(View.VISIBLE);
                mSelectedOption = "Other";
                break;
            default:
                Log.d(TAG, "Invalid option type");
        }
    }

    public enum Options {
        HOUSE,
        APARTMENT,
        OFFICE,
        HOTEL,
        OTHER
    };
}
