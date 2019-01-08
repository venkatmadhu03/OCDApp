package com.ourcitydeals.ctrl.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.model.AllCategoriesList;
import com.ourcitydeals.ctrl.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class AllCategoriesListAdapter extends RecyclerView.Adapter<AllCategoriesListAdapter.ViewHolder> {

    Activity mContext;
    ArrayList<AllCategoriesList> listData;
    OnItemClickListener mItemClickListener;

    public AllCategoriesListAdapter(Activity context, ArrayList<AllCategoriesList> listData) {
        this.mContext = context;
        this.listData = listData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView categoryNameTitleTV;
        RelativeLayout listItemClickRL;

        public ViewHolder(View itemView) {
            super(itemView);
            listItemClickRL = (RelativeLayout)itemView.findViewById(R.id.listItemClickRLID);
            categoryNameTitleTV = (TextView) itemView.findViewById(R.id.categoryNameTitleTVID);

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_row_all_categories, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.categoryNameTitleTV.setText("" + Html.fromHtml(listData.get(position).getCat_name()));
    }
}