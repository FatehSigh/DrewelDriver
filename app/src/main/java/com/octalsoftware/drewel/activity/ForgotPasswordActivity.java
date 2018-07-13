package com.octalsoftware.drewel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;
import com.octalsoftware.drewel.utils.Prefs;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity implements ResponseInterface {


    private Toolbar mToolbar;
    @BindView(R.id.txt_email)
    AppCompatEditText txt_email;
    @BindView(R.id.btn_save)
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            Window window = getWindow();
//            Drawable background = getResources().getDrawable(R.color.white);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
        setContentView(R.layout.forgot_password);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                AppDelegate.Companion.hideKeyBoard(this);
                checkValidation();
                break;
        }
    }

    private void checkValidation() {
        if (!AppDelegate.Companion.isValidString(txt_email.getText().toString())) {
            AppDelegate.Companion.showToast(this, getResources().getString(R.string.please_enter_email_id));
        } else if (!AppDelegate.Companion.isValidEmail(txt_email.getText().toString())) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_valid_email));
        } else if (AppDelegate.Companion.haveNetworkConnection(this, false)) {
            callForgotPasswordApi();
        } else {
            AppDelegate.Companion.showToast(this, getString(R.string.please_check_your_internet_connection));
        }
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
}

    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
    }

    private void callForgotPasswordApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.email, txt_email.getText().toString());
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.forgotpassword);
        requestModel.setWebServiceTag(ApiConstant.forgotpassword);
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
        AppDelegate.Companion.hideProgressDialog(this);
        switch (webServiceTag) {
            case ApiConstant.forgotpassword:
                AppDelegate.Companion.LogT("Response ==>" + message);
                AppDelegate.Companion.showToast(this, successMsg);
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
}
