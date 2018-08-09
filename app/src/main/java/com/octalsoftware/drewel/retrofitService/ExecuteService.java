package com.octalsoftware.drewel.retrofitService;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.constant.Tags;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.octalsoftware.drewel.retrofitService.ApiConstant.GET;


public class ExecuteService {
    public static ProgressDialog progressdialog;

    public void execute(Context context, boolean IsShowProgressDialog, ResponseInterface responseInterface, RequestModel requestModel) {

        if (AppDelegate.Companion.haveNetworkConnection(context, false)) {
            try {
                executeRequestusingRx(requestModel, responseInterface);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            if (responseInterface != null) {
                responseInterface.onNoNetwork("Please check your internet connection.", requestModel.getWebServiceTag());
            }
        }
    }

    public void executeRequestusingRx(RequestModel requestModel, ResponseInterface responseInterface) throws UnsupportedEncodingException, JSONException {
        APIInterface userService;
        Log.e("requestModel==", requestModel + "");
        userService = RestClient.createService(APIInterface.class);
        if (requestModel.getWebServiceType() == GET) {
            if (AppDelegate.Companion.isValidString(requestModel.getWebServiceName2())) {
                if (requestModel.getParamsHashmap() == null)
                    userService.dataRequestGetRxWithoutParam(requestModel.getWebServiceName(), requestModel.getWebServiceName2()).retry(3).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
                else
                    userService.dataRequestGetRx(requestModel.getWebServiceName(), requestModel.getWebServiceName2(), getMultiPartDataText(requestModel)).retry(3).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
            } else {
                if (requestModel.getParamsHashmap() == null)
                    userService.dataRequestGetRxWithoutParam(requestModel.getWebServiceName()).retry(3).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));

                else
                    userService.dataRequestGetRx(requestModel.getWebServiceName(), getMultiPartDataText(requestModel)).retry(3).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
            }
        } else if (requestModel.getQueryFiles() == null) {
            if (AppDelegate.Companion.isValidString(requestModel.getWebServiceName2())) {
                userService.dataRequestRx(requestModel.getWebServiceName(), requestModel.getWebServiceName2(), getMultiPartDataText(requestModel)).retry(3).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
            } else
                userService.dataRequestRx(requestModel.getWebServiceName(), getMultiPartDataText(requestModel)).retry(3).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));

        } else if (AppDelegate.Companion.isValidString(requestModel.getWebServiceName2()))
            userService.dataRequestMultiPartRx(requestModel.getWebServiceName(), requestModel.getWebServiceName2(), getMultiPartDataText(requestModel), getMultiPartDataImage(requestModel)).retry(3).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
        else {
            userService.dataRequestMultiPartRx(requestModel.getWebServiceName(), getMultiPartDataText(requestModel), getMultiPartDataImage(requestModel)).retry(3).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new RetroCallbackUsingRx(responseInterface, requestModel.getWebServiceTag()));
        }
    }


    public RequestBody getMultiPartDataText(RequestModel requestModel) {
        HashMap<String, Object> bodyHashMap = new HashMap<>();
        for (Map.Entry<String, String> stringStringEntry : requestModel.getParamsHashmap().entrySet()) {
            if (AppDelegate.Companion.isValidString((stringStringEntry.getValue())))
                bodyHashMap.put(stringStringEntry.getKey(),/* getRequestBodyString*/(stringStringEntry.getValue()));
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(bodyHashMap)).toString());

        return body;
    }

    public HashMap<String, RequestBody> getMultiPartDataTextforImage(RequestModel requestModel) {

        HashMap<String, Object> bodyHashMap = new HashMap<>();
        for (Map.Entry<String, String> stringStringEntry : requestModel.getParamsHashmap().entrySet()) {
            if (AppDelegate.Companion.isValidString((stringStringEntry.getValue())))
                bodyHashMap.put(stringStringEntry.getKey(),/* getRequestBodyString*/(stringStringEntry.getValue()));
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(bodyHashMap)).toString());

        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
        bodyHashMap.put(Tags.data, body);

        return requestBodyHashMap;
    }

    public List<MultipartBody.Part> getMultiPartDataImage(RequestModel requestModel) {
        List<MultipartBody.Part> bodyHashMap = new ArrayList<>();
        for (Map.Entry<String, File> stringStringEntry : requestModel.getQueryFiles().entrySet()) {
            bodyHashMap.add(getRequestBodyImage(stringStringEntry.getValue(), stringStringEntry.getKey()));
        }
        return bodyHashMap;
    }

    public MultipartBody.Part getRequestBodyImage(File file, String key) {
        RequestBody requestFile = null;
//        if (file.getAbsolutePath().contains("Video")) {
//            requestFile = RequestBody.create(MediaType.parse("video/*"), file);
//        } else
        requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(key, file.getName(), requestFile);

        return body;
    }

    public RequestBody getRequestBodyString(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }


//    public boolean isValidString(String string) {
//        if (string != null && !string.equalsIgnoreCase("null") && string.length() > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static String generateToken(HashMap<String, String> params, HashMap<String, File> fileParams, String timestamp) {
        ArrayList<String> sortedParams = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        try {
            if (params != null & params.size() > 0)
                for (String param : params.keySet()) {
                    sortedParams.add(param.toUpperCase());
                }
        } catch (Exception e) {

        }
        try {
            if (fileParams != null & fileParams.size() > 0)
                for (String param : fileParams.keySet()) {
                    sortedParams.add(param.toUpperCase());
                }
        } catch (Exception e) {

        }

        Collections.sort(sortedParams);
        sb.append(timestamp);

        for (String param : sortedParams) {
            sb.append(param);
        }
        Log.e("Sb==>", sb.toString());
        final String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(sb.toString().getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}




