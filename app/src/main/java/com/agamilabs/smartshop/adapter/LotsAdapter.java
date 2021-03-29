package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromLotsAdapter;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.ScanerDilogFragmentActivity;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class LotsAdapter extends RecyclerView.Adapter<LotsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LotsModel> lotsModelArrayList;
    int count = 0;
    ScanerDilogFragmentActivity scanerDilogFragmentActivity = new ScanerDilogFragmentActivity();
    ICallBackFromLotsAdapter iCallBackFromLotsAdapter;


    public LotsAdapter(Context context, ArrayList<LotsModel> lotsModelArrayList, ICallBackFromLotsAdapter iCallBackFromLotsAdapter) {
        this.context = context;
        this.lotsModelArrayList = lotsModelArrayList;
        this.iCallBackFromLotsAdapter = iCallBackFromLotsAdapter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_lots_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LotsModel current = lotsModelArrayList.get(position);
//        holder.textViewSku.setText(current.getSku());
        holder.textViewQty.setText(String.valueOf(current.getQty()));
        holder.checkBox_Sku.setText(current.getSku());
        if (position == 0){
            holder.checkBox_Sku.setChecked(true);
            current.setSelected(true);
            iCallBackFromLotsAdapter.mBoolean(true, lotsModelArrayList, current.getQty());
        }
        else
            holder.checkBox_Sku.setChecked(false);

        holder.checkBox_Sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked){
                    iCallBackFromLotsAdapter.mBoolean(true, lotsModelArrayList, current.getQty());
                    current.setSelected(true);
                }

                else{
                    iCallBackFromLotsAdapter.mBoolean(false, lotsModelArrayList, current.getQty());
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
