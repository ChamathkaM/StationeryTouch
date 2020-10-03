package com.example.stationerystore.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stationerystore.R;
import com.example.stationerystore.Model.Product;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> productList;
    private Context mContext;
    private ProductItemListener listener;

    public interface ProductItemListener {
        void onProductItemClick(Product product);
    }

    public ProductAdapter(Context mContext, List<Product> productList, ProductItemListener listener) {
        this.mContext = mContext;
        this.productList = productList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Product product = productList.get(position);

        //display single product details
        holder.txtName.setText(product.getProduct());
        holder.txtDescription.setText(product.getDescription());
        holder.txtPrice.setText("LKR " + product.getPrice());

        holder.pbItemImage.setVisibility(View.VISIBLE);
        holder.thumbnail.setVisibility(View.GONE);


        //display image if not null
        if (product.getImage() != null) {
            Picasso.with(mContext).load(product.getImage()).into(holder.thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    holder.pbItemImage.setVisibility(View.GONE);
                    holder.thumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.pbItemImage.setVisibility(View.GONE);
                    holder.thumbnail.setVisibility(View.VISIBLE);
                    Picasso.with(mContext).load(R.drawable.product).into(holder.thumbnail);
                }
            });
        } else {
            holder.pbItemImage.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.product).into(holder.thumbnail);
        }

        //product click event
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onProductItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final ProgressBar pbItemImage;
        public TextView txtName, txtDescription, txtPrice;
        public View container;

        public MyViewHolder(View view) {
            super(view);
            //initialize row UI components
            container = view;
            txtName = view.findViewById(R.id.txtName);
            txtDescription = view.findViewById(R.id.txtDescription);
            txtPrice = view.findViewById(R.id.txtPrice);
            thumbnail = view.findViewById(R.id.thumbnail);
            pbItemImage = view.findViewById(R.id.pb_item_image);
        }
    }
}