package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderUserModel implements Parcelable {
    public String id;
    public String name;
    public String mobile_number;

    protected OrderUserModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        mobile_number = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(mobile_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderUserModel> CREATOR = new Creator<OrderUserModel>() {
        @Override
        public OrderUserModel createFromParcel(Parcel in) {
            return new OrderUserModel(in);
        }

        @Override
        public OrderUserModel[] newArray(int size) {
            return new OrderUserModel[size];
        }
    };

    @Override
    public String toString() {
        return "OrderUserModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mobile_number='" + mobile_number + '\'' +
                '}';
    }
}
