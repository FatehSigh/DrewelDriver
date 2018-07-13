package com.octalsoftware.drewel.retrofitService;

import com.google.gson.annotations.SerializedName;

public class RestError {

    public RestError() {
    }

    @SerializedName("code")
    private Integer code;
    @SerializedName("error_message")
    private String strMessage;

    public RestError(String strMessage) {
        this.strMessage = strMessage;
    }

    public Integer getcode() {
        return code;
    }

    public void setcode(Integer code) {
        this.code = code;
    }

    public String getstrMessage() {
        return strMessage;
    }

    public void setstrMessage(String strMessage) {
        this.strMessage = strMessage;
    }
}