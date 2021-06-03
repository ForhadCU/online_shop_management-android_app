package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackClickHandler;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class StockLotsAdapter extends RecyclerView.Adapter<StockLotsAdapter.MyViewHolder> {
    private ICallbackClickHandler iCallbackClickHandler;
    private ArrayList<LotsModel> lotsModelArrayList;
    private Context context;

    public StockLotsAdapter(ICallbackClickHandler iCallbackClickHandler, ArrayList<LotsModel> lotsModelArrayList, Context context) {
        this.iCallbackClickHandler = iCallbackClickHandler;
        this.lotsModelArrayList = lotsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_stock_lots_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        double pRate;
        LotsModel current = lotsModelArrayList.get(position);
        holder.tvLotsNo.setText(current.getStockLotNo());
        holder.tvSku.setText(current.getSku());
        holder.tvQty.setText(current.getStockQty());
        if (current.getLastlot_purchaserate().equalsIgnoreCase("null"))
            holder.tvPRate.setText("0.00");
        else
            holder.tvPRate.setText(current.getLastlot_purchaserate());
        if (current.getLast_purchaserate().equalsIgnoreCase("null"))
            pRate = 0.00;
        else
            pRate = Double.parseDouble(current.getLast_purchaserate());
        double sValue = pRate * Integer.parseInt(current.getStockQty());
        holder.tvStockValue.setText(String.valueOf(sValue));
    }

    @Override
    public int getItemCount() {
        return lotsModelArrayList == null ? 0 : lotsModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLotsNo, tvSku, tvQty, tvPRate, tvStockValue;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLotsNo = itemView.findViewById(R.id.tvLotsNo);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPRate = itemView.findViewById(R.id.tvPRate);
            tvStockValue = itemView.findViewById(R.id.tvSValue);
        }
    }
}
