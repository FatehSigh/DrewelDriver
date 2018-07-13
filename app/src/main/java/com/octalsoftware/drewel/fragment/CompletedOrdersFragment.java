package com.octalsoftware.drewel.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.HomeActivity;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.CompletedOrderFragmentAdapter;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.OrderModel;
import com.octalsoftware.drewel.model.ResponseModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 4/11/2018.
 */

public class CompletedOrdersFragment extends Fragment implements View.OnClickListener, ResponseInterface, OnClickItemListener {

    @BindView(R.id.recycler_completed_order)
    RecyclerView recyclerView;
    private View.OnClickListener onClickListener;
    private CompletedOrderFragmentAdapter myadapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_order, container, false);
        ButterKnife.bind(this, view);
        onClickListener = this;
        ((HomeActivity) getActivity()).hideNotificationBellIcon();
        ((HomeActivity) getActivity()).hideSwitchCompatOnOff();
        callNewOrderApi();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
//        callNewOrderApi();
    }

    private void setAdapter(List<OrderModel> order) {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myadapter = new CompletedOrderFragmentAdapter(getActivity(), order, this);
        recyclerView.setAdapter(myadapter);
    }

    public static CompletedOrdersFragment newInstance() {
        Bundle args = new Bundle();
        CompletedOrdersFragment fragment = new CompletedOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    private void callNewOrderApi() {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.flag, "3");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_tasks_list);
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
            case ApiConstant.delivery_boy_tasks_list:
                AppDelegate.Companion.hideProgressDialog(getActivity());
                AppDelegate.Companion.LogT("Response ==>" + message);
                ResponseModel userDataModel = new Gson().fromJson(message, ResponseModel.class);
                AppDelegate.Companion.LogT("userDataModel==>" + userDataModel);
                if (AppDelegate.Companion.isValidString(message))
                    setAdapter(userDataModel.Order);
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

    @Override
    public void setOnItemClick(@NotNull String tag, int position) {

    }
}
