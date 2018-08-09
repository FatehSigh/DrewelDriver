package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ProductModel implements Parcelable {
    public String product_id;
    public String quantity;
    public String product_name,ar_product_name;
    public String product_price;
    public String product_image;
    public List<CategoryModel> Category;


    protected ProductModel(Parcel in) {
        product_id = in.readString();
        quantity = in.readString();
        product_name = in.readString();
        ar_product_name = in.readString();
        product_price = in.readString();
        product_image = in.readString();
        Category = in.createTypedArrayList(CategoryModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(quantity);
        dest.writeString(product_name);
        dest.writeString(ar_product_name);
        dest.writeString(product_price);
        dest.writeString(product_image);
        dest.writeTypedList(Category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };
}
