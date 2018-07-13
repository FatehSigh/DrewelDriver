package com.octalsoftware.drewel.fragment.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octalsoftware.drewel.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by sharukhb on 3/13/2018.
 */

public class NewOrderFragmentAdapter extends RecyclerView.Adapter<NewOrderFragmentAdapter.ViewHolder> {
    private View.OnClickListener onClickListener;
    public View mView;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_order_details)
        public TextView btn_order_details;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, mView);
        }
    }

    public NewOrderFragmentAdapter(View.OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.child_new_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.btn_order_details.setOnClickListener(onClickListener);
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
