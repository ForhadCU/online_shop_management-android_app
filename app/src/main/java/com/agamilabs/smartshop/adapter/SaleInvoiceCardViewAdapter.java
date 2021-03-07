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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.InvoiceItem;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SaleInvoiceCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String url_get_saleInvoiceDetails_of_an_invoice_org = "http://pharmacy.egkroy.com/app-module/php/get_saleinvoiceDetails_of_an_invoice_org.php";
    private static final String API_KEY = "apikey";
    private static final String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private static final String INVOICE_NO = "invoiceno";
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ITEM = 0;

    private ArrayList<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItem> invoiceItemArrayList;
    private Context context;
    private RvAdapterInvoiceItemList rvAdapter_invoiceItemList;
//    private int isItemListVisible = 0;

    public SaleInvoiceCardViewAdapter(ArrayList<InvoiceModel> invoiceModelList, Context context) {
        this.invoiceModelList = invoiceModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_sale_invoice_card_layout, parent, false);
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
        holder.tv_customer.setText(current.getCustomerName());
        holder.tv_date.setText(current.getsDate());
        holder.tv_invoiceNo.setText(String.valueOf(current.getInvoiceNo()));
        holder.tv_discount.setText(String.format("%.2f", current.getDiscount()));
        holder.tv_amount.setText(String.format("%.2f", current.getAmount()));
        holder.tv_dueDate.setText(current.getDueDate());
        productItemsCount(holder, position);

        holder.relativeLayoutExpandInvoiceItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isItemListVisible++;

                if (holder.isItemListVisible % 2 != 0) {

                    InvoiceModel current = invoiceModelList.get(position);
                    HashMap<String, String> map_temp = new HashMap<>();
                    map_temp.put(API_KEY, apikey);
                    map_temp.put(INVOICE_NO, String.valueOf(current.getInvoiceNo()));

                    AppController.getAppController().getAppNetworkController().makeRequest(url_get_saleInvoiceDetails_of_an_invoice_org, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);

                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                    holder.linearLayoutClickToRv.setVisibility(View.VISIBLE);
                                    holder.recyclerViewSelectedProductListInvoiceCard.setVisibility(View.VISIBLE);
                                    holder.imageView_down.setVisibility(View.GONE);
                                    holder.imageView_up.setVisibility(View.VISIBLE);
                                    invoiceItemArrayList = new ArrayList<>();

                                    JSONObject data = jsonObject.getJSONObject("data");
                                    JSONArray itemsArray = data.getJSONArray("items");
                                    /*assert invoiceItemArrayList != null;
                                    invoiceItemArrayList.clear();*/

                                    for (int i = 0; i < itemsArray.length(); i++) {
                                        JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);

                                        int itemno = itemsArrayJSONObject.getInt("itemno");
                                        int qty = itemsArrayJSONObject.getInt("qty");
                                        double unitprice = itemsArrayJSONObject.getDouble("unitprice");
                                        String itemname = itemsArrayJSONObject.getString("itemname");

                                        double item_bill = qty * unitprice;

                                        invoiceItemArrayList.add(new InvoiceItem(itemno, qty, unitprice, itemname, item_bill));
//                                            Toast.makeText(mContext, String.valueOf(invoiceItemArrayList.size()), Toast.LENGTH_SHORT).show();

                                    }
                                  /*  rvAdapter_invoiceItemList = new RvAdapterInvoiceItemList(invoiceItemArrayList);
                                    holder.recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);*/

                                    selectedItemListRvHandler(holder);
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
                } else {
                    holder.recyclerViewSelectedProductListInvoiceCard.setVisibility(View.GONE);
                    holder.linearLayoutClickToRv.setVisibility(View.GONE);
                    holder.imageView_down.setVisibility(View.VISIBLE);
                    holder.imageView_up.setVisibility(View.GONE);
                }
            }
        });

    }

    private void productItemsCount(ItemViewHolder holder, int position) {
        InvoiceModel current = invoiceModelList.get(position);
        HashMap<String, String> map_temp = new HashMap<>();
        map_temp.put(API_KEY, apikey);
        map_temp.put(INVOICE_NO, String.valueOf(current.getInvoiceNo()));

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_saleInvoiceDetails_of_an_invoice_org, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        invoiceItemArrayList = new ArrayList<>();

                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray itemsArray = data.getJSONArray("items");
                        holder.tv_items.setText(String.valueOf(itemsArray.length()));
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

    private void selectedItemListRvHandler(ItemViewHolder holder) {
        holder.recyclerViewSelectedProductListInvoiceCard.setHasFixedSize(true);
        holder.recyclerViewSelectedProductListInvoiceCard.setLayoutManager(new LinearLayoutManager(context));
        rvAdapter_invoiceItemList = new RvAdapterInvoiceItemList(invoiceItemArrayList);
        holder.recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);
//        rvAdapter_invoiceItemList.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return invoiceModelList == null ? 0 : invoiceModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return invoiceModelList.get(position).getCustomerName() == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_items, tv_customer, tv_date, tv_dueDate, tv_amount, tv_discount, tv_invoiceNo;
        RecyclerView recyclerViewSelectedProductListInvoiceCard;
        LinearLayout linearLayoutClickToRv;
        RelativeLayout relativeLayoutExpandInvoiceItemList;
        ImageView imageView_up, imageView_down;
        Context mContext;
        int isItemListVisible = 0;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_customer = itemView.findViewById(R.id.tv_customerNameInvoiceCard);
            tv_date = itemView.findViewById(R.id.tv_dateInvoiceCard);
            tv_dueDate = itemView.findViewById(R.id.tv_dueDateInvoiceCard);
            tv_amount = itemView.findViewById(R.id.tv_totalInvoiceCard);
            tv_items = itemView.findViewById(R.id.tv_itemNumInvoiceCard);
            tv_discount = itemView.findViewById(R.id.tv_discountInvoiceCard);
            tv_invoiceNo = itemView.findViewById(R.id.tv_invoiceNo);
            relativeLayoutExpandInvoiceItemList = itemView.findViewById(R.id.l6);
            imageView_up = itemView.findViewById(R.id.imgV_up);
            imageView_down = itemView.findViewById(R.id.imgV_down);
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

/*
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.l5:
                    isItemListVisible++;

                    if (isItemListVisible % 2 != 0) {
                        recyclerViewSelectedProductListInvoiceCard.setVisibility(View.VISIBLE);
                        InvoiceModel current = invoiceModelList.get(getAdapterPosition());
                        HashMap<String, String> map_temp = new HashMap<>();
                        map_temp.put(API_KEY, apikey);
                        map_temp.put(INVOICE_NO, String.valueOf(current.getInvoiceNo()));

                        AppController.getAppController().getAppNetworkController().makeRequest(url_get_saleInvoiceDetails_of_an_invoice_org, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);

                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        JSONArray itemsArray = data.getJSONArray("items");

                                        for (int i = 0; i < itemsArray.length(); i++) {
                                            JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);

                                            int itemno = itemsArrayJSONObject.getInt("itemno");
                                            int qty = itemsArrayJSONObject.getInt("qty");
                                            double unitprice = itemsArrayJSONObject.getDouble("unitprice");
                                            String itemname = itemsArrayJSONObject.getString("itemname");

                                            double item_bill = qty * unitprice;

                                            invoiceItemArrayList.add(new InvoiceItem(itemno, qty, unitprice, itemname, item_bill));
//                                            Toast.makeText(mContext, String.valueOf(invoiceItemArrayList.size()), Toast.LENGTH_SHORT).show();

                                        }
                                              rvAdapter_invoiceItemList = new RvAdapter_invoiceItemList(invoiceItemArrayList);
                                            recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }, map_temp);
                    } else
                        recyclerViewSelectedProductListInvoiceCard.setVisibility(View.GONE);


                    break;
            }
        }
*/
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

}

