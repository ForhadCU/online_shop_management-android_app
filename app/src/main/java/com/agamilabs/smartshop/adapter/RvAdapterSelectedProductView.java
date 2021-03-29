package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromFullScannerActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.InvoiceItemModel;

import java.util.List;

public class RvAdapterSelectedProductView extends RecyclerView.Adapter<RvAdapterSelectedProductView.MyViewHolder> {
    private List<InvoiceItemModel> invoiceItemModelList;
    private Context context;
    private ICallBackFromFullScannerActivity icallBackTest;

    public RvAdapterSelectedProductView(List<InvoiceItemModel> invoiceItemModelList, Context context, ICallBackFromFullScannerActivity icallBackTest) {
        this.invoiceItemModelList = invoiceItemModelList;
        this.context = context;
        this.icallBackTest = icallBackTest;
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
        InvoiceItemModel current = invoiceItemModelList.get(position);

        holder.tv_productName.setText(current.getItemname());
        holder.tv_productQuantity.setText(String.valueOf((int) current.getQty()));
        holder.tv_productPrice.setText("\u09F3" + String.format("%.2f", current.getRate()));
        holder.tv_productTotalPrice.setText("\u09F3" + String.format("%.2f", current.getItem_bill()));
    }

    @Override
    public int getItemCount() {
        return invoiceItemModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_productName, tv_productQuantity, tv_productPrice, tv_productTotalPrice;
        ImageButton imageButtonQuantityIncr, imageButtonQuantityDecr, imageButtonDeleteItem;
        double itemBillCommon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_rvProductView_productName);
            tv_productQuantity = itemView.findViewById(R.id.tv_rvProductView_productQuantity);
            tv_productTotalPrice = itemView.findViewById(R.id.tv_rvProductView_productTotalPrice);
            tv_productPrice = itemView.findViewById(R.id.tv_rvProductView_productPrice);
            imageButtonQuantityIncr = itemView.findViewById(R.id.btn_rvProductView_quantityIncrease);
            imageButtonQuantityDecr = itemView.findViewById(R.id.btn_rvProductView_quantityDecrease);
            imageButtonDeleteItem = itemView.findViewById(R.id.imgBtn_rvProductView_productDelete);

            imageButtonQuantityDecr.setOnClickListener(this);
            imageButtonQuantityIncr.setOnClickListener(this);
            imageButtonDeleteItem.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            InvoiceItemModel current = invoiceItemModelList.get(getAdapterPosition());

            double unitPrice = current.getRate();
            double itemBill = current.getItem_bill();
            double itemQty = current.getQty();
            double itemDiscount = (unitPrice * itemQty) - itemBill;
            switch (v.getId()) {
                case R.id.btn_rvProductView_quantityDecrease:
                    double qty = Double.parseDouble(tv_productQuantity.getText().toString());
                    qty--;
                    if (qty > 0) {
                        itemBillCommon = (qty * unitPrice) - itemDiscount;
                        current.setItem_bill(itemBillCommon);
                        current.setQty((int) qty);
                        tv_productQuantity.setText(String.valueOf((int) qty));
                        tv_productTotalPrice.setText("\u09F3" + String.format("%.2f", itemBillCommon));
                    }
                    icallBackTest.mCallbackQty();
                    break;

                case R.id.btn_rvProductView_quantityIncrease:
                    double qty2 = Double.parseDouble(tv_productQuantity.getText().toString());
                    qty2++;
                    if (qty2 > 0) {
                        itemBillCommon = (qty2 * unitPrice) - itemDiscount;
                        current.setItem_bill(itemBillCommon);
                        current.setQty((int) qty2);
                        tv_productQuantity.setText(String.valueOf((int) qty2));
                        tv_productTotalPrice.setText("\u09F3" + String.format("%.2f", itemBillCommon));
                        icallBackTest.mCallbackQty();
                    }
                    break;

                case R.id.imgBtn_rvProductView_productDelete:
                    invoiceItemModelList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), invoiceItemModelList.size());
                    icallBackTest.mCallbackQty();
                    break;
            }
        }
    }


}
