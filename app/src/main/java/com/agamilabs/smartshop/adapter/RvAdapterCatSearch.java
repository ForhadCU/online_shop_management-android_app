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
import com.agamilabs.smartshop.Interfaces.IClickHandler;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.ProductCatModel;
import com.agamilabs.smartshop.model.Products;

import java.util.ArrayList;

public class RvAdapterCatSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProductCatModel> productCatModelArrayList;
    private IClickHandler iClickHandler;
    private ICallbackCatSearchClickHandler iCallbackCatSearchClickHandler;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private int pageNo;

    public RvAdapterCatSearch(ArrayList<ProductCatModel> productCatModelArrayList, IClickHandler iClickHandler, int catPageNo) {
        this.productCatModelArrayList = productCatModelArrayList;
        this.iClickHandler = iClickHandler;
        this.pageNo = catPageNo;
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
        return productCatModelArrayList == null ? 0 : productCatModelArrayList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return productCatModelArrayList.get(position).getCattext() == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void filterList(ArrayList<ProductCatModel> filteredList) {
        productCatModelArrayList = filteredList;
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
            ProductCatModel current = productCatModelArrayList.get(getAdapterPosition());
            iClickHandler.mCatClick(current.getCattext(), current.getCatid(), current.getParentcatid());
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
        ProductCatModel current = productCatModelArrayList.get(position);

        viewHolder.tvItem.setText(current.getCattext());
       /* viewHolder.textViewSku.setText(current.getSku());
        viewHolder.textViewQty.setText(current.getQty());*/
    }
}
