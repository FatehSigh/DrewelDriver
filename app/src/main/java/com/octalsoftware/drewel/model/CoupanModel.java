package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CoupanModel implements Parcelable {
    public String coupone_code;
    public String amount;
    public String coupon_id;
    public String coupon_percent;


    protected CoupanModel(Parcel in) {
        coupone_code = in.readString();
        amount = in.readString();
        coupon_id = in.readString();
        coupon_percent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(coupone_code);
        dest.writeString(amount);
        dest.writeString(coupon_id);
        dest.writeString(coupon_percent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoupanModel> CREATOR = new Creator<CoupanModel>() {
        @Override
        public CoupanModel createFromParcel(Parcel in) {
            return new CoupanModel(in);
        }

        @Override
        public CoupanModel[] newArray(int size) {
            return new CoupanModel[size];
        }
    };

    @Override
    public String toString() {
        return "CoupanModel{" +
                "coupone_code='" + coupone_code + '\'' +
                ", amount='" + amount + '\'' +
                ", coupon_id='" + coupon_id + '\'' +
                ", coupon_percent='" + coupon_percent + '\'' +
                '}';
    }
}
