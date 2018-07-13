package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDataModel implements Parcelable {
    public String user_id;
    public String first_name;
    public String last_name;
    public String email;
    public String role_id;
    public String mobile_number;
    public String country_code;
    public String user_code;
    public String vehicle_number;

    public String latitude;
    public String longitude;
    public String is_notification;
    public String fb_id;
    public String remember_token;
    public String modified;
    public String is_mobileverify;
    public String img;
    public String address;
    public String address_name;
    public String address_latitude;
    public String address_longitude;
    public String zip_code;
    public String cart_id;
    public String cart_quantity;


    protected UserDataModel(Parcel in) {
        user_id = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        email = in.readString();
        role_id = in.readString();
        mobile_number = in.readString();
        country_code = in.readString();
        user_code = in.readString();
        vehicle_number = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        is_notification = in.readString();
        fb_id = in.readString();
        remember_token = in.readString();
        modified = in.readString();
        is_mobileverify = in.readString();
        img = in.readString();
        address = in.readString();
        address_name = in.readString();
        address_latitude = in.readString();
        address_longitude = in.readString();
        zip_code = in.readString();
        cart_id = in.readString();
        cart_quantity = in.readString();
    }

    public static final Creator<UserDataModel> CREATOR = new Creator<UserDataModel>() {
        @Override
        public UserDataModel createFromParcel(Parcel in) {
            return new UserDataModel(in);
        }

        @Override
        public UserDataModel[] newArray(int size) {
            return new UserDataModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(email);
        parcel.writeString(role_id);
        parcel.writeString(mobile_number);
        parcel.writeString(country_code);
        parcel.writeString(user_code);
        parcel.writeString(vehicle_number);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(is_notification);
        parcel.writeString(fb_id);
        parcel.writeString(remember_token);
        parcel.writeString(modified);
        parcel.writeString(is_mobileverify);
        parcel.writeString(img);
        parcel.writeString(address);
        parcel.writeString(address_name);
        parcel.writeString(address_latitude);
        parcel.writeString(address_longitude);
        parcel.writeString(zip_code);
        parcel.writeString(cart_id);
        parcel.writeString(cart_quantity);
    }
}
