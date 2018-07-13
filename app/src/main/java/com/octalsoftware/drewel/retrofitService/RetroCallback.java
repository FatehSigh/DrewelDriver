package com.octalsoftware.drewel.retrofitService;


import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class RetroCallback<T> implements Callback<T> {
    private ResponseInterface responseInterface;
    private String webServiceTag;


    public RetroCallback(ResponseInterface responseInterface, String webServiceTag) {
        this.responseInterface = responseInterface;
        this.webServiceTag = webServiceTag;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Log.e("response", response.body() + "");
        if (response.isSuccessful()) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject((LinkedTreeMap) response.body());
                boolean status = jsonObject.getBoolean("status");
                String message = jsonObject.getString("msg");
                if (status) {
                    if (responseInterface != null) {
                        responseInterface.onSuccess(String.valueOf(response.body()), webServiceTag, "");
                    }
                } else {
                    responseInterface.onFailure(message, webServiceTag);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (response.body() != null)
                    responseInterface.onFailureRetro(new RestError((String) response.body()), webServiceTag);
                else {
                    responseInterface.onFailureRetro(new RestError(response.message()), webServiceTag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e("onFailure==>", t + "");
    }
}
