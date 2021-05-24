package com.agamilabs.smartshop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackForBillDetails;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.InvoiceItemModel;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.PurchaseItemModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PurchaseInvoiceCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String url_get_PurchaseBillDetails_of_a_bill_org = "http://pharmacy.egkroy.com/app-module/php/get_purchaseBillDetails_of_a_bill_org.php";
    private static final String API_KEY = "apikey";
    private static final String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private static final String BILL_NO = "billno";
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ITEM = 0;

    private ArrayList<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItemModel> invoiceItemModelArrayList;
    private Context context;
    private RvAdapterInvoiceItemList rvAdapter_invoiceItemList;
    private ICallbackForBillDetails iCallbackForBillDetails;
    private ArrayList<LotsModel> billDetailsSummaryList;
    private ArrayList<PurchaseItemModel> purchaseItemModelArrayList;

    public PurchaseInvoiceCardViewAdapter(ArrayList<InvoiceModel> invoiceModelList, Context context, ICallbackForBillDetails iCallbackForBillDetails) {
        this.invoiceModelList = invoiceModelList;
        this.context = context;
        this.iCallbackForBillDetails = iCallbackForBillDetails;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_purchase_invoice_card_layout, parent, false);
            ItemViewHolder myViewHolder = new ItemViewHolder(view);
            return myViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populatedItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {
        //ProgressBar would be displayed
    }

    private void populatedItemRows(ItemViewHolder holder, int position) {
        InvoiceModel current = invoiceModelList.get(position);
        holder.tv_supplier.setText(current.getSupplierName());
        holder.tv_date.setText(current.getpDate());
        holder.tv_billNo.setText(String.valueOf(current.getBillNo()));
        holder.tv_dueDate.setText(current.getDueDate());
        holder.tv_discount.setText(String.format("\u09F3 " + "%.2f", current.getDiscount()));
        holder.tv_deduction.setText(String.format("\u09F3 " + "%.2f", current.getDeduction()));
        holder.tv_amount.setText(String.format("\u09F3 " + "%.2f", current.getAmount()));
        holder.tv_grandTotal.setText(String.format("\u09F3 " + "%.2f", current.getTotalAmountToPay()));

        productItemsCount(holder, position);

        if (current.getUnPaidAmount() < 0.1) {
            holder.tv_dueText.setVisibility(View.GONE);
            holder.tv_paidText.setVisibility(View.VISIBLE);
            holder.circularImageView_customerImg.setBorderColor(holder.mContext.getResources().getColor((R.color.green)));
            holder.circularImageView_activeIcon.setBorderColor(holder.mContext.getResources().getColor((R.color.green)));
            holder.tv_paidOrDue.setText(String.format("\u09F3 " + "%.2f", current.getPaid()));

        } else {
            holder.tv_dueText.setVisibility(View.VISIBLE);
            holder.tv_paidText.setVisibility(View.GONE);
            holder.tv_paidOrDue.setText(String.format("\u09F3 " + "%.2f", current.getUnPaidAmount()));
//            holder.circularImageView_customerImg.setBorderColor(R.color.yellow);
            holder.circularImageView_customerImg.setBorderColor(context.getResources().getColor(R.color.yellow));
            holder.circularImageView_activeIcon.setBorderColor(context.getResources().getColor(R.color.yellow));
        }

        holder.relativeLayoutExpandInvoiceItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceModel current = invoiceModelList.get(position);
                iCallbackForBillDetails.dataParsedBillDetails(current.getPurchaseItemModelArrayList());
                iCallbackForBillDetails.handleBottomSheet();
            }
        });

    }

    private void productItemsCount(ItemViewHolder holder, int position) {
        InvoiceModel current = invoiceModelList.get(position);
        HashMap<String, String> map_temp = new HashMap<>();
        map_temp.put(API_KEY, apikey);
        map_temp.put(BILL_NO, String.valueOf(current.getBillNo()));

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_PurchaseBillDetails_of_a_bill_org, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        purchaseItemModelArrayList = new ArrayList<>();
                        int itemCount = 0;

                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray itemsArray = data.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);

                            int orgno = itemsArrayJSONObject.getInt("orgno");
                            int itemno = itemsArrayJSONObject.getInt("itemno");
                            int qty = itemsArrayJSONObject.getInt("qty");
                            double purchaseRate = itemsArrayJSONObject.getDouble("unitprice");
                            String itemname = itemsArrayJSONObject.getString("itemname");
                            String sku = itemsArrayJSONObject.getString("item_id");

                            double item_bill = qty * purchaseRate;

                            boolean hasItem = false;
                            billDetailsSummaryList = new ArrayList<>();

                            for (int j = 0; j < purchaseItemModelArrayList.size(); j++) {
                                PurchaseItemModel current = purchaseItemModelArrayList.get(j);
                                if (current.getItemNO() == itemno && current.getOrgNo() == orgno) {
                                    hasItem = true;
                                    current.setTotalAmount(current.getTotalAmount() + item_bill);
                                    current.getSummeryArrayList().add(new LotsModel(itemno, orgno, sku, qty));
                                }
                            }
                            if (!hasItem) {
                                itemCount += 1;
                                billDetailsSummaryList.add(new LotsModel(itemno, orgno, sku, qty));
                                purchaseItemModelArrayList.add(new PurchaseItemModel(orgno, itemno, itemname, purchaseRate, item_bill, billDetailsSummaryList));
                            }
