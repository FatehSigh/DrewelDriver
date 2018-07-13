package com.octalsoftware.drewel.fragment.Adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.octalsoftware.drewel.R;
import com.octalsoftware.drewel.application.DrewelApplication;
import com.octalsoftware.drewel.model.ProductModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharukhb on 3/13/2018.
 */

public class SimilarOrderItemAdapter extends RecyclerView.Adapter<SimilarOrderItemAdapter.ViewHolder> {
    List<ProductModel> products;

    public SimilarOrderItemAdapter(List<ProductModel> products) {
        this.products = products;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imv_product)
        public ImageView imv_product;
        @BindView(R.id.tv_name)
        public AppCompatTextView tv_name;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.child_similar_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductModel productModel = products.get(position);
        ImageLoader.getInstance().displayImage(productModel.product_image, holder.imv_product, DrewelApplication.options);
        holder.tv_name.setText(productModel.product_name);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
