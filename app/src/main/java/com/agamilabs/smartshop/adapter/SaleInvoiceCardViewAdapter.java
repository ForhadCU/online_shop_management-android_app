package com.agamilabs.smartshop.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromFullScannerActivitySale;
import com.agamilabs.smartshop.PdfConverterActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.InvoiceItemModel;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SaleInvoiceCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String url_get_saleInvoiceDetails_of_an_invoice_org = "http://pharmacy.egkroy.com/app-module/php/get_saleinvoiceDetails_of_an_invoice_org.php";
    private static final String url_get_mini_invoice = "http://pharmacy.egkroy.com/app-module/php/get_mini_invoice_detail.php";
    private static final String url_printer = "192.168.1.127/pos/printutility/posreceipt.php";
    private static final String API_KEY = "apikey";
    private static final String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private static final String INVOICE_NO = "invoiceno";
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final String PRINTER_NAME_KEY = "print_name";
    private static final String PRINTER_URL_KEY = "print_url";
    private String printer_size;
    private JSONObject jsonObjectData;


    private ArrayList<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItemModel> invoiceItemModelArrayList;
    private Context context;
    private RvAdapterInvoiceItemList rvAdapter_invoiceItemList;
    private Dialog dialogPrinterConfirmation;
    private ICallBackFromFullScannerActivitySale iCallBackFromFullScannerActivitySale;
//    private int isItemListVisible = 0;

    public SaleInvoiceCardViewAdapter(ICallBackFromFullScannerActivitySale iCallBackFromFullScannerActivitySale) {
        this.iCallBackFromFullScannerActivitySale = iCallBackFromFullScannerActivitySale;
    }

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

    @SuppressLint("DefaultLocale")
    private void populatedItemRows(ItemViewHolder holder, int position) {
        InvoiceModel current = invoiceModelList.get(position);
        holder.tv_customer.setText(current.getCustomerName());
        holder.tv_date.setText(current.getsDate());
        holder.tv_dueDate.setText(current.getDueDate());
        holder.tv_invoiceNo.setText(String.valueOf(current.getInvoiceNo()));
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
 /*       *//**
         * 1120 = cash paid
         * 1110 = bank paid
         * 1160 = bKash paid
         *//*
        if (current.getUnPaidAmount() < 0.1) {
            holder.tv_dueText.setVisibility(View.GONE);
            holder.tv_paidText.setVisibility(View.VISIBLE);
            holder.circularImageView_customerImg.setBorderColor(holder.mContext.getResources().getColor((R.color.green)));
            holder.circularImageView_activeIcon.setBorderColor(holder.mContext.getResources().getColor((R.color.green)));
            holder.tv_paidOrDue.setText(String.format("\u09F3 " + "%.2f", current.getPaid()));
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < current.getArrayListPayments().size(); i++) {
                if (i == 0) {
                    switch (current.getArrayListPayments().get(i)) {
                        case "1120":
                            stringBuffer.append("Cash");
                            break;
                        case "1110":
                            stringBuffer.append("Bank");
                            break;
                        case "1160":
                            stringBuffer.append("bKash");
                            break;
                    }
                } else {
                    holder.tv_paidText.setTextSize(12);
                    switch (current.getArrayListPayments().get(i)) {
                        case "1120":
                            stringBuffer.append("/").append("Cash");
                            break;
                        case "1110":
                            stringBuffer.append("/").append("Bank");
                            break;
                        case "1160":
                            stringBuffer.append("/").append("bKash");
                            break;
                    }
                }
            }
            holder.tv_paidText.setText(stringBuffer.toString());

        } else {
            holder.tv_dueText.setVisibility(View.VISIBLE);
            holder.tv_paidText.setVisibility(View.GONE);
            holder.tv_paidOrDue.setText(String.format("\u09F3 " + "%.2f", current.getUnPaidAmount()));
//            holder.circularImageView_customerImg.setBorderColor(R.color.yellow);
            holder.circularImageView_customerImg.setBorderColor(context.getResources().getColor(R.color.yellow));
            holder.circularImageView_activeIcon.setBorderColor(context.getResources().getColor(R.color.yellow));
        }
*/
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
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);

                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                    holder.linearLayoutClickToRv.setVisibility(View.VISIBLE);
                                    holder.recyclerViewSelectedProductListInvoiceCard.setVisibility(View.VISIBLE);
                                    holder.imageView_down.setVisibility(View.GONE);
                                    holder.imageView_up.setVisibility(View.VISIBLE);
                                    invoiceItemModelArrayList = new ArrayList<>();

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

                                        /**
                                         * temp comment
                                         */
                                        invoiceItemModelArrayList.add(new InvoiceItemModel(itemno, qty, unitprice, itemname, item_bill));
