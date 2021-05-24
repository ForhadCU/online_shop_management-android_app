package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackForBillDetails;
import com.agamilabs.smartshop.PurchaseInvoiceViewerActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.PurchaseItemModel;

import java.util.ArrayList;

public class RvAdapterBillDetails extends RecyclerView.Adapter<RvAdapterBillDetails.MyViewHolder> {
    private Context context;
    private ICallbackForBillDetails iCallbackForBillDetails;
    private ArrayList<PurchaseItemModel> purchaseItemModelArrayList;
    private RvAdapterBillDetailsSummary rvAdapterBillDetailsSummary;

    public RvAdapterBillDetails(ArrayList<PurchaseItemModel> purchaseItemModelArrayList, Context context, ICallbackForBillDetails iCallbackForBillDetails) {
        this.context = context;
        this.iCallbackForBillDetails = iCallbackForBillDetails;
        this.purchaseItemModelArrayList = purchaseItemModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bill_details_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PurchaseItemModel current = purchaseItemModelArrayList.get(position);

        holder.tv_productName.setText(current.getItemName());
        holder.tv_productPrice.setText("\u09F3" + String.format("%.2f", current.getPurchaseRate()));
        holder.tv_productTotalPrice.setText("\u09F3" + String.format("%.2f", current.getTotalAmount()));

        rvAdapterBillDetailsSummary = new RvAdapterBillDetailsSummary(current.getSummeryArrayList(), context, current.getPurchaseRate());
        holder.recyclerView_SelectedSku.setAdapter(rvAdapterBillDetailsSummary);
    }

    @Override
    public int getItemCount() {
        return purchaseItemModelArrayList == null ? 0 : purchaseItemModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_productName, tv_productPrice, tv_productTotalPrice;
        RecyclerView recyclerView_SelectedSku;
        Context mContext;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_productName = itemView.findViewById(R.id.tv_rvProductView_productName);
            tv_productTotalPrice = itemView.findViewById(R.id.tv_rvProductView_productTotalPrice);
            tv_productPrice = itemView.findViewById(R.id.tv_rvProductView_productPrice);
            recyclerView_SelectedSku = itemView.findViewById(R.id.rv_selectedSkuList);
            mContext = itemView.getContext();

            recyclerView_SelectedSku.setHasFixedSize(true);
            recyclerView_SelectedSku.setLayoutManager(new LinearLayoutManager(mContext));
        }
    }
}
