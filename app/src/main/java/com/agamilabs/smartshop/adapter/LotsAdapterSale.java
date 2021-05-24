package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromLotsAdapterSale;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class LotsAdapterSale extends RecyclerView.Adapter<LotsAdapterSale.MyViewHolder> {
    private Context context;
    private ArrayList<LotsModel> lotsModelArrayList;
    int count = 0;
    ICallBackFromLotsAdapterSale iCallBackFromLotsAdapterSale;
    private ArrayList<String> selectedSkuArrayList;
    private int lastCheckedPosition = -1;


    public LotsAdapterSale(Context context, ArrayList<LotsModel> lotsModelArrayList, ArrayList<String> selectedSkuArrayList, ICallBackFromLotsAdapterSale iCallBackFromLotsAdapter) {
        this.context = context;
        this.lotsModelArrayList = lotsModelArrayList;
        this.iCallBackFromLotsAdapterSale = iCallBackFromLotsAdapter;
        this.selectedSkuArrayList = selectedSkuArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_lots_layout_sale, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LotsModel current = lotsModelArrayList.get(position);
//        holder.textViewSku.setText(current.getSku());
        holder.textViewQty.setText(String.valueOf(current.getQty()));
        holder.checkBox_Sku.setText(current.getSku());
//        holder.radioButton.setText("current.getSku()");
        if (selectedSkuArrayList != null)
        {
            for (int i=0; i<selectedSkuArrayList.size(); i++)
            {
                if (selectedSkuArrayList.get(i).equals(current.getSku())){
                    holder.checkBox_Sku.setChecked(true);
                    current.setSelected(true);
                    iCallBackFromLotsAdapterSale.mBoolean(true, lotsModelArrayList, current.getQty(), null);
                }
            }
        }
        else if (position == 0){
            holder.checkBox_Sku.setChecked(true);
            current.setSelected(true);
            iCallBackFromLotsAdapterSale.mBoolean(true, lotsModelArrayList, current.getQty(), null);
        }
        else
            holder.checkBox_Sku.setChecked(false);

        holder.checkBox_Sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked){
                    iCallBackFromLotsAdapterSale.mBoolean(true, lotsModelArrayList, current.getQty(), null);
                    current.setSelected(true);
                }

                else{
                    iCallBackFromLotsAdapterSale.mBoolean(false, lotsModelArrayList, current.getQty(), null);
                    current.setSelected(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (lotsModelArrayList == null) ? 0 : lotsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSku, textViewQty;
        CheckBox checkBox_Sku;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            textViewSku = itemView.findViewById(R.id.tv_rvLotsSku);
            textViewQty = itemView.findViewById(R.id.tv_rvLotsQty);
            checkBox_Sku = itemView.findViewById(R.id.checkbox_sku);


        }
    }
}
