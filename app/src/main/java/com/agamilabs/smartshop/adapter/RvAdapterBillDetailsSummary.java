package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class RvAdapterBillDetailsSummary extends RecyclerView.Adapter<RvAdapterBillDetailsSummary.MyViewHolder> {
    private ArrayList<LotsModel> summeryArrayList;
    private Context context;
    private double purchaseRate;

    public RvAdapterBillDetailsSummary(ArrayList<LotsModel> summeryArrayList, Context context, double purchaseRate) {
        this.summeryArrayList = summeryArrayList;
        this.context = context;
        this.purchaseRate = purchaseRate;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bill_details_summary, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LotsModel current = summeryArrayList.get(position);

        holder.tv_rvSelectedSkuSerial.setText(String.valueOf(position+1));
        holder.tv_rvSelectedSku.setText(current.getSku());
        holder.tv_rvSelectedSkuQty.setText(String.valueOf(current.getQtyIncrease()));
        holder.tv_rvSelectedSkuTotalPrice.setText("\u09F3 " + String.format("%.2f", current.getQtyIncrease() * purchaseRate));
    }

    @Override
    public int getItemCount() {
        return summeryArrayList == null ? 0 : summeryArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rvSelectedSkuSerial, tv_rvSelectedSku, tv_rvSelectedSkuQty, tv_rvSelectedSkuTotalPrice;
        LinearLayout llSingleRow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rvSelectedSkuSerial = itemView.findViewById(R.id.tv_rvSelectedSkuSerial);
            tv_rvSelectedSku = itemView.findViewById(R.id.tv_rvSelectedSku);
            tv_rvSelectedSkuQty = itemView.findViewById(R.id.tv_rvSelectedSkuQty);
            tv_rvSelectedSkuTotalPrice = itemView.findViewById(R.id.tv_rvSelectedSkuTotalPrice);
            llSingleRow = itemView.findViewById(R.id.l5);
        }
    }
}
