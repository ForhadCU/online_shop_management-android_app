package com.agamilabs.smartshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackCatSearchClickHandler;
import com.agamilabs.smartshop.Interfaces.ICallbackClickHandler;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.Products;

import java.util.ArrayList;

public class RvAdapterProductSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Products> productsArrayList;
    private ICallbackClickHandler iCallbackClickHandler;
    private ICallbackCatSearchClickHandler iCallbackCatSearchClickHandler;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int productPageNo;

    public RvAdapterProductSearch(ArrayList<Products> productsArrayList, ICallbackClickHandler iCallbackClickHandler, int productPageNo) {
        this.productsArrayList = productsArrayList;
        this.iCallbackClickHandler = iCallbackClickHandler;
        this.productPageNo = productPageNo;
    }
    public RvAdapterProductSearch(ArrayList<Products> productsArrayList, ICallbackCatSearchClickHandler iCallbackCatSearchClickHandler, int productPageNo) {
        this.productsArrayList = productsArrayList;
        this.iCallbackCatSearchClickHandler = iCallbackCatSearchClickHandler;
        this.productPageNo = productPageNo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      /*  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_customer_search_list_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_product_search_list_layout, parent, false);
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
        return productsArrayList == null ? 0 : productsArrayList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return productsArrayList.get(position).getItem_name() == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void filterList(ArrayList<Products> filteredList) {
        productsArrayList = filteredList;
        notifyDataSetChanged();
    }

//    public String getItem()

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvItem, textViewSku, textViewQty;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_rvProductName);
     /*       textViewSku = itemView.findViewById(R.id.tv_rvProductSku);
            textViewQty = itemView.findViewById(R.id.tv_rvProductQty);*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Products current = productsArrayList.get(getAdapterPosition());
            iCallbackClickHandler.productClickHandler(current.getItem_name(), current.getId());
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
        Products current = productsArrayList.get(position);

        viewHolder.tvItem.setText(current.getItem_name());
       /* viewHolder.textViewSku.setText(current.getSku());
        viewHolder.textViewQty.setText(current.getQty());*/
    }
}
