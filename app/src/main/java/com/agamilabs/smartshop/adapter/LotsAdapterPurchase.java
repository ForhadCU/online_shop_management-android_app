package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromLotsAdapterPurchase;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class LotsAdapterPurchase extends RecyclerView.Adapter<LotsAdapterPurchase.MyViewHolder> {
    private final int VIEW_TYPE_EXISTED_SKU = 0;
    private final int VIEW_TYPE_NEW_SKU = 1;
    private Context context;
    private ArrayList<LotsModel> lotsModelArrayList;
    int count = 0;
    ICallBackFromLotsAdapterPurchase iCallBackFromLotsAdapterPurchase;
    private ArrayList<String> selectedSkuArrayList;
    private int lastCheckedPosition = -1;

    public LotsAdapterPurchase(Context context, ArrayList<LotsModel> lotsModelArrayList, ArrayList<String> selectedSkuArrayList, ICallBackFromLotsAdapterPurchase iCallBackFromLotsAdapterPurchase) {
        this.context = context;
        this.lotsModelArrayList = lotsModelArrayList;
        this.iCallBackFromLotsAdapterPurchase = iCallBackFromLotsAdapterPurchase;
        this.selectedSkuArrayList = selectedSkuArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
 /*       if (viewType == VIEW_TYPE_EXISTED_SKU)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_lots_layout_purchase, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_NEW_SKU)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_lots_layout_purchase, parent, false);
            return new MyViewHolder(view);
        }*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_lots_layout_purchase_existed_sku, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LotsModel current = lotsModelArrayList.get(position);
        mSpannedTextViewQty(holder, current);
        holder.radioButton.setChecked(position == lastCheckedPosition);

        holder.radioButton.setText(current.getSku());

        if (current.getLotno() == -1) {
            holder.llExistedSku.setVisibility(View.GONE);
            holder.llAddNewSku.setVisibility(View.VISIBLE);
            holder.llInputNewSku.setVisibility(View.GONE);
            holder.radioButton.setOnClickListener(null);

            if (current.isClickControl()) {
                holder.llAddNewSku.setVisibility(View.VISIBLE);
                holder.llInputNewSku.setVisibility(View.GONE);
            } else {
                holder.llAddNewSku.setVisibility(View.GONE);
                holder.llInputNewSku.setVisibility(View.VISIBLE);
            }

            holder.llAddNewSku.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.llAddNewSku.setVisibility(View.GONE);
                    holder.llInputNewSku.setVisibility(View.VISIBLE);
                    current.setClickControl(false);
//                    notifyItemChanged(lastCheckedPosition);
                    lastCheckedPosition = position;
                    holder.editTextInputNewSku.setText("");

                    notifyDataSetChanged();

                    iCallBackFromLotsAdapterPurchase.clickHandleAddNewBtn(true, lotsModelArrayList, lastCheckedPosition);

                }
            });
        } else {
            holder.llExistedSku.setVisibility(View.VISIBLE);
            holder.llAddNewSku.setVisibility(View.GONE);
            holder.llInputNewSku.setVisibility(View.GONE);
            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                int copyOfLastCheckedPosition = lastCheckedPosition;
                    lastCheckedPosition = position;
                    current.setClickControl(true);
                /*notifyItemChanged(copyOfLastCheckedPosition);
                notifyItemChanged(lastCheckedPosition);*/
                    notifyDataSetChanged();

                    iCallBackFromLotsAdapterPurchase.clickHandleRadioBtn(true, lotsModelArrayList, current.getQty(), current.getSku(), lastCheckedPosition);
                }
            });
            holder.llAddNewSku.setOnClickListener(null);

        }

        if (position != lastCheckedPosition && current.getLotno() == -1) {
            holder.llExistedSku.setVisibility(View.GONE);
            holder.llAddNewSku.setVisibility(View.VISIBLE);
            holder.llInputNewSku.setVisibility(View.GONE);
        }

        holder.editTextInputNewSku.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lotsModelArrayList.get(lastCheckedPosition).setSku(s.toString());

                if (s.toString().length() == 1)
                {
                    iCallBackFromLotsAdapterPurchase.clickHandleAddNewBtn(false, lotsModelArrayList, lastCheckedPosition);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void mSpannedTextViewQty(MyViewHolder holder, LotsModel current) {
        holder.textViewQty.setText("Qty : " + String.valueOf(current.getQty()) + "  ( +" + String.valueOf(current.getQtyIncrease()) + " )");
        String currentQtyText = holder.textViewQty.getText().toString();
        SpannableStringBuilder qtySpan = new SpannableStringBuilder(currentQtyText);
        if (current.getQtyIncrease() > 0) {

            qtySpan.setSpan(new ForegroundColorSpan(0xFF4CAF50), currentQtyText.indexOf("(") + 1, currentQtyText.indexOf(")"),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            qtySpan.setSpan(new RelativeSizeSpan(0.9f), currentQtyText.indexOf("("), currentQtyText.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            qtySpan.replace(currentQtyText.indexOf("(") + 1, currentQtyText.indexOf(")"), "+" + String.valueOf(current.getQtyIncrease()));
        } else {
            qtySpan.replace(currentQtyText.indexOf(":") + 2,
                    currentQtyText.length(),
                    String.valueOf(current.getQty()));
        }
        holder.textViewQty.setText(qtySpan);
    }

    @Override
    public int getItemCount() {
        return (lotsModelArrayList == null) ? 0 : lotsModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements TextView.OnEditorActionListener {
        TextView textViewSku, textViewQty, textViewBtnAddNewSku;
        RadioButton radioButton;
        LinearLayout llAddNewSku, llInputNewSku, llExistedSku;
        EditText editTextInputNewSku;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            textViewSku = itemView.findViewById(R.id.tv_rvLotsSku);
            textViewQty = itemView.findViewById(R.id.tv_rvLotsQty);
            radioButton = itemView.findViewById(R.id.radioBtn_sku);
            textViewBtnAddNewSku = itemView.findViewById(R.id.tv_createNewSku);
            llAddNewSku = itemView.findViewById(R.id.llAddNewSku);
            llInputNewSku = itemView.findViewById(R.id.llInputNewSku);
            editTextInputNewSku = itemView.findViewById(R.id.edtTxt_newSku);
            llExistedSku = itemView.findViewById(R.id.llExistedSku);

            editTextInputNewSku.setOnEditorActionListener(this);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                editTextInputNewSku.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            } else
                return false;
        }


    }
}
