package com.ourcitydeals.ctrl.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.R;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.ourcitydeals.ctrl.model.VendorsProductsListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewVendorsAdapter extends BaseAdapter {
	private ArrayList<VendorsProductsListData> listData;
	private LayoutInflater layoutInflater = null;
	Context mContext;
	//Animation animation = null;

	public GridViewVendorsAdapter(Activity context, ArrayList<VendorsProductsListData> listData) {
		this.mContext = context;
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View itemView, ViewGroup paramViewGroup) {
		ViewHolder holder = null;
		if (itemView == null) {
			holder = new ViewHolder();
			itemView = layoutInflater.inflate(R.layout.list_item_row_products_items, null, false);

			holder.listItemClickRL = (RelativeLayout)itemView.findViewById(R.id.listItemClickRLID);
			holder.soldby = (TextView) itemView.findViewById(R.id.soldby);
			holder.vendesc = (TextView) itemView.findViewById(R.id.vendesc);
			holder.uid = (TextView) itemView.findViewById(R.id.keyid);
			holder.title = (TextView) itemView.findViewById(R.id.title);
			holder.pic = (ImageView) itemView.findViewById(R.id.thumbnail);
			holder.fullView = (RelativeLayout) itemView.findViewById(R.id.rel);
			holder.available = (TextView) itemView.findViewById(R.id.available);
			holder.mrating = (RatingBar) itemView.findViewById(R.id.strs);

			// This will now execute only for the first time of each row
			itemView.setTag(holder);
		} else {
			holder = (ViewHolder) itemView.getTag();
		}

		VendorsProductsListData ci = listData.get(position);
		holder.uid.setText(ci.getproduct_id());
		holder.title.setText(ci.getproduct_name());
		holder.soldby.setVisibility(View.GONE);
		holder.vendesc.setVisibility(View.GONE);
		//holder.soldby.setText("" + ci.getvendor_name().toString() + "");
		//holder.vendesc.setText(ci.getvendor_description().toString());
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

		return itemView;
	}

	static class ViewHolder {
		protected TextView uid;
		protected TextView soldby;
		protected TextView vendesc;
		protected TextView title;
		protected TextView available;
		protected ImageView pic;
		protected RelativeLayout fullView;
		protected RatingBar mrating;
		RelativeLayout listItemClickRL;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void updateContent(ArrayList<VendorsProductsListData> updates) {
		listData.addAll(updates);
	    Log.d("DEBUG","Line 126 " + this.listData.size());              //--- Shows 3 again --
	    this.notifyDataSetChanged();                  //--- Screen still shows original 16 images--
	}
}