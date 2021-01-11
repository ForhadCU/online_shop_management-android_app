package com.agamilabs.smartshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Models.InvoiceItem;
import com.agamilabs.smartshop.Models.InvoiceModel;
import com.agamilabs.smartshop.R;


import java.util.List;

public class RvAdapter_selectedProductView extends RecyclerView.Adapter<RvAdapter_selectedProductView.MyViewHolder> {
    private List<InvoiceItem> searchListItemList;
    private Context context;

    public RvAdapter_selectedProductView(List<InvoiceItem> searchListItemList, Context context) {
        this.searchListItemList = searchListItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_selected_product_view_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        InvoiceItem current = searchListItemList.get(position);
        holder.tv_productName.setText(current.getItem_name());
        holder.tv_productQuantity.setText(String.valueOf((int) current.getQty()));
        holder.tv_productPrice.setText(String.valueOf((int) current.getItem_bill()));
    }

    @Override
    public int getItemCount() {
        return searchListItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_productName, tv_productQuantity, tv_productPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_rvProductView_productName);
            tv_productQuantity = itemView.findViewById(R.id.tv_rvProductView_productQuantity);
            tv_productPrice = itemView.findViewById(R.id.tv_rvProductView_productTotalPrice);
        }
    }
}
