package com.agamilabs.smartshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.model.InvoiceItem;
import com.agamilabs.smartshop.R;

import java.util.ArrayList;

public class RvAdapterInvoiceItemList extends RecyclerView.Adapter<RvAdapterInvoiceItemList.MyViewHolder> {
    private ArrayList<InvoiceItem> invoiceItemList;

    public RvAdapterInvoiceItemList(ArrayList<InvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_selected_items_invoice_card_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        InvoiceItem current = invoiceItemList.get(position);
        holder.textViewItemSerial.setText(String.valueOf(position+1));
        holder.textView_itemName.setText(current.getItemname());
        holder.textView_itemPriceForCal.setText(String.format("%.2f", current.getRate()));
        holder.textView_rate.setText((String.format("%.2f", current.getRate())));
        holder.textView_qty.setText(String.valueOf((int) current.getQty()));
        holder.textView_total.setText((String.format("%.2f", current.getItem_bill())));

    }

    @Override
    public int getItemCount() {
        return invoiceItemList == null ? 0 : invoiceItemList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        TextView textViewItemSerial, textView_itemName, textView_rate, textView_itemPriceForCal, textView_qty, textView_total;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewItemSerial = itemView.findViewById(R.id.tv_rvSelectedItemSerial);
            textView_itemName = itemView.findViewById(R.id.tv_rvSelectedItemName);
            textView_rate = itemView.findViewById(R.id.tv_rvSelectedItemRate);
            textView_qty = itemView.findViewById(R.id.tv_rvSelectedItemQty);
            textView_total = itemView.findViewById(R.id.tv_rvSelectedItemTotalPrice);
            textView_itemPriceForCal = itemView.findViewById(R.id.tv_rvSelectedItemPriceForCal);
        }
    }
}