//                                    iCallbackForBillDetails.dataParsedBillDetails(orgno, itemno, itemname, purchaseRate, item_bill, sku, qty);
                        }

                        current.setPurchaseItemModelArrayList(purchaseItemModelArrayList);

                        if (itemsArray.length() > 1)
                            holder.tv_items.setText(" (" + String.valueOf(itemCount) + " Items)");
                        else
                            holder.tv_items.setText(" (" + String.valueOf(itemCount) + " Item)");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(holder.mContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, map_temp);

    }


    @Override
    public int getItemCount() {
        return invoiceModelList == null ? 0 : invoiceModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return invoiceModelList.get(position).getSupplierNo() == 0 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_items, tv_supplier, tv_date, tv_dueDate, tv_amount, tv_discount, tv_billNo, tv_paidAmount;
        TextView tv_invoiceNo, tv_paidOrDue, tv_deduction, tv_grandTotal, tv_dueText, tv_paidText;
        RecyclerView recyclerViewSelectedProductListInvoiceCard;
        LinearLayout linearLayoutClickToRv;
        RelativeLayout relativeLayoutExpandInvoiceItemList;
        ImageView imageView_up;
        CircularImageView circularImageView_customerImg, circularImageView_activeIcon;
        Context mContext;
        int isItemListVisible = 0;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_supplier = itemView.findViewById(R.id.tv_customerNameInvoiceCard);
            tv_date = itemView.findViewById(R.id.tv_dateInvoiceCard);
            tv_dueDate = itemView.findViewById(R.id.tv_dueDateInvoiceCard);
            tv_amount = itemView.findViewById(R.id.tv_totalInvoiceCard);
            tv_items = itemView.findViewById(R.id.tv_itemNumInvoiceCard);
            tv_discount = itemView.findViewById(R.id.tv_discountInvoiceCard);
            tv_billNo = itemView.findViewById(R.id.tv_invoiceNo);
            tv_paidAmount = itemView.findViewById(R.id.tv_paidInvoiceCard);
            tv_paidOrDue = itemView.findViewById(R.id.tv_paidOrDueInvoiceCard);
            tv_deduction = itemView.findViewById(R.id.tv_deductionInvoiceCard);
            tv_grandTotal = itemView.findViewById(R.id.tv_grandTotalInvoiceCard);
            tv_dueText = itemView.findViewById(R.id.tv_dueText);
            tv_paidText = itemView.findViewById(R.id.tv_paidText);
            circularImageView_customerImg = itemView.findViewById(R.id.imgV_customerImgInvoiceCard);
            circularImageView_activeIcon = itemView.findViewById(R.id.imgV_activeIcon);
            relativeLayoutExpandInvoiceItemList = itemView.findViewById(R.id.l3);
            imageView_up = itemView.findViewById(R.id.imgV_up);
            recyclerViewSelectedProductListInvoiceCard = itemView.findViewById(R.id.rv_selectedProductListInvoiceCard);
            linearLayoutClickToRv = itemView.findViewById(R.id.l5);
            mContext = itemView.getContext();

            /*recyclerViewSelectedProductListInvoiceCard.setHasFixedSize(true);
            recyclerViewSelectedProductListInvoiceCard.setLayoutManager(new LinearLayoutManager());
            rvAdapter_invoiceItemList = new RvAdapter_invoiceItemList( invoiceItemArrayList);
            recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);*/

//            itemView.setOnClickListener(this);
//            linearLayoutClickToRv.setOnClickListener(this);
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}

