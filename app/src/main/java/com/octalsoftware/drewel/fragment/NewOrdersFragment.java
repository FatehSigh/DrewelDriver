package com.octalsoftware.drewel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.activity.NewOrderDetailActivity;
import com.octalsoftware.drewel.fragment.Adapter.NewOrderFragmentAdapter;
import com.octalsoftware.drewel.utils.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 4/13/2018.
 */

public class NewOrdersFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.recycler_new_order)
    RecyclerView recyclerView;
    private NewOrderFragmentAdapter myadapter;
    private Toolbar toolbar;
    View.OnClickListener onClickListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_order_new, container, false);
        ButterKnife.bind(this, view);
        onClickListener = this;
        setAdapter();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppDelegate.Companion.setLocale(new Prefs(getActivity()).getDefaultLanguage(), getActivity());
//        callNewOrderApi();
    }
    private void setAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myadapter = new NewOrderFragmentAdapter(onClickListener);
        recyclerView.setAdapter(myadapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.imv_notification_bell:
                // Not implemented here
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_order_details:
                startActivity(new Intent(getActivity(), NewOrderDetailActivity.class));
                break;
        }
    }
}