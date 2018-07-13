package com.octalsoftware.drewel.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.octalsoftware.drewel.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMoneyActivity extends AppCompatActivity {

    @BindView(R.id.btn_accept_order)
    Button btn_accept_order;

    @BindView(R.id.edt_profile_first_name)
    EditText edt_profile_first_name;

    public Toolbar mToolbar;
    private boolean noSuccess;
    private boolean dead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        ButterKnife.bind(this);
        initView();
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* tv_status.setText(getText(R.string.accepted));
        btn_decline_order.setVisibility(View.GONE);
        btn_accept_order.setText(getText(R.string.pickup_from_vendor));*/
    }


}
