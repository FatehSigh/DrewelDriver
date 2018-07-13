package com.octalsoftware.drewel.fragment;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.activity.AboutActivity;
import com.octalsoftware.drewel.activity.ChangePasswordActivity;
import com.octalsoftware.drewel.activity.LoginActivity;
import com.octalsoftware.drewel.activity.MyAccountActivity;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.model.UserDataModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestClient;
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

public class MoreFragment extends Fragment implements ResponseInterface {
    @BindView(R.id.tv_my_account)
    AppCompatTextView tv_my_account;
    @BindView(R.id.tv_setting)
    AppCompatTextView tv_setting;
    @BindView(R.id.tv_about_app)
    AppCompatTextView tv_about_app;
    @BindView(R.id.tv_change_language)
    AppCompatTextView tv_change_language;
    @BindView(R.id.tv_contact_us)
    AppCompatTextView tv_contact_us;
    @BindView(R.id.tv_about_us)
    AppCompatTextView tv_about_us;
    @BindView(R.id.tv_rate_app)
    AppCompatTextView tv_rate_app;
    @BindView(R.id.tv_logout)
    AppCompatTextView tv_logout;
    @BindView(R.id.switch_off_on_notification)
    SwitchCompat switch_off_on_notification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
    }

    boolean isChecked = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (AppDelegate.Companion.isValidString(Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).is_notification)) {
            if (Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).is_notification.equals("0"))
                isChecked = false;
            else isChecked = true;
        }
        switch_off_on_notification.setChecked(isChecked);
        switch_off_on_notification.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            this.isChecked = isChecked;
            callNotificationTurnOnApi(isChecked);
        });
    }

    @OnClick({R.id.tv_my_account, R.id.tv_change_password, R.id.tv_about_app, R.id.tv_change_language, R.id.tv_contact_us, R.id.tv_about_us, R.id.tv_rate_app, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_my_account:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;
            case R.id.tv_change_password:
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;
            case R.id.tv_about_app:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.tv_change_language:
                showAlert(getActivity(), "", getString(R.string.choose_prefered_language));
                break;
            case R.id.tv_contact_us:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"drewel.support@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;
            case R.id.tv_about_us:
                AppDelegate.Companion.openURL(getActivity(), RestClient.url + "about-us");
                break;

            case R.id.tv_rate_app:
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }
                break;

            case R.id.tv_logout:
                logoutAlert(getActivity(), getString(R.string.logout), getString(R.string.logout_message));
//                callApiLogout();
                break;
        }
    }

    private void callApiLogout() {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(getActivity()).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());
//      paramsHashMap.put(Tags.device_id, new Prefs(getActivity()).getFcMtokeninTemp());
//      paramsHashMap.put(Tags.device_type, "android");
        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.logout);
        requestModel.setWebServiceTag(ApiConstant.logout);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

    private void callNotificationTurnOnApi(boolean isChecked) {
        AppDelegate.Companion.showProgressDialog(getActivity());
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        if (isChecked)
            paramsHashMap.put(Tags.is_notification, "on");
        else
            paramsHashMap.put(Tags.is_notification, "off");
        paramsHashMap.put(Tags.user_id, new Prefs(getActivity()).getUserdata().user_id);
        paramsHashMap.put(Tags.language, new Prefs(getActivity()).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.delivery_boy_notification_status);
        requestModel.setWebServiceTag(ApiConstant.delivery_boy_notification_status);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(getActivity(), true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        if (!isAdded())
            return;
        AppDelegate.Companion.hideProgressDialog(getActivity());
        AppDelegate.Companion.showToast(getActivity(), message);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        if (!isAdded())
            return;
        AppDelegate.Companion.hideProgressDialog(getActivity());
        switch (webServiceTag) {
            case ApiConstant.logout:
                AppDelegate.Companion.LogT("Response ==>" + message);
                AppDelegate.Companion.showToast(getActivity(), successMsg);
                String defaultLanguage = new Prefs(getActivity()).getDefaultLanguage();
                new Prefs(getActivity()).clearSharedPreference();
                new Prefs(getActivity()).setDefaultLanguage(defaultLanguage);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case ApiConstant.delivery_boy_notification_status:
                AppDelegate.Companion.showToast(getActivity(), successMsg);
                UserDataModel userDataModel1 = new Prefs(getActivity()).getUserdata();
                if (isChecked) {
                    assert userDataModel1 != null;
                    userDataModel1.is_notification = 1 + "";
                } else {
                    assert userDataModel1 != null;
                    userDataModel1.is_notification = 0 + "";
                }
                new Prefs(getActivity()).setUserdata(userDataModel1);
                break;
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        if (!isAdded())
            return;
        AppDelegate.Companion.hideProgressDialog(getActivity());
        AppDelegate.Companion.showToast(getActivity(), message);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        if (!isAdded())
            return;
        AppDelegate.Companion.hideProgressDialog(getActivity());
        AppDelegate.Companion.showToast(getActivity(), message.getstrMessage());
    }

    void showAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(true);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.english), (dialogInterface, i) -> {
                new Prefs(getActivity()).setDefaultLanguage(Tags.LANGUAGE_ENGLISH);
                AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
                dialogInterface.dismiss();
                refreshActivity();
            });
            mAlert.setNegativeButton(getString(R.string.arabic), (dialogInterface, i) -> {
                new Prefs(getActivity()).setDefaultLanguage(Tags.LANGUAGE_ARABIC);
                AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
                dialogInterface.dismiss();
                refreshActivity();

            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
    }

    void logoutAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(true);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                callApiLogout();
                dialogInterface.dismiss();
            });
            mAlert.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
    }

    private void refreshActivity() {
        Intent intent = getActivity().getIntent();
        startActivity(intent);
        getActivity().finish();
    }
}
