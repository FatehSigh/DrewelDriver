package com.octalsoftware.drewel.retrofitService;

import android.content.Context;

import java.io.File;
import java.util.HashMap;


public class RequestModel {

    protected Context context;
    private int webServiceType;
    private String webServiceName, webServiceName2, webServiceTag;
    private HashMap<String, String> paramsHashmap = new HashMap<>();
    private HashMap<String, String> headerHashmap = new HashMap<>();
    private HashMap<String, File> files;

    public String getWebServiceName2() {
        return webServiceName2;
    }

    public void setWebServiceName2(String webServiceName2) {
        this.webServiceName2 = webServiceName2;
    }

    public Context getContext() {
        return context;
    }

    public HashMap<String, String> getParamsHashmap() {
        return paramsHashmap;
    }

    public void setParamsHashmap(HashMap<String, String> paramsHashmap) {
        this.paramsHashmap = paramsHashmap;
    }

    public HashMap<String, String> getHeaderHashmap() {
        return headerHashmap;
    }

    public void setHeaderHashmap(HashMap<String, String> headerHashmap) {
        this.headerHashmap = headerHashmap;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public int getWebServiceType() {
        return webServiceType;
    }

    public void setWebServiceType(int webServiceType) {
        this.webServiceType = webServiceType;
    }

    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public String getWebServiceTag() {
        return webServiceTag;
    }

    public void setWebServiceTag(String webServiceTag) {
        this.webServiceTag = webServiceTag;
    }

    public HashMap<String, File> getQueryFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "RequestModel{" +
                "context=" + context +
                ", webServiceType=" + webServiceType +
                ", webServiceName='" + webServiceName + '\'' +
                ", webServiceTag='" + webServiceTag + '\'' +
                ", paramsHashmap=" + paramsHashmap +
                ", headerHashmap=" + headerHashmap +
                ", files=" + files +
                '}';
    }


    public void setQueryFiles(HashMap<String, File> files) {
        this.files = files;
    }

}
