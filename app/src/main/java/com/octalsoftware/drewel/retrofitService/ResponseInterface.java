package com.octalsoftware.drewel.retrofitService;


public interface ResponseInterface {

    void onNoNetwork(String message, String webServiceTag);

    void onSuccess(String message, String webServiceTag, String successMsg);

    void onFailure(String message, String webServiceTag);

    void onFailureRetro(RestError message, String webServiceTag);
}
