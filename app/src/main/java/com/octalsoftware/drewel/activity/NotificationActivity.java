package com.octalsoftware.drewel.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.HomeActivity;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.firebase.MyFirebaseMessagingService;
import com.octalsoftware.drewel.fragment.Adapter.NotificationListAdapter;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.NotificationsModel;
import com.octalsoftware.drewel.model.PNModel;
import com.octalsoftware.drewel.model.ResponseModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;
import com.octalsoftware.drewel.utils.SwipeHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity implements ResponseInterface, OnClickItemListener {
    @BindView(R.id.recycler_accepted_order)
    RecyclerView recyclerView;
    @BindView(R.id.txt_clearall)
    TextView txt_clearall;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    NotificationListAdapter myadapter;
    private List<NotificationsModel> notificationList;
    int From = 0;
    PNModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationlist);
        ButterKnife.bind(this);
        initView();
        if (getIntent() != null && getIntent().getIntExtra(Tags.FROM, 0) == 1) {
            From = 1;
            notificationModel = getIntent().getParcelableExtra(Tags.DATA);
            callReadNotificationApi(notificationModel.getItem_id());
        }
        callGetNotificationListApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);

    }

    @Override
    public void onBackPressed() {
        if (From == 1) {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (From == 1) {
                    startActivity(new Intent(this, HomeActivity.class));
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txt_clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog(getString(R.string.delete_all_notifications), 0, true);
            }
        });
    }

    private void setAdapter(List<NotificationsModel> products) {
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        myadapter = new NotificationListAdapter(this, products, this);
        recyclerView.setAdapter(myadapter);
if(products.size()>0)
    txt_clearall.setVisibility(View.VISIBLE);
else
    txt_clearall.setVisibility(View.GONE);
//        object : SwipeHelper(this, recyclerView) {
//            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder, underlayButtons: MutableList<SwipeHelper.UnderlayButton>) {
//                underlayButtons.add(SwipeHelper.UnderlayButton(getString(R.string.delete), 0, Color.parseColor("#eb011c"), UnderlayButtonClickListener { pos -> showLogoutDialog(getString(R.string.delete_notificaions), pos, false) }))
//            }
//        }
        new SwipeHelper(this, recyclerView) {

            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(getString(R.string.delete), 0, Color.parseColor("#eb011c"), new UnderlayButtonClickListener() {
                    @Override
                    public void onClick(int pos) {
                        showLogoutDialog(getString(R.string.delete_notificaions), pos, false);
                    }
                }));

            }
        };

    }

    void showLogoutDialog(String message, int position, Boolean clearAll) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(this);
            mAlert.setCancelable(true);
            mAlert.setTitle(getString(R.string.app_name));
            mAlert.setMessage(message);
            mAlert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deletePosition = position;
                    NotificationActivity.this.clearAll = clearAll;
                    callDeleteNotificationListApi(position, clearAll);
                    dialogInterface.dismiss();
                }
            });
            mAlert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    myadapter .notifyDataSetChanged();
                    dialogInterface.dismiss();
                }
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }

    }

    private void callGetNotificationListApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.get_notifications);
        requestModel.setWebServiceTag(ApiConstant.get_notifications);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private void callDeleteNotificationListApi(int position, Boolean clearAll) {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        if ((!clearAll))
            paramsHashMap.put(Tags.notification_id, notificationList.get(position).id);
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delete_notification);
        requestModel.setWebServiceTag(ApiConstant.delete_notification);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    boolean clearAll = false;
    int deletePosition = 0;

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(this);
        switch (webServiceTag) {
            case ApiConstant.get_notifications:
                AppDelegate.Companion.LogT("Response==>" + message);
                ResponseModel orderDetailModel = new Gson().fromJson(message, ResponseModel.class);
                notificationList = orderDetailModel.Notifications;

                setAdapter(notificationList);
                break;
            case ApiConstant.read_notification:
                AppDelegate.Companion.LogT("Response==>" + message);
                if (notificationList != null && notificationList.size() > 0) {
                    notificationList.get(position).is_read = "1";
                    myadapter.notifyDataSetChanged();
                }
                break;
            case ApiConstant.delete_notification: {
                if (clearAll) {
                    notificationList = new ArrayList();
                    setAdapter(notificationList);
                    clearAll = false;
                        txt_clearall.setVisibility(View.GONE);
                } else {
                    notificationList.remove(deletePosition);
                    myadapter.notifyDataSetChanged();
                    //                    unread = Prefs.getInstance(this).getPreferenceIntData(Prefs.getInstance(this).UNREAD_COUNT)
                    //                    unread -= 1
                    try {
                        if (notificationList.isEmpty())
                            txt_clearall.setVisibility(View.GONE);
                        else
                            txt_clearall.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }

                }
            }
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message.getstrMessage());
    }

    int position = 0;
    int UPDATE_REQUESTCODE = 1200;

    @Override
    public void setOnItemClick(@NotNull String tag, int position) {
        if (AppDelegate.Companion.isValidString(notificationList.get(position).type))
            if (tag.equalsIgnoreCase(Tags.details)) {
                if (notificationList.get(position).type.equalsIgnoreCase(MyFirebaseMessagingService.NotificationType.deliveryBoyAssigned)) {
                    this.position = position;
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra(Tags.FROM, 2);
                    intent.putExtra(Tags.DATA, notificationList.get(position));
                    if (AppDelegate.Companion.isValidString(notificationList.get(position).item_id))
                        startActivityForResult(intent, UPDATE_REQUESTCODE);
                } else if (notificationList.get(position).type.equalsIgnoreCase(MyFirebaseMessagingService.NotificationType.deliveryStatusChange)) {
                    this.position = position;
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra(Tags.FROM, 2);
                    intent.putExtra(Tags.DATA, notificationList.get(position));
                    if (AppDelegate.Companion.isValidString(notificationList.get(position).item_id))
                        startActivityForResult(intent, UPDATE_REQUESTCODE);
                } else if (notificationList.get(position).type.equalsIgnoreCase(MyFirebaseMessagingService.NotificationType.general)) {
                    this.position = position;
                    if (notificationList.get(position).is_read.equals("0"))
                        callReadNotificationApi(notificationList.get(position).id);
                }
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUESTCODE) {
            if (data != null) {
                NotificationsModel notificationsModel = data.getParcelableExtra(Tags.data);
                notificationList.set(position, notificationsModel);
                myadapter.notifyDataSetChanged();
            }
        }
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
}
