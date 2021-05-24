package com.agamilabs.smartshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackForBillDetails;
import com.agamilabs.smartshop.adapter.PurchaseInvoiceCardViewAdapter;
import com.agamilabs.smartshop.adapter.RvAdapterAddedItemViewPurchase;
import com.agamilabs.smartshop.adapter.RvAdapterBillDetails;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.PurchaseItemModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/*
optional input:
limit
paidflag
customerno
saledate
duedate
statusid
 */

public class PurchaseInvoiceViewerActivity extends AppCompatActivity implements ICallbackForBillDetails, View.OnClickListener {
    private static final String url_get_filtered_purchase_invoice = "http://pharmacy.egkroy.com/app-module/php/get_filtered_purchase_invoices.php";
    private static final String API_KEY = "apikey";
    private static final String PAGE_NO = "pageno";
    private static final String LIMIT = "limit";
    private static final String PAID_FLAG = "paidflag";
    private static final String CUSTOMER_NO = "customerno";
    private static final String SALE_DATE = "saledate";
    private static final String DUE_DATE = "duedate";
    private static final String STATUS_ID = "statusid";
    private static final String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private int pageNo = 1;
    private boolean isLoading = false;

    private RecyclerView recyclerViewInvoiceCardView, recyclerViewDetailsOfPurchaseBill;
    private Toolbar toolbar;
    private ArrayList<InvoiceModel> purchaseInvoiceModelList;
    private LinearLayout llProgressLoading, llDiscardInvoice;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout relativeLayoutBottomSheet;
    private ArrayList<LotsModel> billDetailsSummaryList;
//    private ArrayList<PurchaseItemModel> purchaseItemModelArrayList;
    private RvAdapterBillDetails rvAdapterBillDetails;
    private FrameLayout frameLayoutDarkBg;




    private PurchaseInvoiceCardViewAdapter purchaseInvoiceCardViewAdapter;
    //apex
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_invoice_viewer);

        recyclerViewInvoiceCardView = findViewById(R.id.rv_invoiceCardView);
        llProgressLoading = findViewById(R.id.l2);
        toolbar = findViewById(R.id.main_app_bar);
        relativeLayoutBottomSheet = findViewById(R.id.bottom_sheet_details_of_purchase);
        bottomSheetBehavior = BottomSheetBehavior.from(relativeLayoutBottomSheet);
        recyclerViewDetailsOfPurchaseBill = findViewById(R.id.rv_productView);
        llDiscardInvoice = findViewById(R.id.ll_discardInvoice);
        frameLayoutDarkBg = findViewById(R.id.purchaseBillViewerDarkBg);

