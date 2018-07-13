package com.octalsoftware.drewel.fragment.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.OrderModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by sharukhb on 3/13/2018.
 */

public class AcceptedOrderFragmentAdapter extends RecyclerView.Adapter<AcceptedOrderFragmentAdapter.ViewHolder> {
    public List<OrderModel> order;
    public View mView;
    public Context context;
    OnClickItemListener OnClickItemListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_order_details)
        public TextView btn_order_details;
        @BindView(R.id.tv_order_id_number)
        public TextView tv_order_id_number;
        @BindView(R.id.tv_placed_on_date)
        public TextView tv_placed_on_date;
        @BindView(R.id.tv_status)
        public TextView tv_status;
        @BindView(R.id.tv_delivery_date)
        public TextView tv_delivery_date;
        @BindView(R.id.tv_order_amount)
        public TextView tv_order_amount;
        @BindView(R.id.tv_delivery_address_in_miles)
        public TextView tv_delivery_address_in_miles;
        @BindView(R.id.tv_delivery_order_to_person)
        public TextView tv_delivery_order_to_person;
        @BindView(R.id.tv_delivery_order_address)
        public TextView tv_delivery_order_address;
        @BindView(R.id.btn_call_delivery_person)
        public Button btn_call_delivery_person;
        @BindView(R.id.btn_accept_order)
        public TextView btn_accept_order;
        @BindView(R.id.tv_delivery_time)
        AppCompatTextView tv_delivery_time;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, mView);
        }
    }

    public AcceptedOrderFragmentAdapter(Context context, List<OrderModel> order, OnClickItemListener OnClickItemListener) {
        this.OnClickItemListener = OnClickItemListener;
        this.context = context;
        this.order = order;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.child_accepted_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderModel orderModel = order.get(position);
        holder.tv_order_id_number.setText("#" + orderModel.order_id);
        try {
            Date startdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(orderModel.created_at);
            holder.tv_placed_on_date.setText(new SimpleDateFormat("dd MMM ''yy   h:mm a").format(startdate));
            Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(orderModel.delivery_date);
            holder.tv_delivery_date.setText(new SimpleDateFormat("EEE, dd MMM ''yy").format(endDate));
            Date starttime = new SimpleDateFormat("hh:mm:ss").parse(orderModel.delivery_start_time);
            Date endtime = new SimpleDateFormat("hh:mm:ss").parse(orderModel.delivery_end_time);
            holder.tv_delivery_time.setText(new SimpleDateFormat("h:mm a").format(starttime) + " to " + new SimpleDateFormat("h:mm a").format(endtime));

        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }

        holder.tv_status.setText(orderModel.order_delivery_status);
        holder.tv_order_amount.setText(orderModel.total_amount+" "+context.getString(R.string.omr));
        DecimalFormat df = new DecimalFormat(".##");
        if (AppDelegate.Companion.isValidString(orderModel.distance))
            holder.tv_delivery_address_in_miles.setText(df.format(Double.parseDouble(orderModel.distance)) + " Miles");
        holder.tv_delivery_order_to_person.setText(orderModel.deliver_to);
        holder.tv_delivery_order_address.setText(orderModel.delivery_address);
        holder.btn_call_delivery_person.setText(orderModel.deliver_mobile);
        holder.btn_call_delivery_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDelegate.Companion.call(context, orderModel.deliver_mobile);
            }
        });

        holder.btn_order_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDelegate.Companion.call(context, orderModel.deliver_mobile);
            }
        });
        holder.btn_accept_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnClickItemListener != null)
                    OnClickItemListener.setOnItemClick(Tags.pick_from_vendor, position);
            }
        });
        try {
            holder.btn_order_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (OnClickItemListener != null)
                        OnClickItemListener.setOnItemClick(Tags.details, position);

                }
            });
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return order.size();
    }
}
