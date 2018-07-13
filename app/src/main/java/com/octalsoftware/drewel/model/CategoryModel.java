package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryModel implements Parcelable {
    public String id;
    public String category_name;
    public String img;

    protected CategoryModel(Parcel in) {
        id = in.readString();
        category_name = in.readString();
        img = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category_name);
        dest.writeString(img);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id='" + id + '\'' +
                ", category_name='" + category_name + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