//        purchaseItemModelArrayList = new ArrayList<>();



        sheetBehaviorHandler();

        handleAppbar();
        handleBackPressButton();
        rvHandler();
        getFilteredPurchaseInvoiceList();

        llDiscardInvoice.setOnClickListener(this);
        frameLayoutDarkBg.setOnClickListener(this);
    }

    private void handleBackPressButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sheetBehaviorHandler() {
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    frameLayoutDarkBg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


    private void getFilteredPurchaseInvoiceList() {
        HashMap<String, String> map_sale_input = new HashMap<>();
        map_sale_input.put(API_KEY, apikey);
//        map_sale_input.put(PAGE_NO, String.valueOf(pageNo));
        map_sale_input.put(PAGE_NO, String.valueOf(pageNo));

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_purchase_invoice, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("mytag", "onResponse: "+response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        llProgressLoading.setVisibility(View.GONE);
                        JSONArray data_sale_invoice_list = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data_sale_invoice_list.length(); i++) {
                            JSONObject dataObject = data_sale_invoice_list.getJSONObject(i);
                            int supplierNo = dataObject.getInt("supplieno");
                            String pDate = dataObject.getString("pdate");
                            String dueDate = dataObject.getString("duedate");
                            int currency = dataObject.getInt("currency");
                            int statusId = dataObject.getInt("statusid");
                            int orgNo = dataObject.getInt("orgno");
                            int billNo = dataObject.getInt("billno");
                            double discount = dataObject.getDouble("discount");
                            double paid = dataObject.getDouble("paid");
                            int addedBy = dataObject.getInt("addedby");
                            double deduction = dataObject.getDouble("deduction");
                            double total = dataObject.getDouble("total");
                            String name = dataObject.getString("name");
                            double totalAmountToPay = dataObject.getDouble("total_amount_to_pay");
                            double unPaidAmount = dataObject.getDouble("unpaid_amount");

                            purchaseInvoiceModelList.add(new InvoiceModel(supplierNo, pDate, dueDate, currency, statusId, orgNo, billNo,
                                    discount, paid, addedBy, deduction, total, name, totalAmountToPay, unPaidAmount));
                            purchaseInvoiceCardViewAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PurchaseInvoiceViewerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, map_sale_input);

        recyclerViewInvoiceCardView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading)
                {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == purchaseInvoiceModelList.size() - 1)
                    {
                        //bottom of list
                        purchaseInvoiceModelList.add(new InvoiceModel(0));
                        purchaseInvoiceCardViewAdapter.notifyItemInserted(purchaseInvoiceModelList.size());
                        pageNo++;
//                        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                purchaseInvoiceModelList.remove(purchaseInvoiceModelList.size() - 1 );
                                int scrollPosition = purchaseInvoiceModelList.size();
                                purchaseInvoiceCardViewAdapter.notifyItemRemoved(scrollPosition);

                                HashMap<String, String> mapForScroll = new HashMap<>();
                                mapForScroll.put(API_KEY, apikey);
                                mapForScroll.put(PAGE_NO, String.valueOf(pageNo));
                                mapForScroll.put(LIMIT, String.valueOf(10));

                                AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_purchase_invoice, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                JSONArray data_sale_invoice_list = jsonObject.getJSONArray("data");
                                                for (int i = 0; i < data_sale_invoice_list.length(); i++) {
                                                    JSONObject dataObject = data_sale_invoice_list.getJSONObject(i);
                                                    int supplierNo = dataObject.getInt("supplieno");
                                                    String pDate = dataObject.getString("pdate");
                                                    String dueDate = dataObject.getString("duedate");
                                                    int currency = dataObject.getInt("currency");
                                                    int statusId = dataObject.getInt("statusid");
                                                    int orgNo = dataObject.getInt("orgno");
                                                    int billNo = dataObject.getInt("billno");
                                                    double discount = dataObject.getDouble("discount");
                                                    double paid = dataObject.getDouble("paid");
                                                    int addedBy = dataObject.getInt("addedby");
                                                    double deduction = dataObject.getDouble("deduction");
                                                    double total = dataObject.getDouble("total");
                                                    String name = dataObject.getString("name");
                                                    double totalAmountToPay = dataObject.getDouble("total_amount_to_pay");
                                                    double unPaidAmount = dataObject.getDouble("unpaid_amount");

                                                    purchaseInvoiceModelList.add(new InvoiceModel(supplierNo, pDate, dueDate, currency, statusId, orgNo, billNo,
                                                            discount, paid, addedBy, deduction, total, name, totalAmountToPay, unPaidAmount));
                                                    purchaseInvoiceCardViewAdapter.notifyDataSetChanged();
                                                }
                                            } else
                                                Toast.makeText(getApplicationContext(), "No more data load.", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getApplicationContext(), "Loaded", Toast.LENGTH_SHORT).show();
                                            isLoading = false;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }, mapForScroll);
                            }
                        }, 2000);
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void handleAppbar() {
        this.setSupportActionBar(toolbar);
        this.setTitle("Purchase Invoices");
    }

    private void rvHandler() {
        recyclerViewInvoiceCardView.setHasFixedSize(true);
        recyclerViewInvoiceCardView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetailsOfPurchaseBill.setHasFixedSize(true);
        recyclerViewDetailsOfPurchaseBill.setLayoutManager(new LinearLayoutManager(this));
        purchaseInvoiceModelList = new ArrayList<>();

        purchaseInvoiceCardViewAdapter = new PurchaseInvoiceCardViewAdapter(purchaseInvoiceModelList, this, this);
        recyclerViewInvoiceCardView.setAdapter(purchaseInvoiceCardViewAdapter);
        purchaseInvoiceCardViewAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
              /*  Intent intentSaleScanner = new Intent(getApplicationContext(), FullScannerActivityPurchase.class);
                intentSaleScanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentSaleScanner);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /*Intent intentSaleScanner = new Intent(getApplicationContext(), FullScannerActivityPurchase.class);
        intentSaleScanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSaleScanner);*/
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            this.finish();
            super.onBackPressed();
        }
    }

    private void mAdpaterHandler(ArrayList<PurchaseItemModel> purchaseItemModelArrayList) {
        if (purchaseItemModelArrayList.size() > 0) {
            rvAdapterBillDetails = new RvAdapterBillDetails(purchaseItemModelArrayList, this, this);
            recyclerViewDetailsOfPurchaseBill.setAdapter(rvAdapterBillDetails);
            rvAdapterBillDetails.notifyDataSetChanged();
        }
    }

    @Override
    public void handleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            frameLayoutDarkBg.setVisibility(View.VISIBLE);
        } else{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            frameLayoutDarkBg.setVisibility(View.GONE);
        }
    }

    @Override
    public void dataParsedBillDetails(ArrayList<PurchaseItemModel> purchaseItemModelArrayList) {
        mAdpaterHandler(purchaseItemModelArrayList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.purchaseBillViewerDarkBg:
            case R.id.ll_discardInvoice:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;


        }
    }
}