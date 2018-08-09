package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderModel implements Parcelable {
    public String order_id;
    public String deliver_to;
    public String deliver_mobile;
    public String delivery_address,delivery_latitude,delivery_longitude;
    public String distance;
    public String delivery_date;
    public String delivery_start_time;
    public String delivery_end_time;
    public String total_quantity;
    public String total_amount;
    public String is_cancelled;
    public String order_delivery_status;
    public String order_status;
    public String payment_mode;
    public String created_at,transaction_id,net_amount,loyalty_points,loyalty_discount,coupon_discount,delivery_charges,order_date,cancelled_before;
    public OrderModel() {
    }

    protected OrderModel(Parcel in) {
        order_id = in.readString();
        deliver_to = in.readString();
        deliver_mobile = in.readString();
        delivery_address = in.readString();
        delivery_latitude = in.readString();
        delivery_longitude = in.readString();
        distance = in.readString();
        delivery_date = in.readString();
        delivery_start_time = in.readString();
        delivery_end_time = in.readString();
        total_quantity = in.readString();
        total_amount = in.readString();
        is_cancelled = in.readString();
        order_delivery_status = in.readString();
        order_status = in.readString();
        payment_mode = in.readString();
        created_at = in.readString();
        transaction_id = in.readString();
        net_amount = in.readString();
        loyalty_points = in.readString();
        loyalty_discount = in.readString();
        coupon_discount = in.readString();
        delivery_charges = in.readString();
        order_date = in.readString();
        cancelled_before = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_id);
        dest.writeString(deliver_to);
        dest.writeString(deliver_mobile);
        dest.writeString(delivery_address);
        dest.writeString(delivery_latitude);
        dest.writeString(delivery_longitude);
        dest.writeString(distance);
        dest.writeString(delivery_date);
        dest.writeString(delivery_start_time);
        dest.writeString(delivery_end_time);
        dest.writeString(total_quantity);
        dest.writeString(total_amount);
        dest.writeString(is_cancelled);
        dest.writeString(order_delivery_status);
        dest.writeString(order_status);
        dest.writeString(payment_mode);
        dest.writeString(created_at);
        dest.writeString(transaction_id);
        dest.writeString(net_amount);
        dest.writeString(loyalty_points);
        dest.writeString(loyalty_discount);
        dest.writeString(coupon_discount);
        dest.writeString(delivery_charges);
        dest.writeString(order_date);
        dest.writeString(cancelled_before);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };
}
