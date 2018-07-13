package com.octalsoftware.drewel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.octalsoftware.drewel.activity.NotificationActivity;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.fragment.CompletedOrdersFragment;
import com.octalsoftware.drewel.fragment.EarningFragment;
import com.octalsoftware.drewel.fragment.MoreFragment;
import com.octalsoftware.drewel.fragment.OrdersFragment;
import com.octalsoftware.drewel.model.NotificationsModel;
import com.octalsoftware.drewel.model.ResponseModel;
import com.octalsoftware.drewel.model.UserDataModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.LocationService;
import com.octalsoftware.drewel.utils.Prefs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class HomeActivity extends AppCompatActivity implements ResponseInterface {

    private static final String TAG = HomeActivity.class.getName();
    private static final int MYPERMISSIONCODE = 1111;
    @BindView(R.id.navigationView)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imv_notification_bell)
    ImageView imv_notification_bell;
    @BindView(R.id.switch_off_on_notification)
    SwitchCompat switch_off_on_notification;
    @BindView(R.id.txt_toolbar)
    TextView txt_toolbar;
    public FragmentManager mFragmentManager = null;
    FragmentTransaction fragmentTransaction = null;
    private Fragment mFragment;
    private boolean firstTimeBackPressed = true;
    boolean isChecked = false;
    @BindView(R.id.txt_counter)
    TextView txt_counter;
    @BindView(R.id.rl_notif)
    RelativeLayout rl_notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermissions();
        try {
            ButterKnife.bind(this);
            disableShiftMode(bottomNavigationView);
            mFragmentManager = getSupportFragmentManager();
            mFragment = new OrdersFragment();
            populateFragment("", mFragment);
            bottomNavigationView.setOnNavigationItemSelectedListener(navigationSelectedListener);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        callDriverAvailability(1);
        initView();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_COUNT");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @OnClick({R.id.imv_notification_bell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imv_notification_bell:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
        txt_counter.setVisibility(View.GONE);
        callGetNotificationListApi();
    }

    private void initView() {
        if (AppDelegate.Companion.isValidString(Objects.requireNonNull(new Prefs(this).getUserdata()).is_notification)) {
            isChecked = !Objects.requireNonNull(new Prefs(this).getUserdata()).is_notification.equals("0");
        }
        if (!isChecked)
            showAlert(this, "", getString(R.string.notif_turn_on));
        switch_off_on_notification.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked)
                callDriverAvailability(1);
            else
                callDriverAvailability(2);
        });
    }

    private void checkPermissions() {
        String[] permissionArrays = new String[]{
                ACCESS_FINE_LOCATION,
                WRITE_EXTERNAL_STORAGE,
                ACCESS_COARSE_LOCATION,
                CAMERA
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                startService(new Intent(this, LocationService.class));
            requestPermissions(permissionArrays, MYPERMISSIONCODE);
        } else {
            startService(new Intent(this, LocationService.class));
        }
    }

    public int NEW_ORDER = 1, COMPLETED_ORDER = 2, EARNINGS = 3, MORE = 4;

    void changeToolbarStyle(int value) {
        if (value == EARNINGS || value == MORE) {
            txt_toolbar.setTextColor(getResources().getColor(R.color.black));
            toolbar.setBackgroundColor(getResources().getColor(R.color.white));
            imv_notification_bell.setImageResource(R.mipmap.notificationgrey);
            if (value == MORE)
                switch_off_on_notification.setVisibility(VISIBLE);
            else {
                switch_off_on_notification.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                Window window = getWindow();
                Drawable background = getResources().getDrawable(R.color.white);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                window.setBackgroundDrawable(background);
            }
        } else {
            txt_toolbar.setTextColor(getResources().getColor(R.color.offwhite));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            imv_notification_bell.setImageResource(R.mipmap.notification);
            switch_off_on_notification.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
                Window window = getWindow();
                Drawable background = getResources().getDrawable(R.color.colorPrimary);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                window.setBackgroundDrawable(background);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MYPERMISSIONCODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        String permissionNAME = permissions[i];
                        if (CAMERA.equalsIgnoreCase(permissionNAME)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                //CAMERA PERMISSION GRANTERgranted
                                Log.wtf(TAG, "Provided CAMERA");
                            } else {
                                Log.wtf(TAG, " CAMERA");
                            }
                        }

                        if (WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permissionNAME)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                                //STORAGE PERMISSION PROVIDED
                                Log.wtf(TAG, "Provided WRITE_EXTERNAL_STORAGE");
                            else
                                Log.wtf(TAG, "WRITE_EXTERNAL_STORAGE");
                        }

                        if (ACCESS_FINE_LOCATION.equalsIgnoreCase(permissionNAME)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Log.wtf(TAG, "Provided ACCESS_FINE_LOCATION");
                                startService(new Intent(this, LocationService.class));
                            } else {
                                Log.wtf(TAG, "ACCESS_FINE_LOCATION");
                            }
                        }

                        if (ACCESS_COARSE_LOCATION.equalsIgnoreCase(permissionNAME)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Log.wtf(TAG, "Provided-ACCESS_COARSE_LOCATION");
                                startService(new Intent(this, LocationService.class));
                            } else {
                                Log.wtf(TAG, "ACCESS_COARSE_LOCATION");
                            }
                        }
                    }
                } else {
                }

                break;

        }

    }

    public void setToolbarTitle(String title_name) {
        txt_toolbar.setText(title_name);
    }

    public void populateFragment(String fragmentName, Fragment mFragment) {
        fragmentTransaction = mFragmentManager.beginTransaction();
        if (mFragment != null) {
            try {
                fragmentTransaction.replace(R.id.container, mFragment);
                // fragmentTransaction.replace(R.id.fragment_place, mFragment).addToBackStack(fragmentName);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView bottomNavigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e(TAG, e.toString());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void callGetNotificationListApi() {
//        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.user_id, new Prefs(this).getUserdata().user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.get_notifications);
        requestModel.setWebServiceTag(ApiConstant.get_notifications);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_order:
//                    changeToolbarStyle(NEW_ORDER);
                    mFragment = new OrdersFragment();
                    populateFragment("", mFragment);
                    setToolbarTitle("" + getText(R.string.orders));
                    showNotificationBellIcon();
                    showSwitchCompatOnOff();
                    break;
                case R.id.navigation_completed:
//                    changeToolbarStyle(COMPLETED_ORDER);
                    mFragment = new CompletedOrdersFragment();
                    populateFragment("", mFragment);
                    setToolbarTitle("" + getText(R.string.completed_orders));
                    hideNotificationBellIcon();
                    break;
               /* case R.id.navigation_earning:
//                    changeToolbarStyle(EARNINGS);
                    mFragment = new EarningFragment();
                    populateFragment("", mFragment);
                    setToolbarTitle(getString(R.string.my_earnings));
                    hideNotificationBellIcon();
                    hideSwitchCompatOnOff();
                    break;*/
                case R.id.navigation_more:
//                    changeToolbarStyle(MORE);
                    mFragment = new MoreFragment();
                    populateFragment("", mFragment);
                    setToolbarTitle("" + getText(R.string.more));
                    showNotificationBellIcon();
                    hideSwitchCompatOnOff();
                    break;
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (firstTimeBackPressed) {
            Toast.makeText(this, getText(R.string.Press_again_to_exit), Toast.LENGTH_SHORT).show();
            firstTimeBackPressed = false;
        } else {
            super.onBackPressed();
        }
    }

    public void hideNotificationBellIcon() {
        rl_notif.setVisibility(GONE);
        imv_notification_bell.setVisibility(GONE);
    }

    public void showNotificationBellIcon() {
        rl_notif.setVisibility(VISIBLE);
        imv_notification_bell.setVisibility(VISIBLE);
    }

    public void hideSwitchCompatOnOff() {
        switch_off_on_notification.setVisibility(GONE);
    }

    public void showSwitchCompatOnOff() {
        switch_off_on_notification.setVisibility(VISIBLE);
    }

    private void callNotificationTurnOnApi(boolean isChecked) {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        if (isChecked)
            paramsHashMap.put(Tags.is_notification, "on");
        else
            paramsHashMap.put(Tags.is_notification, "off");
        paramsHashMap.put(Tags.user_id, new Prefs(this).getUserdata().user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_notification_status);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_notification_status);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private List<NotificationsModel> notificationList;

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        AppDelegate.Companion.hideProgressDialog(this);
        switch (webServiceTag) {
            case ApiConstant.delivery_boy_notification_status:
                AppDelegate.Companion.hideProgressDialog(this);
                AppDelegate.Companion.showToast(this, successMsg);
                UserDataModel userDataModel1 = new Prefs(this).getUserdata();
                userDataModel1.is_notification = 1 + "";
                new Prefs(this).setUserdata(userDataModel1);
                break;
            case ApiConstant.updateLocation:
                AppDelegate.Companion.hideProgressDialog(this);
                AppDelegate.Companion.showToast(this, successMsg);
                if (switch_off_on_notification.isChecked()) {
                    switch_off_on_notification.setChecked(false);
                } else
                    switch_off_on_notification.setChecked(true);
                break;
            case ApiConstant.driverStatus:
                AppDelegate.Companion.showToast(this, successMsg);
                break;
            case ApiConstant.get_notifications:
                AppDelegate.Companion.LogT("Response==>" + message);
                ResponseModel orderDetailModel = new Gson().fromJson(message, ResponseModel.class);
                notificationList = orderDetailModel.Notifications;
                getCount(notificationList);
                break;
        }
    }

    private void getCount(List<NotificationsModel> notificationList) {
        int counter = 0;
        for (int i = 0; i < notificationList.size(); i++) {
            if (notificationList.get(i).is_read.equals("0"))
                counter++;
        }
        if (counter > 0) {
            txt_counter.setText(counter + "");
            txt_counter.setVisibility(VISIBLE);
        } else
            txt_counter.setVisibility(View.GONE);
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

    void showAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(true);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.turn_on), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isChecked = true;
                    callNotificationTurnOnApi(isChecked);
                    dialogInterface.dismiss();
                }
            });
            mAlert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }

    }

    private void callDriverAvailability(int is_available) {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.status, is_available + "");
        paramsHashMap.put(Tags.user_id, new Prefs(this).getUserdata().user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
//        paramsHashMap.put(Tags.device_id, new Prefs(this).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.driverStatus);
        requestModel.setWebServiceTag(ApiConstant.driverStatus);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "This is the broadcast", Toast.LENGTH_SHORT).show();
            callGetNotificationListApi();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
