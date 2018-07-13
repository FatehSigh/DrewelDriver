package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ResponseModel implements Parcelable {

    public List<OrderModel> Order;
    public List<NotificationsModel> Notifications;
    public String unread;

    protected ResponseModel(Parcel in) {
        Order = in.createTypedArrayList(OrderModel.CREATOR);
        Notifications = in.createTypedArrayList(NotificationsModel.CREATOR);
        unread = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(Order);
        dest.writeTypedList(Notifications);
        dest.writeString(unread);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseModel> CREATOR = new Creator<ResponseModel>() {
        @Override
        public ResponseModel createFromParcel(Parcel in) {
            return new ResponseModel(in);
        }

        @Override
        public ResponseModel[] newArray(int size) {
            return new ResponseModel[size];
        }
    };

    @Override
    public String toString() {
        return "ResponseModel{" +
                "Order=" + Order +
                ", Notifications=" + Notifications +
                ", unread='" + unread + '\'' +
                '}';
    }
}
