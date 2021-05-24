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

import com.agamilabs.smartshop.Interfaces.ICallBackFromFullScannerActivityPurchase;
import com.agamilabs.smartshop.Interfaces.ICallbackForBillDetails;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.PurchaseItemModel;

import java.util.ArrayList;

public class RvAdapterAddedItemViewPurchase extends RecyclerView.Adapter<RvAdapterAddedItemViewPurchase.MyViewHolder> {
    private ArrayList<PurchaseItemModel> purchaseItemModelsArraylist;
    private Context context;
    private ICallBackFromFullScannerActivityPurchase iCallBackFromFullScannerActivityPurchase;
    private ICallbackForBillDetails iCallbackForBillDetails;
    private RvSummeryAdapter rvSummeryAdapter;

    public RvAdapterAddedItemViewPurchase(ArrayList<PurchaseItemModel> purchaseItemModelsArraylist, Context context,
                                          ICallBackFromFullScannerActivityPurchase iCallBackFromFullScannerActivityPurchase) {
        this.purchaseItemModelsArraylist = purchaseItemModelsArraylist;
        this.context = context;
        this.iCallBackFromFullScannerActivityPurchase = iCallBackFromFullScannerActivityPurchase;
    }

    public RvAdapterAddedItemViewPurchase(ArrayList<PurchaseItemModel> purchaseItemModelsArraylist, Context context,
                                          ICallbackForBillDetails iCallbackForBillDetails) {
        this.purchaseItemModelsArraylist = purchaseItemModelsArraylist;
        this.context = context;
        this.iCallbackForBillDetails = iCallbackForBillDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_selected_product_view_layout_purchase, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PurchaseItemModel current = purchaseItemModelsArraylist.get(position);

        holder.tv_productName.setText(current.getItemName());
        holder.tv_productPrice.setText("\u09F3" + String.format("%.2f", current.getPurchaseRate()));
        holder.tv_productTotalPrice.setText("\u09F3" + String.format("%.2f", current.getTotalAmount()));

        rvSummeryAdapter = new RvSummeryAdapter(context, current.getSummeryArrayList(), String.valueOf(current.getPurchaseRate()));
        holder.recyclerView_SelectedSku.setAdapter(rvSummeryAdapter);
    }


    @Override
    public int getItemCount() {
        return purchaseItemModelsArraylist == null ? 0 : purchaseItemModelsArraylist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_productName, tv_productPrice, tv_productTotalPrice;
        ImageButton imageButtonDeleteItem, imageButtonEditItem;
        RecyclerView recyclerView_SelectedSku;
        double itemBillCommon;
        Context mContext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_productName = itemView.findViewById(R.id.tv_rvProductView_productName);
            tv_productTotalPrice = itemView.findViewById(R.id.tv_rvProductView_productTotalPrice);
            tv_productPrice = itemView.findViewById(R.id.tv_rvProductView_productPrice);
            imageButtonDeleteItem = itemView.findViewById(R.id.imgBtn_rvProductView_productDelete);
            imageButtonEditItem = itemView.findViewById(R.id.imgBtn_rvProductView_productEditor);
            recyclerView_SelectedSku = itemView.findViewById(R.id.rv_selectedSkuList);
            mContext = itemView.getContext();

            recyclerView_SelectedSku.setHasFixedSize(true);
            recyclerView_SelectedSku.setLayoutManager(new LinearLayoutManager(mContext));
            imageButtonDeleteItem.setOnClickListener(this);
            imageButtonEditItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PurchaseItemModel current = purchaseItemModelsArraylist.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.imgBtn_rvProductView_productDelete:
                    purchaseItemModelsArraylist.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), purchaseItemModelsArraylist.size());
                    iCallBackFromFullScannerActivityPurchase.mCallbackQty();
                    break;

                case R.id.imgBtn_rvProductView_productEditor:
                    iCallBackFromFullScannerActivityPurchase.mCallBackEditItem(current.getOrgNo(), current.getItemNO(), current.getItemName(),
                            current.getPurchaseRate(), current.getSaleRate(), current.getUnitid(), current.getExpirydate(),
                            current.getDiscount_percentage(), current.getLotsModelArrayList(), current.getTaxrate());
            }
        }
    }


}
