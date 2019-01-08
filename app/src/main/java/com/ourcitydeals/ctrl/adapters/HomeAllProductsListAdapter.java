package com.ourcitydeals.ctrl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.R;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by HARIKRISHNA on 02/20/2016. at CaretTech
 */

public class HomeAllProductsListAdapter extends RecyclerView.Adapter<HomeAllProductsListAdapter.ViewHolder> {
    Context mContext;
    OnItemClickListener mItemClickListener;
    private ArrayList<HomeProductsData> listData;

    public HomeAllProductsListAdapter(Context context, ArrayList<HomeProductsData> listData) {
        this.mContext = context;
        this.listData = listData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView uid;
        protected TextView soldby;
        protected TextView vendesc;
        protected TextView title;
        protected TextView available;
        protected ImageView pic;
        protected RelativeLayout fullView;
        protected RatingBar mrating;
        RelativeLayout listItemClickRL;

        public ViewHolder(View itemView) {
            super(itemView);
            listItemClickRL = (RelativeLayout)itemView.findViewById(R.id.listItemClickRLID);
            soldby = (TextView) itemView.findViewById(R.id.soldby);
            vendesc = (TextView) itemView.findViewById(R.id.vendesc);
            uid = (TextView) itemView.findViewById(R.id.keyid);
            title = (TextView) itemView.findViewById(R.id.title);
            pic = (ImageView) itemView.findViewById(R.id.thumbnail);
            fullView = (RelativeLayout) itemView.findViewById(R.id.rel);
            available = (TextView) itemView.findViewById(R.id.available);
            mrating = (RatingBar) itemView.findViewById(R.id.strs);

            listItemClickRL.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public int getItemCount() {
        return listData.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row_products_items, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        HomeProductsData ci = listData.get(position);
        holder.uid.setText(ci.getproduct_id());
        holder.title.setText(ci.getproduct_name());
        holder.soldby.setText("" + ci.getvendor_name().toString() + "");
        holder.vendesc.setText(ci.getvendor_description().toString());
        if (ci.getprice().equalsIgnoreCase("0") || ci.getprice().isEmpty()) {
            holder.available.setText("");
        } else {
            holder.available.setText(""+mContext.getResources().getString(R.string.rs)+ " " + ci.getprice());
        }

        if (ci.getimageUrl() != null && !ci.getimageUrl().isEmpty()) {
            Picasso.with(mContext).load(ci.getimageUrl()).placeholder(R.drawable.placeholder_pro).into(holder.pic);
        } else {
            holder.pic.setBackgroundResource(R.drawable.placeholder_pro);
        }
    }
}