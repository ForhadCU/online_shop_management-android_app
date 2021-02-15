package com.agamilabs.smartshop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.InvoiceItem;
import com.agamilabs.smartshop.model.InvoiceModel;

import java.util.ArrayList;

public class RvAdapter_selectedProductDetailsView extends RecyclerView.Adapter<RvAdapter_selectedProductDetailsView.MyViewHolder> {
    private ArrayList<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItem> invoiceItemArrayList;
    private Context context;
    private RvAdapter_invoiceItemList rvAdapter_invoiceItemList;
    private int isItemListVisible = 0;

    public RvAdapter_selectedProductDetailsView(ArrayList<InvoiceModel> invoiceModelList, Context context) {
        this.invoiceModelList = invoiceModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_invoice_card_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        InvoiceModel current = invoiceModelList.get(position);

        holder.tv_customer.setText(current.getCustomerName());
       /* holder.tv_date.setText(current.getCurrentDate());
        holder.tv_dueDate.setText(current.getDueDate());*/
        holder.tv_discount.setText(String.format("%.2f", current.getDiscount()));
        holder.tv_amount.setText("\u09F3" + String.format("%.2f", current.getAmount()));
        invoiceItemArrayList = current.getInvoiceItemArrayList();
        holder.tv_items.setText(String.valueOf((int) invoiceItemArrayList.size()));

        selectedItemListRvHandler(holder);


    }

    private void selectedItemListRvHandler(MyViewHolder holder) {
        holder.recyclerViewSelectedProductListInvoiceCard.setHasFixedSize(true);
        holder.recyclerViewSelectedProductListInvoiceCard.setLayoutManager(new LinearLayoutManager(context));
        rvAdapter_invoiceItemList = new RvAdapter_invoiceItemList(invoiceItemArrayList);
        holder.recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);
        rvAdapter_invoiceItemList.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return invoiceModelList == null ? 0 : invoiceModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_items, tv_customer, tv_date, tv_dueDate, tv_amount, tv_discount;
        RecyclerView recyclerViewSelectedProductListInvoiceCard;
        LinearLayout linearLayoutClickToRv;
        Context mContext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_customer = itemView.findViewById(R.id.tv_customerNameInvoiceCard);
            tv_date = itemView.findViewById(R.id.tv_dateInvoiceCard);
            tv_dueDate = itemView.findViewById(R.id.tv_dueDateInvoiceCard);
            tv_amount = itemView.findViewById(R.id.tv_totalInvoiceCard);
            tv_items = itemView.findViewById(R.id.tv_itemNumInvoiceCard);
            tv_discount = itemView.findViewById(R.id.tv_discountInvoiceCard);
            recyclerViewSelectedProductListInvoiceCard = itemView.findViewById(R.id.rv_selectedProductListInvoiceCard);
            linearLayoutClickToRv = itemView.findViewById(R.id.l5);
            mContext = itemView.getContext();

            /*recyclerViewSelectedProductListInvoiceCard.setHasFixedSize(true);
            recyclerViewSelectedProductListInvoiceCard.setLayoutManager(new LinearLayoutManager());
            rvAdapter_invoiceItemList = new RvAdapter_invoiceItemList( invoiceItemArrayList);
            recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);*/

            itemView.setOnClickListener(this);
            linearLayoutClickToRv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.l5:
                    isItemListVisible++;

                    if (isItemListVisible % 2 != 0)
                        recyclerViewSelectedProductListInvoiceCard.setVisibility(View.VISIBLE);
                    else
                        recyclerViewSelectedProductListInvoiceCard.setVisibility(View.GONE);


                    break;
            }
        }
    }
}

