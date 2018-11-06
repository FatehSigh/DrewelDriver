package com.octalsoftware.drewel.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.activity.DeliveredOrderDetailActivity;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.DeliveredOrderFragmentAdapter;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.OrderModel;
import com.octalsoftware.drewel.model.ResponseModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.NotificationRxJavaBus;
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

public class OutOfDeliveryOrdersFragment extends Fragment implements View.OnClickListener, ResponseInterface, OnClickItemListener {
    @BindView(R.id.recycler_delivered_order)
    RecyclerView recyclerView;
    private DeliveredOrderFragmentAdapter myadapter;
    View.OnClickListener onClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivered_order, container, false);
        ButterKnife.bind(this, view);
        onClickListener = this;
        callNewOrderApi();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_DELIVER");
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
        myadapter = new DeliveredOrderFragmentAdapter(getActivity(), order, this);
        recyclerView.setAdapter(myadapter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded())
                callNewOrderApiBroadcast();
        }
    };

    public void callNewOrderApiBroadcast() {
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.flag, "2");
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
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
        paramsHashMap.put(Tags.flag, "2");
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_tasks_list);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

    List<OrderModel> orderModelList;

    private void callChangeOrderStatusrApi(int position) {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.order_id, orderModelList.get(position).order_id);
        paramsHashMap.put(Tags.status, "3");

        if (!new Prefs(getActivity()).getStringValue(Tags.LAT, "").isEmpty()) {
            double latitude = Double.parseDouble(new Prefs(getActivity()).getStringValue(Tags.LAT, ""));
            double longitude = Double.parseDouble(new Prefs(getActivity()).getStringValue(Tags.LNG, ""));

            String distance = AppDelegate.Companion.distance(AppDelegate.Companion.getStoreLat(), AppDelegate.Companion.getStoreLng(), latitude, longitude);
            paramsHashMap.put(Tags.distance_km, distance);
        } else
            paramsHashMap.put(Tags.distance_km, "0");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(broadcastReceiver);
    }

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
                NotificationRxJavaBus.Companion.getInstance().getNotificationPublishSubject().onNext("UPDATE_COMPLETED");
                collectCashAlert(getActivity(),"",getString(R.string.collect_cash));
                break;
        }
    }
    void collectCashAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(true);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
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
        if (requestCode == 300) {
            if (data != null) {
                orderModelList.remove(position);
                myadapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setOnItemClick(@NotNull String tag, int position) {
        if (tag.equalsIgnoreCase(Tags.deliver_to_customer)) {
            this.position = position;
            callChangeOrderStatusrApi(position);
        } else if (tag.equalsIgnoreCase(Tags.details)) {
            this.position = position;
            startActivityForResult(new Intent(getActivity(), DeliveredOrderDetailActivity.class).putExtra(Tags.DATA, orderModelList.get(position)), 300);
        }
    }
}