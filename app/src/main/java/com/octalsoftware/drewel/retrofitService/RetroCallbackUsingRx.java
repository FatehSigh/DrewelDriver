package com.octalsoftware.drewel.retrofitService;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.octalsoftware.drewel.constant.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

class RetroCallbackUsingRx<T> implements Observer<LinkedTreeMap> {
    private ResponseInterface responseInterface;
    private String webServiceTag;

    public RetroCallbackUsingRx(final ResponseInterface responseInterface, final String webServiceTag) {
        this.responseInterface = responseInterface;
        this.webServiceTag = webServiceTag;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(LinkedTreeMap response) {
        Log.e("response in on next" +
                "==>", response + "");
        if (response != null) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                boolean status = jsonObject.getJSONObject(Tags.response).getBoolean(Tags.status);
                String message = "";
                if (status) {
                    if (jsonObject.getJSONObject(Tags.response).has(Tags.data))
                        message = jsonObject.getJSONObject(Tags.response).getString(Tags.data);
                    if (responseInterface != null) {
                        responseInterface.onSuccess(message, webServiceTag, jsonObject.getJSONObject(Tags.response).getString(Tags.message));
                    }
                } else {
                    message = jsonObject.getJSONObject(Tags.response).getString(Tags.message);
                    responseInterface.onFailure(message, webServiceTag);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (response != null) {
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getJSONObject(Tags.response).getBoolean(Tags.status);
                    String message = jsonObject.getJSONObject(Tags.response).getString(Tags.message);
                    responseInterface.onFailureRetro(new RestError(message), webServiceTag);
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        Log.e("response==>", throwable + "");
        Observable<RestError> observable = Observable.create(
                (ObservableOnSubscribe<RestError>) e -> {
                    e.onNext(getMessage(throwable));
                    e.onComplete();
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        Observer<RestError> observer = new Observer<RestError>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(RestError data) {
                responseInterface.onFailureRetro(data, webServiceTag);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(observer);
    }

    @Override
    public void onComplete() {

    }

    public RestError getMessage(Throwable throwable) {
        RestError error = null;

        if (throwable instanceof HttpException) {
            ResponseBody body = ((HttpException) throwable).response().errorBody();
            JSONObject json = null;
            try {
                json = new JSONObject(new String(body.bytes()));
                Log.e("body json", json.toString() + "");
                error = new RestError(json.getJSONObject(Tags.response).getString(Tags.message));
            } catch (JSONException e) {
                error = new RestError(throwable.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (throwable instanceof ConnectException) {
                error = new RestError("Please check your internet connection");
            } else
                error = new RestError("Something went wrong.");
        }
        return error;
    }

}
