package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CountryModel implements Parcelable {
    public String id;
    public String sortname;
    public String name;
    public String phonecode;

    @Override
    public String toString() {
        return "CountryModel{" +
                "id='" + id + '\'' +
                ", sortname='" + sortname + '\'' +
                ", name='" + name + '\'' +
                ", phonecode='" + phonecode + '\'' +
                '}';
    }

    protected CountryModel(Parcel in) {
        id = in.readString();
        sortname = in.readString();
        name = in.readString();
        phonecode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sortname);
        dest.writeString(name);
        dest.writeString(phonecode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CountryModel> CREATOR = new Creator<CountryModel>() {
        @Override
        public CountryModel createFromParcel(Parcel in) {
            return new CountryModel(in);
        }

        @Override
        public CountryModel[] newArray(int size) {
            return new CountryModel[size];
        }
    };
}
