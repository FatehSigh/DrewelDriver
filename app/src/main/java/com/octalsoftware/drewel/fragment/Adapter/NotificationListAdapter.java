package com.octalsoftware.drewel.fragment.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.application.DrewelApplication;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.interfaces.OnClickItemListener;
import com.octalsoftware.drewel.model.NotificationsModel;
import com.octalsoftware.drewel.utils.Prefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 3/13/2018.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    private OnClickItemListener OnClickItemListener;
    private Context context;
    private List<NotificationsModel> notificationsModelList;

    public NotificationListAdapter(Context context, List<NotificationsModel> notificationsModelList, OnClickItemListener OnClickItemListener) {
        this.OnClickItemListener = OnClickItemListener;
        this.context = context;
        this.notificationsModelList = notificationsModelList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        public AppCompatTextView tv_title;
        @BindView(R.id.tv_order_date_time)
        public AppCompatTextView tv_order_date_time;
        @BindView(R.id.ll_main)
        LinearLayout ll_main;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.child_notification, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationsModel notificationsModel = notificationsModelList.get(position);
        if (notificationsModel != null) {
            if (AppDelegate.Companion.isValidString(notificationsModel.is_read))
                if (notificationsModel.is_read.equalsIgnoreCase("0"))
                    holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary_verylight));
                else
                    holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.white));
            else
                holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.white));
//            if (new Prefs(context).getDefaultLanguage().equals(Tags.LANGUAGE_ARABIC))
//                holder.tv_title.setText(notificationsModel.message_arabic);
//            else
                holder.tv_title.setText(notificationsModel.message);

            try {
                Date startdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(notificationsModel.created);
                holder.tv_order_date_time.setText(new SimpleDateFormat("dd MMM ''yy    h:mm a").format(startdate));
            } catch (Exception e) {
                AppDelegate.Companion.LogE(e);
            }
            holder.ll_main.setOnClickListener(v -> {
                OnClickItemListener.setOnItemClick(Tags.details, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return notificationsModelList.size();
    }
}
