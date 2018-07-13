package com.octalsoftware.drewel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.EarningAdapter;
import com.octalsoftware.drewel.model.UserDataModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharukhb on 4/11/2018.
 */

public class EarningFragment extends Fragment implements ResponseInterface {
    @BindView(R.id.recyclerView_my_earning)
    RecyclerView recyclerView;
    private EarningAdapter myadapter;
    View.OnClickListener onClickListener;
    @BindView(R.id.tv_my_earning)
    AppCompatTextView tv_my_earning;
    @BindView(R.id.tv_due_payment)
    AppCompatTextView tv_due_payment;
    @BindView(R.id.btn_withdrawal)
    Button btn_withdrawal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earning, container, false);
        ButterKnife.bind(this, view);
        callEarningListApi();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
        setAdapter();
//        callEarningListApi();
    }
    private void setAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myadapter = new EarningAdapter(onClickListener);
        recyclerView.setAdapter(myadapter);
    }

    @OnClick({R.id.btn_withdrawal})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_withdrawal:

                break;
        }
    }

    private void callEarningListApi() {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.flag, "3");
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.earningList);
        requestModel.setWebServiceTag(ApiConstant.earningList);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;
        AppDelegate.Companion.showToast(getActivity(), message);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;
        switch (webServiceTag) {
            case ApiConstant.earningList:
                AppDelegate.Companion.LogT("Response ==>" + message);
                UserDataModel userDataModel = new Gson().fromJson(message, UserDataModel.class);
                AppDelegate.Companion.LogT("userDataModel==>" + userDataModel);
                AppDelegate.Companion.showToast(getActivity(), successMsg);
                setAdapter();
                break;
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;
        AppDelegate.Companion.showToast(getActivity(), message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;
        AppDelegate.Companion.showToast(getActivity(), message.getstrMessage());
    }

}
