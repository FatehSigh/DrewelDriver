package com.octalsoftware.drewel.retrofitService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestClient {
    //        public static String base_url = "http://192.168.1.92/drewel/web_services/";
//    public static String url = "https://56.octallabs.com/drewel/";
//    public static String base_url = "https://56.octallabs.com/drewel/web_services/";
    public static String url = "http://drewel.om/";
    public static String base_url = "http://drewel.om/web_services/";
    public static Retrofit retrofit;

    public static Retrofit getRestClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);
        clientBuilder.connectTimeout(10, TimeUnit.MINUTES);
        clientBuilder.readTimeout(10, TimeUnit.MINUTES);
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .baseUrl(base_url).build();
        return retrofit;
    }

    public static <S> S createService(Class<S> serviceClass) {
        getRestClient();
        return retrofit.create(serviceClass);
    }
}
