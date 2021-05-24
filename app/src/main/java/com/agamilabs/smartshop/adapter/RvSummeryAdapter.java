package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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

public class RvSummeryAdapter extends RecyclerView.Adapter<RvSummeryAdapter.MyViewHolder> {
    private ArrayList<LotsModel> summeryArrayList;
    private Context context;
    private double purchaseRate;

    public RvSummeryAdapter(Context context, ArrayList<LotsModel> summeryArrayList, String purchaseRate) {
        this.summeryArrayList = summeryArrayList;
        this.context = context;
        this.purchaseRate = Double.parseDouble(purchaseRate);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_summery_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LotsModel current = summeryArrayList.get(position);

        holder.tv_rvSelectedSkuSerial.setText(String.valueOf(position+1));
        holder.tv_rvSelectedSku.setText(current.getSku());
        holder.tv_rvSelectedSkuQty.setText(String.valueOf(current.getQty())+"  ( +"+String.valueOf(current.getQtyIncrease())+" )");
        mSetSpanned(holder);
        holder.tv_rvSelectedSkuTotalPrice.setText("\u09F3 " + String.format("%.2f", current.getQtyIncrease() * purchaseRate));

    }

    private void mSetSpanned(MyViewHolder holder) {
        String currentSkuQty = holder.tv_rvSelectedSkuQty.getText().toString();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(currentSkuQty);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF4CAF50), currentSkuQty.indexOf("(") + 1, currentSkuQty.indexOf(")"),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.tv_rvSelectedSkuQty.setText(spannableStringBuilder);
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
