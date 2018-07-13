package com.octalsoftware.drewel.fragment.Adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octalsoftware.drewel.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by sharukhb on 3/13/2018.
 */

public class EarningAdapter extends RecyclerView.Adapter<EarningAdapter.ViewHolder> {
    View.OnClickListener onclickListener;

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_id_number)
        AppCompatTextView tv_order_id_number;
        @BindView(R.id.tv_order_date_time)
        AppCompatTextView tv_order_date_time;
        @BindView(R.id.tv_earning_amount)
        AppCompatTextView tv_earning_amount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public EarningAdapter(View.OnClickListener clickListener) {
        this.onclickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.child_earning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_earning_amount.setText("$" + "");
        holder.tv_order_id_number.setText("#" + "");

//        try {
//            Date startdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(orderModel.created_at);
//            holder.tv_order_date_time.setText(new SimpleDateFormat("dd MMM ''yy   h:mm a").format(startdate));
//            Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderModel.delivery_date);
//            holder.tv_order_date_time.setText(new SimpleDateFormat("EEE, dd MMM ''yy").format(endDate));
//        } catch (Exception e) {
//            AppDelegate.Companion.LogE(e);
//        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
