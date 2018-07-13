package com.octalsoftware.drewel.retrofitService;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomResponseInterceptor implements Interceptor {

    private static String newToken;
    private String bodyString;

    private final String TAG = getClass().getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() != 200) {
            Response r = null;
            try {
                r = makeTokenRefreshCall(request, chain);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return r;
        }
        Log.d(TAG, "INTERCEPTED:$ " + response.toString());
        return response;
    }

    private Response makeTokenRefreshCall(Request req, Chain chain) throws JSONException, IOException {
        Log.d(TAG, "Retrying new request");
        Request newRequest;
        newRequest = req.newBuilder().build();
        Response another = chain.proceed(newRequest);
        while (another.code() != 200) {
            makeTokenRefreshCall(newRequest, chain);
        }
        return another;
    }

    private String fetchToken() {

        return "bdfd";
    }
}