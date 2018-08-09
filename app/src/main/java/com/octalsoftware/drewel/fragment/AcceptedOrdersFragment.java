package com.octalsoftware.drewel.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.activity.AcceptedOrderDetailActivity;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.AcceptedOrderFragmentAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 4/13/2018.
 */


public class AcceptedOrdersFragment extends Fragment implements View.OnClickListener, ResponseInterface, OnClickItemListener {
    @BindView(R.id.recycler_accepted_order)
    RecyclerView recyclerView;
    private AcceptedOrderFragmentAdapter myadapter;
    private Toolbar toolbar;
    private View.OnClickListener onClickListener;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accepted_order, container, false);
        ButterKnife.bind(this, view);
        onClickListener = this;
        callNewOrderApi();

//        setAdapter();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACCEPTED");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
    }

    private void setAdapter(List<OrderModel> order) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myadapter = new AcceptedOrderFragmentAdapter(getActivity(), order, this);
        recyclerView.setAdapter(myadapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_order_details:
//                startActivity(new Intent(getActivity(), AcceptedOrderDetailActivity.class));
//                break;
        }
    }

    private void callNewOrderApi() {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, new Prefs(getActivity()).getUserdata().user_id);
        paramsHashMap.put(Tags.flag, "1");
//        paramsHashMap.put(Tags.device_id, new Prefs(getActivity()).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

    private void callChangeOrderStatusrApi(int position) {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, new Prefs(getActivity()).getUserdata().user_id);
        paramsHashMap.put(Tags.order_id, orderModelList.get(position).order_id);
        paramsHashMap.put(Tags.status, "2");
//        paramsHashMap.put(Tags.device_id, new Prefs(getActivity()).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_update_order_status);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_update_order_status);
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

    List<OrderModel> orderModelList;

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;

        switch (webServiceTag) {
            case ApiConstant.delivery_boy_tasks_list:
                AppDelegate.Companion.LogT("Response ==>" + message);
                ResponseModel userDataModel = new Gson().fromJson(message, ResponseModel.class);
                AppDelegate.Companion.LogT("userDataModel==>" + userDataModel);
                orderModelList = new ArrayList<>();
                if (AppDelegate.Companion.isValidString(message))
                    orderModelList = userDataModel.Order;
                setAdapter(orderModelList);
                break;
            case ApiConstant.delivery_boy_update_order_status:
                AppDelegate.Companion.showToast(getActivity(), successMsg);
                orderModelList.remove(position);
                myadapter.notifyDataSetChanged();
                Intent intent2 = new Intent();
                intent2.setAction("UPDATE_DELIVER");
                getActivity().sendBroadcast(intent2);
                break;
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;

//        AppDelegate.Companion.showToast(getActivity(), message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(getActivity());
        if (!isAdded())
            return;
        AppDelegate.Companion.showToast(getActivity(), message.getstrMessage());
    }

    int position;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (data != null) {
                orderModelList.remove(position);
                myadapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setOnItemClick(@NotNull String tag, int position) {
        if (tag.equalsIgnoreCase(Tags.pick_from_vendor)) {
            this.position = position;
            callChangeOrderStatusrApi(position);
        } else if (tag.equalsIgnoreCase(Tags.details)) {
            this.position = position;
            startActivityForResult(new Intent(getActivity(), AcceptedOrderDetailActivity.class).putExtra(Tags.DATA, orderModelList.get(position)), 200);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//   Toast.makeText(context, "This is the broadcast", Toast.LENGTH_SHORT).show();
            callNewOrderApiBroadcast();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void callNewOrderApiBroadcast() {
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.flag, "1");
//        paramsHashMap.put(Tags.device_id, new Prefs(getActivity()).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

}
