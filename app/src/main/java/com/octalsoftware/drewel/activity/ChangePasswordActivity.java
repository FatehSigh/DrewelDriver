package com.octalsoftware.drewel.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity implements ResponseInterface {


    @BindView(R.id.txt_oldpassword)
    AppCompatEditText txt_oldpassword;
    @BindView(R.id.txt_newpassword)
    AppCompatEditText txt_newpassword;
    @BindView(R.id.txt_confirmpassword)
    AppCompatEditText txt_confirmpassword;
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
        setContentView(R.layout.change_password);
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
    @Override
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
    }

    private void checkValidation() {
        if (!AppDelegate.Companion.isValidString(txt_oldpassword.getText().toString())) {
            AppDelegate.Companion.showToast(this, getResources().getString(R.string.enter_old_password));
        } else if (!AppDelegate.Companion.isValidString(txt_newpassword.getText().toString())) {
            AppDelegate.Companion.showToast(this, getString(R.string.enter_new_password));
        } else if (!(AppDelegate.Companion.isValidPassword(txt_newpassword.getText().toString()))) {
            AppDelegate.Companion.showToast(this, getString(R.string.please_enter_valid_password));
        } else if (txt_oldpassword.getText().toString().equals(txt_newpassword.getText().toString())) {
            AppDelegate.Companion.showToast(this, getResources().getString(R.string.forchange_pass));
        } else if (!AppDelegate.Companion.isValidString(txt_confirmpassword.getText().toString())) {
            AppDelegate.Companion.showToast(this, getResources().getString(R.string.forconpass));
        } else if (!txt_confirmpassword.getText().toString().equals(txt_newpassword.getText().toString())) {
            AppDelegate.Companion.showToast(this, getResources().getString(R.string.formatch));
        } else if (AppDelegate.Companion.haveNetworkConnection(this, false)) {
            callChangePasswordApi();
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
        Toolbar mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* tv_status.setText(getText(R.string.accepted));
        btn_decline_order.setVisibility(View.GONE);
        btn_accept_order.setText(getText(R.string.pickup_from_vendor));*/
    }

    private void callChangePasswordApi() {
        AppDelegate.Companion.showProgressDialog(this);
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.old_password, txt_oldpassword.getText().toString());
        paramsHashMap.put(Tags.new_password, txt_newpassword.getText().toString());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.changePasswordUser);
        requestModel.setWebServiceTag(ApiConstant.changePasswordUser);
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
            case ApiConstant.changePasswordUser:
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