//                                            Toast.makeText(mContext, String.valueOf(invoiceItemArrayList.size()), Toast.LENGTH_SHORT).show();
                                    }
                                  /*  rvAdapter_invoiceItemList = new RvAdapterInvoiceItemList(invoiceItemArrayList);
                                    holder.recyclerViewSelectedProductListInvoiceCard.setAdapter(rvAdapter_invoiceItemList);*/
                                    selectedItemListRvHandler(holder);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "JSONException: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(holder.mContext, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
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

        holder.imageView_webPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrinterConfirmation = new Dialog(context);
                dialogPrinterConfirmation.setContentView(R.layout.dialog_save_and_print);
                WindowManager.LayoutParams wmlp = dialogPrinterConfirmation.getWindow().getAttributes();
                wmlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialogPrinterConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogPrinterConfirmation.show();
                dialogPrinterConfirmation.setCancelable(false);

                EditText editText_printerName = dialogPrinterConfirmation.findViewById(R.id.edtTxt_printerName);

                EditText editText_printerUrl = dialogPrinterConfirmation.findViewById(R.id.edtTxt_printerUrl);
                Button btn_saveAndPrint = dialogPrinterConfirmation.findViewById(R.id.btn_saveAndPrint);
                Button btn_cancel = dialogPrinterConfirmation.findViewById(R.id.btn_cancelSaveAndPrint);
                RadioGroup radioGroupPrinterSize = dialogPrinterConfirmation.findViewById(R.id.rdG_printerSize);
                RelativeLayout rl_loadingMiniInvoiceDetails = dialogPrinterConfirmation.findViewById(R.id.rl_loadingMiniInvoice);
                RelativeLayout rl_printerDetails = dialogPrinterConfirmation.findViewById(R.id.rl_printerDetails);
//                RadioButton radioButtonPrinterSize;

                String name = holder.sharedPreferencesPrinterUrl.getString(PRINTER_NAME_KEY, "");
                String url = holder.sharedPreferencesPrinterUrl.getString(PRINTER_URL_KEY, "");
                editText_printerUrl.setText(url);
                editText_printerName.setText(name);

                btn_saveAndPrint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String printerUrl = editText_printerUrl.getText().toString();
                        String printer_id = editText_printerName.getText().toString();
                        int printerSizeSelected = radioGroupPrinterSize.getCheckedRadioButtonId();
                        RadioButton radioButtonPrinterSize = dialogPrinterConfirmation.findViewById(printerSizeSelected);
                        switch (radioButtonPrinterSize.getId()) {
                            case R.id.radioBtn_printerSize50mm:
                                printer_size = "50";
                                break;

                            case R.id.radioBtn_printerSize80mm:
                                printer_size = "80";
                                break;
                        }

                        if (!TextUtils.isEmpty(printer_id) && !TextUtils.isEmpty(printer_id)) {
                            SharedPreferences.Editor editor = holder.sharedPreferencesPrinterUrl.edit();
                            editor.putString(PRINTER_NAME_KEY, printer_id);
                            editor.putString(PRINTER_URL_KEY, printerUrl);
                            editor.apply();
                            printInvoice(printer_id, printer_size, jsonObjectData, printerUrl);
                            dialogPrinterConfirmation.cancel();
                        } else
                            Toast.makeText(context, "Empty fields", Toast.LENGTH_SHORT).show();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText_printerUrl.setText("");
                        dialogPrinterConfirmation.cancel();
                    }
                });
                getMiniInvoiceDetails(rl_loadingMiniInvoiceDetails, rl_printerDetails);
            }

            private void printInvoice(String printer_id, String printer_size, JSONObject jsonObjectData, String printerUrl) {
                HashMap<String, String> mapPrintInvoice = new HashMap<>();
                mapPrintInvoice.put("printer_id", printer_id);
                mapPrintInvoice.put("printer_size", printer_size);
                mapPrintInvoice.put("printer_data", jsonObjectData.toString());

                AppController.getAppController().getAppNetworkController().makeRequest(printerUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, mapPrintInvoice);
            }

            private void getMiniInvoiceDetails(RelativeLayout rl_loadingMiniInvoiceDetails, RelativeLayout rl_printerDetails) {
                HashMap<String, String> mapTemp = new HashMap<>();
                mapTemp.put(API_KEY, apikey);
                mapTemp.put(INVOICE_NO, String.valueOf(current.getInvoiceNo()));

                AppController.getAppController().getAppNetworkController().makeRequest(url_get_mini_invoice, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                rl_printerDetails.setVisibility(View.VISIBLE);
                                rl_loadingMiniInvoiceDetails.setVisibility(View.GONE);
                                jsonObjectData = jsonObject.getJSONObject("data");
//                                Toast.makeText(context, jsonObjectData.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            dialogPrinterConfirmation.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        dialogPrinterConfirmation.dismiss();
                    }
                }, mapTemp);
            }
        });
        holder.imageView_invoiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "InvoiceView click", Toast.LENGTH_SHORT).show();
