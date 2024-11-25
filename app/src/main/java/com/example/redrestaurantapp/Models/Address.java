package com.example.redrestaurantapp.Models;

import java.io.Serializable;

public class Address implements Serializable {
    private double latitude;
    private double longitude;
    private String address;
    private String buildingType;
    private String aptFlatFloor;
    private String buildingName;
    private String landmark;
    private String deliveryInstructions;
    private String addressLabel;

    public Address() {}

    public Address(double latitude, double longitude, String address, String buildingType, String aptFlatFloor, String buildingName, String landmark, String deliveryInstructions, String addressLabel) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.buildingType = buildingType;
        this.aptFlatFloor = aptFlatFloor;
        this.buildingName = buildingName;
        this.landmark = landmark;
        this.deliveryInstructions = deliveryInstructions;
        this.addressLabel = addressLabel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public String getAptFlatFloor() {
        return aptFlatFloor;
    }

    public void setAptFlatFloor(String aptFlatFloor) {
        this.aptFlatFloor = aptFlatFloor;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    @Override
    public String toString() {
        return "Address{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", buildingType='" + buildingType + '\'' +
                ", aptFlatFloor='" + aptFlatFloor + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", landmark='" + landmark + '\'' +
                ", deliveryInstructions='" + deliveryInstructions + '\'' +
                ", addressLabel='" + addressLabel + '\'' +
                '}';
    }
}
