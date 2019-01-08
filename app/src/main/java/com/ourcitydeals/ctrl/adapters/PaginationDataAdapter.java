package com.ourcitydeals.ctrl.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ourcitydeals.ctrl.R;
import com.ourcitydeals.ctrl.model.HomeProductsData;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginationDataAdapter extends RecyclerView.Adapter {
	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;

	private List<HomeProductsData> studentList;

	// The minimum amount of items to have below your current scroll position
	// before loading more.
	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;

	

	public PaginationDataAdapter(List<HomeProductsData> students, RecyclerView recyclerView) {
		studentList = students;

		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();


					recyclerView
					.addOnScrollListener(new RecyclerView.OnScrollListener() {
						@Override
						public void onScrolled(RecyclerView recyclerView,
											   int dx, int dy) {
							super.onScrolled(recyclerView, dx, dy);

							totalItemCount = linearLayoutManager.getItemCount();
							lastVisibleItem = linearLayoutManager
									.findLastVisibleItemPosition();
							if (!loading
									&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
								// End has been reached
								// Do something
								if (onLoadMoreListener != null) {
									onLoadMoreListener.onLoadMore();
								}
								loading = true;
							}
						}
					});
		}
	}

	@Override
	public int getItemViewType(int position) {
		return studentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
													  int viewType) {
		RecyclerView.ViewHolder vh;
		if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pagination_list_row, parent, false);
			vh = new StudentViewHolder(v);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pagination_progress_item, parent, false);
			vh = new ProgressViewHolder(v);
		}
		return vh;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof StudentViewHolder) {
			HomeProductsData singleStudent= (HomeProductsData) studentList.get(position);
			((StudentViewHolder) holder).tvName.setText(singleStudent.getproduct_name());
			((StudentViewHolder) holder).tvEmailId.setText(singleStudent.getproduct_id());
			((StudentViewHolder) holder).student= singleStudent;
		} else {
			((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
		}
	}

	public void setLoaded() {
		loading = false;
	}

	@Override
	public int getItemCount() {
		return studentList.size();
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}


	//
	public static class StudentViewHolder extends RecyclerView.ViewHolder {
		public TextView tvName;
		
		public TextView tvEmailId;
		
		public HomeProductsData student;

		public StudentViewHolder(View v) {
			super(v);
			tvName = (TextView) v.findViewById(R.id.tvName);
			
			tvEmailId = (TextView) v.findViewById(R.id.tvEmailId);
			
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(v.getContext(),"OnClick :" + student.getproduct_name() + " \n "+student.getprice(),Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public static class ProgressViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public ProgressViewHolder(View v) {
			super(v);
			progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		}
	}
}