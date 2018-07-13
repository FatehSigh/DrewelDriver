package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderDetailModel implements Parcelable {
    //    public boolean status;
//    public String message;
//    public UserDataModel data;
//    public String is_deactivate;
    public OrderUserModel User;
    public OrderModel Order;
    public List<ProductModel> Products;
    public List<CoupanModel> Coupons;


    protected OrderDetailModel(Parcel in) {
        User = in.readParcelable(OrderUserModel.class.getClassLoader());
        Order = in.readParcelable(OrderModel.class.getClassLoader());
        Products = in.createTypedArrayList(ProductModel.CREATOR);
        Coupons = in.createTypedArrayList(CoupanModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(User, flags);
        dest.writeParcelable(Order, flags);
        dest.writeTypedList(Products);
        dest.writeTypedList(Coupons);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDetailModel> CREATOR = new Creator<OrderDetailModel>() {
        @Override
        public OrderDetailModel createFromParcel(Parcel in) {
            return new OrderDetailModel(in);
        }

        @Override
        public OrderDetailModel[] newArray(int size) {
            return new OrderDetailModel[size];
        }
    };

    @Override
    public String toString() {
        return "OrderDetailModel{" +
                "User=" + User +
                ", Order=" + Order +
                ", Products=" + Products +
                ", Coupons=" + Coupons +
                '}';
    }
}