/*
                Intent intentToPdfConverter = new Intent(holder.mContext, PdfConverterActivity.class);
                intentToPdfConverter.putExtra("invoiceNo", String.valueOf(current.getInvoiceNo()));
                intentToPdfConverter.putExtra("orgNo", String.valueOf(current.getOrgNo()));
                holder.mContext.startActivity(intentToPdfConverter);
*/

                String url = "http://pharmacy.egkroy.com/reports/iframe_invoice_report.php?invoiceno="+String.valueOf(current.getInvoiceNo())+"&orgno="+String.valueOf(current.getOrgNo());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(holder.mContext, Uri.parse(url));
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
                        invoiceItemModelArrayList = new ArrayList<>();

                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray itemsArray = data.getJSONArray("items");
                        if (itemsArray.length() > 1)
                            holder.tv_items.setText(" (" + String.valueOf(itemsArray.length()) + " Items)");
                        else
                            holder.tv_items.setText(" (" + String.valueOf(itemsArray.length()) + " Item)");
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
        rvAdapter_invoiceItemList = new RvAdapterInvoiceItemList(invoiceItemModelArrayList);
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
        TextView tv_items, tv_customer, tv_date, tv_dueDate, tv_amount, tv_discount, tv_invoiceNo, tv_paidOrDue, tv_deduction, tv_grandTotal,
                tv_dueText, tv_paidText;
        RecyclerView recyclerViewSelectedProductListInvoiceCard;
        LinearLayout linearLayoutClickToRv;
        RelativeLayout relativeLayoutExpandInvoiceItemList;
        ImageView imageView_up, imageView_down, imageView_webPrint, imageView_invoiceView;
        CircularImageView circularImageView_customerImg, circularImageView_activeIcon;
        SharedPreferences sharedPreferencesPrinterUrl;
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
            tv_paidOrDue = itemView.findViewById(R.id.tv_paidOrDueInvoiceCard);
            tv_deduction = itemView.findViewById(R.id.tv_deductionInvoiceCard);
            tv_grandTotal = itemView.findViewById(R.id.tv_grandTotalInvoiceCard);
            tv_dueText = itemView.findViewById(R.id.tv_dueText);
            tv_paidText = itemView.findViewById(R.id.tv_paidText);
            relativeLayoutExpandInvoiceItemList = itemView.findViewById(R.id.l3);
            imageView_up = itemView.findViewById(R.id.imgV_up);
            imageView_down = itemView.findViewById(R.id.imgV_down);
            imageView_webPrint = itemView.findViewById(R.id.imgV_webPrint);
            imageView_invoiceView = itemView.findViewById(R.id.imgV_invoice);
            circularImageView_customerImg = itemView.findViewById(R.id.imgV_customerImgInvoiceCard);
            circularImageView_activeIcon = itemView.findViewById(R.id.imgV_activeIcon);
            recyclerViewSelectedProductListInvoiceCard = itemView.findViewById(R.id.rv_selectedProductListInvoiceCard);
            linearLayoutClickToRv = itemView.findViewById(R.id.l5);
            mContext = itemView.getContext();

            sharedPreferencesPrinterUrl = mContext.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

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

