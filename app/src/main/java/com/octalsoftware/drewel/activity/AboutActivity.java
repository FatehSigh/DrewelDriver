package com.octalsoftware.drewel.activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.retrofitService.RestClient;
import com.octalsoftware.drewel.utils.Prefs;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_appversion)
    AppCompatTextView tv_appversion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutapp);
        ButterKnife.bind(this);
        initView();
    }
    @OnClick({R.id.tv_howto, R.id.tv_terms,R.id.tv_privacy, R.id.tv_faq})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_howto:
                AppDelegate.Companion.openURL(this,RestClient.url+"how-it-works");
                break;
            case R.id.tv_terms:
                AppDelegate.Companion.openURL(this,RestClient.url+"terms-of-use");
                break;
            case R.id.tv_privacy:
                AppDelegate.Companion.openURL(this,RestClient.url+"privacy-policy");
                break;
            case R.id.tv_faq:
                AppDelegate.Companion.openURL(this,RestClient.url+"faq");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
    protected void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(this).getDefaultLanguage(), this);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tv_appversion.setText(version + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
