package com.octalsoftware.drewel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationsModel implements Parcelable {
    public String id, type, message, send_by, name, is_read, created, user_id, unread,item_id,message_arabic;

    public NotificationsModel() {
    }


    protected NotificationsModel(Parcel in) {
        id = in.readString();
        type = in.readString();
        message = in.readString();
        send_by = in.readString();
        name = in.readString();
        is_read = in.readString();
        created = in.readString();
        user_id = in.readString();
        unread = in.readString();
        item_id = in.readString();
        message_arabic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(message);
        dest.writeString(send_by);
        dest.writeString(name);
        dest.writeString(is_read);
        dest.writeString(created);
        dest.writeString(user_id);
        dest.writeString(unread);
        dest.writeString(item_id);
        dest.writeString(message_arabic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationsModel> CREATOR = new Creator<NotificationsModel>() {
        @Override
        public NotificationsModel createFromParcel(Parcel in) {
            return new NotificationsModel(in);
        }

        @Override
        public NotificationsModel[] newArray(int size) {
            return new NotificationsModel[size];
        }
    };
}
