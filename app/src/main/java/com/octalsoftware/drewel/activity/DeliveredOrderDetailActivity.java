package com.octalsoftware.drewel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.HomeActivity;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.SimilarOrderItemAdapter;
import com.octalsoftware.drewel.model.NotificationsModel;
import com.octalsoftware.drewel.model.OrderDetailModel;
import com.octalsoftware.drewel.model.OrderModel;
import com.octalsoftware.drewel.model.PNModel;
import com.octalsoftware.drewel.model.ProductModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveredOrderDetailActivity extends AppCompatActivity implements ResponseInterface {
    @BindView(R.id.tv_order_place_on_date)
    public TextView tv_order_place_on_date;
    @BindView(R.id.tv_total_items)
    public TextView tv_total_items;
    @BindView(R.id.tv_delivery_date)
    public TextView tv_delivery_date;
    @BindView(R.id.tv_order_amount)
    public TextView tv_order_amount;
    @BindView(R.id.tv_delivery_address_in_miles)
    public TextView tv_delivery_address_in_miles;
    @BindView(R.id.tv_delivery_order_to_person)
    public TextView tv_delivery_order_to_person;
    @BindView(R.id.tv_delivery_order_address)
    public TextView tv_delivery_order_address;
    @BindView(R.id.btn_call_delivery_person)
    public Button btn_call_delivery_person;
    @BindView(R.id.tv_order_items)
    public TextView tv_order_items;
    @BindView(R.id.txt_toolbar)
    public TextView txt_toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_status)
    TextView tv_status;
    OrderModel orderModel;
    PNModel pnModel;
    NotificationsModel notificationModel;
    @BindView(R.id.btn_accept_order)
    Button btn_accept_order;
    @BindView(R.id.tv_delivery_time)
    AppCompatTextView tv_delivery_time;
    int From = 0;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.rl_norecordfound)
    RelativeLayout rl_norecordfound;
    @BindView(R.id.txt_norecordFound)
    AppCompatTextView txt_norecordFound;
    boolean isCalled = false;
    @BindView(R.id.imv_track_order)
    ImageView imv_track_order;
    @BindView(R.id.tv_payment_mode)
    AppCompatTextView tv_payment_mode;
    OrderDetailModel orderDetailModel;
    private SimilarOrderItemAdapter myadapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_order_detail);
        ButterKnife.bind(this);
        initView();
        if (getIntent() != null && getIntent().getIntExtra(Tags.FROM, 0) == 1) {
            From = 1;
            pnModel = getIntent().getParcelableExtra(Tags.DATA);
            txt_toolbar.setText(String.format("%s #%s", getString(R.string.order), pnModel.getItem_id()));
            callCompletedOrderDetailApi(pnModel.getItem_id());
        } else if (getIntent() != null && getIntent().getIntExtra(Tags.FROM, 0) == 2) {
            From = 2;
            notificationModel = getIntent().getParcelableExtra(Tags.DATA);
            txt_toolbar.setText(String.format("%s #%s", getString(R.string.order), notificationModel.item_id));
            callCompletedOrderDetailApi(notificationModel.item_id);

        } else {
            orderModel = getIntent().getParcelableExtra(Tags.DATA);
            txt_toolbar.setText(String.format("%s #%s", getString(R.string.order), orderModel.order_id));
            callCompletedOrderDetailApi(orderModel.order_id);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
//        callCompletedOrderDetailApi();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (From == 2) {
                    if (isCalled) {
                        Intent intent = new Intent();
                        intent.putExtra(Tags.data, notificationModel);
                        setResult(Activity.RESULT_OK, intent);
                    }
                } else if (From == 1) {
                    startActivity(new Intent(this, HomeActivity.class));
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({R.id.btn_accept_order, R.id.btn_call_delivery_person})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_accept_order:
                callChangeOrderStatusrApi();
                break;
            case R.id.btn_call_delivery_person:
                AppDelegate.Companion.call(this, orderDetailModel.Order.deliver_mobile);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (From == 2) {
            if (isCalled) {
                Intent intent = new Intent();
                intent.putExtra(Tags.data, notificationModel);
                setResult(Activity.RESULT_OK, intent);
            }
        } else if (From == 1) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setAdapter(List<ProductModel> products) {
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);
        myadapter = new SimilarOrderItemAdapter(products, this);
        recyclerView.setAdapter(myadapter);
    }

    private void callCompletedOrderDetailApi(String order_id) {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.order_id, order_id);
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.get_order_detail_for_delivery_boy);
        requestModel.setWebServiceTag(ApiConstant.get_order_detail_for_delivery_boy);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private void callReadNotificationApi(String id) {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.notification_id, id);

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.read_notification);
        requestModel.setWebServiceTag(ApiConstant.read_notification);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private void callChangeOrderStatusrApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        if (From == 1)
            paramsHashMap.put(Tags.order_id, pnModel.getItem_id());
        else if (From == 2)
            paramsHashMap.put(Tags.order_id, notificationModel.item_id);
        else
            paramsHashMap.put(Tags.order_id, orderModel.order_id);
        paramsHashMap.put(Tags.status, "3");

        double latitude = Double.parseDouble(new Prefs(this).getStringValue(Tags.LAT, ""));
        double longitude = Double.parseDouble(new Prefs(this).getStringValue(Tags.LNG, ""));
        String distance = AppDelegate.Companion.distance(AppDelegate.Companion.getStoreLat(), AppDelegate.Companion.getStoreLng(), latitude, longitude);
        paramsHashMap.put(Tags.distance_km, distance);
