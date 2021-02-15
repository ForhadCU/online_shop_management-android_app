package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackCustomerSearchClickHandler;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.Customer;

import java.util.ArrayList;

public class RvAdapterPersonSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Customer> customerArrayList;
    private ICallbackCustomerSearchClickHandler iCallbackCustomerSearchClickHandler;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public RvAdapterPersonSearch(ArrayList<Customer> customerArrayList, ICallbackCustomerSearchClickHandler iCallbackCustomerSearchClickHandler) {
        this.customerArrayList = customerArrayList;
        this.iCallbackCustomerSearchClickHandler = iCallbackCustomerSearchClickHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      /*  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_customer_search_list_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_customer_search_list_layout, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return customerArrayList == null ? 0 : customerArrayList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return customerArrayList.get(position).getName() == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void filterList(ArrayList<Customer> filteredList) {
        customerArrayList = filteredList;
        notifyDataSetChanged();
    }

//    public String getItem()

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_rvCustomerName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Customer current = customerArrayList.get(getAdapterPosition());
            iCallbackCustomerSearchClickHandler.customerClickHandler(current.getName());
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        Customer current = customerArrayList.get(position);
        viewHolder.tvItem.setText(current.getName());

    }

/*    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewName;
        Context mContext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tv_rvCustomerName);
            mContext = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Customer current = customerArrayList.get(getAdapterPosition());
            iCallbackCustomerSearchClickHandler.customerClickHandler(current.getName());
        }
    }*/
}
