package com.octalsoftware.drewel.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.Adapter.SimilarOrderItemAdapter;
import com.octalsoftware.drewel.model.OrderDetailModel;
import com.octalsoftware.drewel.model.OrderModel;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompletedOrderDetailActivity extends AppCompatActivity implements ResponseInterface {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_status)
    AppCompatTextView tv_status;
    @BindView(R.id.tv_delivery_time)
    AppCompatTextView tv_delivery_time;
    @BindView(R.id.btn_decline_order)
    Button btn_decline_order;
    @BindView(R.id.btn_accept_order)
    Button btn_accept_order;
    private SimilarOrderItemAdapter myadapter;
    private Toolbar mToolbar;
    OrderModel orderModel;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Modifying the Previous XML
        setContentView(R.layout.activity_new_order_detail);
        ButterKnife.bind(this);
        orderModel = getIntent().getParcelableExtra(Tags.DATA);
        initView();
        callCompletedOrderDetailApi();

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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_accept_order.setVisibility(View.GONE);
        btn_decline_order.setVisibility(View.GONE);
        txt_toolbar.setText(getString(R.string.order) + " #" + orderModel.order_id);
//        tv_status.setText(getText(R.string.completed));
    }

    private void setAdapter(List<ProductModel> products) {
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);
        myadapter = new SimilarOrderItemAdapter(products);
        recyclerView.setAdapter(myadapter);
    }

    private void callCompletedOrderDetailApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.order_id, orderModel.order_id);
        paramsHashMap.put(Tags.user_id, new Prefs(this).getUserdata().user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.get_order_detail_for_delivery_boy);
        requestModel.setWebServiceTag(ApiConstant.get_order_detail_for_delivery_boy);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        switch (webServiceTag) {
            case ApiConstant.get_order_detail_for_delivery_boy:
                AppDelegate.Companion.hideProgressDialog(this);
                AppDelegate.Companion.LogT("Response ==>" + message);
                OrderDetailModel orderDetailModel = new Gson().fromJson(message, OrderDetailModel.class);
                setData(orderDetailModel);
                setAdapter(orderDetailModel.Products);
                break;
        }
    }

    @OnClick({R.id.btn_call_delivery_person})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_call_delivery_person:
                AppDelegate.Companion.call(this, orderModel.deliver_mobile);
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

        tv_status.setText(orderDetailModel.Order.order_delivery_status);
        tv_order_amount.setText(orderDetailModel.Order.total_amount+" "+ getString(R.string.omr));
        DecimalFormat df = new DecimalFormat(".##");
//        if (AppDelegate.Companion.isValidString(orderModel.distance))
        tv_delivery_address_in_miles.setText(df.format(Double.parseDouble(orderDetailModel.Order.distance)) + " Miles");
        tv_delivery_order_to_person.setText(orderDetailModel.Order.deliver_to);
        tv_delivery_order_address.setText(orderDetailModel.Order.delivery_address);
        btn_call_delivery_person.setText(orderDetailModel.Order.deliver_mobile);

    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
//        AppDelegate.Companion.showSnackBar(et_email, message.getstrMessage());
    }

}