//        paramsHashMap.put(Tags.device_id, new Prefs(getActivity()).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_update_order_status);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_update_order_status);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    void collectCashAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(false);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.putExtra(Tags.data, 1);
                setResult(Activity.RESULT_OK, intent);
                dialogInterface.dismiss();
                finish();
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message);
        txt_norecordFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(this);
        switch (webServiceTag) {
            case ApiConstant.get_order_detail_for_delivery_boy:
                AppDelegate.Companion.LogT("Response ==>" + message);
                orderDetailModel = new Gson().fromJson(message, OrderDetailModel.class);

                setData(orderDetailModel);
                setAdapter(orderDetailModel.Products);
                scrollView.setVisibility(View.VISIBLE);
                rl_norecordfound.setVisibility(View.GONE);
                if (From == 1)
                    callReadNotificationApi(pnModel.getNotification_id());
                else if (From == 2) if (notificationModel.is_read.equals("0"))
                    callReadNotificationApi(notificationModel.id);
                break;
            case ApiConstant.delivery_boy_update_order_status:
                AppDelegate.Companion.showToast(this, successMsg);

                collectCashAlert(this, "", getString(R.string.collect_cash));

                if (orderDetailModel.Order.payment_mode.equals("COD")) {
                    Intent intent = new Intent();
                    intent.putExtra(Tags.data, 1);
                    setResult(Activity.RESULT_OK, intent);
                    collectCashAlert(this, "", getString(R.string.collect_cash));
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Tags.data, 1);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }


//                if (From == 1) {
//                    startActivity(new Intent(this, HomeActivity.class));
//                } else if (From == 2) if (notificationModel.is_read.equals("0")) {
//                } else {
//                    Intent intent = new Intent();
//                    intent.putExtra(Tags.data, 1);
//                    setResult(Activity.RESULT_OK, intent);
//                }
//                finish();
                break;
            case ApiConstant.read_notification:
                AppDelegate.Companion.LogT("Response==>" + message);
                if (notificationModel != null) {
                    notificationModel.is_read = "1";
                    isCalled = true;
                }
                break;
        }

    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void setData(OrderDetailModel orderDetailModel) {
        tv_total_items.setText(orderDetailModel.Order.total_quantity);
        tv_order_items.setText(orderDetailModel.Order.total_quantity);
        try {
            Date startdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(orderDetailModel.Order.order_date);
            tv_order_place_on_date.setText(new SimpleDateFormat("dd MMM ''yy   h:mm a").format(startdate));
            Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderDetailModel.Order.delivery_date);
            tv_delivery_date.setText(new SimpleDateFormat("EEE, dd MMM ''yy").format(endDate));
            Date starttime = new SimpleDateFormat("hh:mm:ss").parse(orderDetailModel.Order.delivery_start_time);
            Date endtime = new SimpleDateFormat("hh:mm:ss").parse(orderDetailModel.Order.delivery_end_time);
            tv_delivery_time.setText(new SimpleDateFormat("h:mm a").format(starttime) + " to " + new SimpleDateFormat("h:mm a").format(endtime));

        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }

//        tv_status.setText(orderDetailModel.Order.order_delivery_status);
        switch (orderDetailModel.Order.order_delivery_status) {
            case "Cancelled":
                tv_status.setText(getString(R.string.Cancelled));
                break;
            case "Not Cancelled":
                tv_status.setText(getString(R.string.NotCancelled));
                break;
            case "Pending":
                tv_status.setText(getString(R.string.Pending));
                break;
            case "Under Packaging":
                tv_status.setText(getString(R.string.UnderPackaging));
                break;
            case "Ready To Deliver":
                tv_status.setText(getString(R.string.ReadyToDeliver));
                break;
            case "Delivered":
                tv_status.setText(getString(R.string.delivered));
                break;
        }
        switch (orderDetailModel.Order.payment_mode) {
            case "COD":
                tv_payment_mode.setText(getString(R.string.COD));
                break;
            case "Wallet":
                tv_payment_mode.setText(getString(R.string.wallet));
                break;
            case "Online":
                tv_payment_mode.setText(getString(R.string.credit_card));
                break;
            default:
                tv_payment_mode.setText(getString(R.string.thawani));
                break;
        }
        tv_order_amount.setText(orderDetailModel.Order.total_amount + " " + getString(R.string.omr));
        DecimalFormat df = new DecimalFormat(".##");
//        if (orderModel != null && AppDelegate.Companion.isValidString(orderModel.distance))
        tv_delivery_address_in_miles.setText(df.format(Double.parseDouble(orderDetailModel.Order.distance)) + " " + getString(R.string.miles));
        tv_delivery_order_to_person.setText(orderDetailModel.Order.deliver_to);
        tv_delivery_order_address.setText(orderDetailModel.Order.delivery_address);
        btn_call_delivery_person.setText(orderDetailModel.Order.deliver_mobile);
        imv_track_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveredOrderDetailActivity.this, TrackOrderActivity.class).putExtra(Tags.LAT, orderDetailModel.Order.delivery_latitude).putExtra(Tags.LNG, orderDetailModel.Order.delivery_longitude)
                );
            }
        });
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message);
        txt_norecordFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message.getstrMessage());
        txt_norecordFound.setVisibility(View.VISIBLE);
    }
}
