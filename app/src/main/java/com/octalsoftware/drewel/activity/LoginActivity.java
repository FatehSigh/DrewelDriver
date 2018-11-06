package com.octalsoftware.drewel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.HomeActivity;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.model.UserDataModel;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity implements ResponseInterface, View.OnClickListener {

    @BindView(R.id.et_email)
    TextInputEditText et_email;

    @BindView(R.id.et_password)
    TextInputEditText et_password;

    private Toolbar mToolbar;
    @BindView(R.id.tv_change_language)
    TextView tv_change_language;
    @BindView(R.id.txt_forgot_password)
    TextView txt_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
        if (new Prefs(this).getDefaultLanguage().equals(Tags.LANGUAGE_ENGLISH))
            tv_change_language.setText(getText(R.string.english));
        else
            tv_change_language.setText(getText(R.string.arabic));

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        findViewById(R.id.txt_login).setOnClickListener(this);
        findViewById(R.id.ll_change_language).setOnClickListener(this);
        findViewById(R.id.txt_forgot_password).setOnClickListener(this);
//      et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void callLoginGApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.email, et_email.getText().toString());
        paramsHashMap.put(Tags.password, et_password.getText().toString());
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.device_id, new Prefs(this).getFcMtokeninTemp());
        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.login);
        requestModel.setWebServiceTag(ApiConstant.login);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
        AppDelegate.Companion.showToast(this, message);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        switch (webServiceTag) {
            case ApiConstant.login:
                AppDelegate.Companion.hideProgressDialog(this);
                AppDelegate.Companion.LogT("Response ==>" + message);
                UserDataModel userDataModel = new Gson().fromJson(message, UserDataModel.class);
                AppDelegate.Companion.LogT("userDataModel==>" + userDataModel);
                AppDelegate.Companion.showToast(this, successMsg);
                new Prefs(this).setUserdata(userDataModel);
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_login:
                AppDelegate.Companion.hideKeyBoard(this);
                checkValidation();
                break;
            case R.id.ll_change_language:
                showAlert(this, "", getString(R.string.choose_prefered_language));
                break;
            case R.id.txt_forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void checkValidation() {
//        callLoginGApi();
        if (!AppDelegate.Companion.isValidString(et_email.getText().toString())) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_email_id));
        } else if (!AppDelegate.Companion.isValidEmail(et_email.getText().toString())) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_valid_email));
        } else if (!AppDelegate.Companion.isValidString(et_password.getText().toString())) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_password));
//        } else if (!AppDelegate.Companion.isValidPassword(et_password.getText().toString())) {
//            AppDelegate.Companion.showSnackBar(et_password, getString(R.string.please_enter_valid_password));
//        } else if (!AppDelegate.Companion.isValidPassword(et_password.getText().toString())) {
//            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_valid_password));
        } else if (!AppDelegate.Companion.haveNetworkConnection(this)) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_check_your_internet_connection));
        } else
            callLoginGApi();
    }

    void showAlert(Context mContext, String Title, String Message) {
        try {
            AlertDialog.Builder mAlert = new AlertDialog.Builder(mContext);
            mAlert.setCancelable(true);
            mAlert.setTitle(Title);
            mAlert.setMessage(Message);
            mAlert.setPositiveButton(getString(R.string.english), (dialogInterface, i) -> {
                tv_change_language.setText(getString(R.string.english));
                new Prefs(LoginActivity.this).setDefaultLanguage(Tags.LANGUAGE_ENGLISH);
                AppDelegate.Companion.setLocale(new Prefs(LoginActivity.this).getDefaultLanguage(), LoginActivity.this);
                dialogInterface.dismiss();
                refreshActivity();
            });
            mAlert.setNegativeButton(getString(R.string.arabic), (dialogInterface, i) -> {
                tv_change_language.setText(getString(R.string.arabic));
                new Prefs(LoginActivity.this).setDefaultLanguage(Tags.LANGUAGE_ARABIC);
                AppDelegate.Companion.setLocale(new Prefs(LoginActivity.this).getDefaultLanguage(), LoginActivity.this);
                dialogInterface.dismiss();
                refreshActivity();
            });
            mAlert.show();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }

    }

    private void refreshActivity() {
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }
}
