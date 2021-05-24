package com.agamilabs.smartshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.adapter.SaleInvoiceCardViewAdapter;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;

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

public class SaleInvoiceViewerActivity extends AppCompatActivity {
    private static final String url_get_filtered_sale_invoice = "http://pharmacy.egkroy.com/app-module/php/get_filtered_sale_invoices.php";
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

    private RecyclerView recyclerViewInvoiceCardView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ArrayList<InvoiceModel> saleInvoiceModelList;
//    private ProgressBar progressBarLoading;
    private LinearLayout llProgressLoading;

    private SaleInvoiceCardViewAdapter rvAdapter_selectedProductDetailsView;
//apex
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_viewer);

        recyclerViewInvoiceCardView = findViewById(R.id.rv_invoiceCardView);
        llProgressLoading = findViewById(R.id.l2);
        toolbar = findViewById(R.id.main_app_bar);
//        progressBar = findViewById(R.id.progress_miniInvoiceReq);

        handleAppbar();
        handleBackPressButton();
        rvHandler();
        getFilteredSaleInvoiceList();
    }

    private void handleBackPressButton() {

    }

    private void getFilteredSaleInvoiceList() {
        HashMap<String, String> map_sale_input = new HashMap<>();
        map_sale_input.put(API_KEY, apikey);
//        map_sale_input.put(PAGE_NO, String.valueOf(pageNo));
        map_sale_input.put(PAGE_NO, String.valueOf(pageNo));

        mGetFilteredInvoice(map_sale_input);

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
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == saleInvoiceModelList.size() - 1)
                    {
                        //bottom of list
                        saleInvoiceModelList.add(new InvoiceModel(null));
                        rvAdapter_selectedProductDetailsView.notifyItemInserted(saleInvoiceModelList.size());
                        pageNo++;
//                        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saleInvoiceModelList.remove(saleInvoiceModelList.size() - 1 );
                                int scrollPosition = saleInvoiceModelList.size();
                                rvAdapter_selectedProductDetailsView.notifyItemRemoved(scrollPosition);

                                HashMap<String, String> mapForScroll = new HashMap<>();
                                mapForScroll.put(API_KEY, apikey);
                                mapForScroll.put(PAGE_NO, String.valueOf(pageNo));
                                mapForScroll.put(LIMIT, String.valueOf(10));
                                mGetFilteredInvoice(mapForScroll);
                            }
                        }, 2000);
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void mGetFilteredInvoice(HashMap<String, String> mapForScroll) {
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_sale_invoice, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        llProgressLoading.setVisibility(View.GONE);
                        Log.d("saleInvoiceView", "onResponse: "+response);
//                        Toast.makeText(SaleInvoiceViewerActivity.this, response, Toast.LENGTH_SHORT).show();
                        JSONArray data_sale_invoice_list = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data_sale_invoice_list.length(); i++) {
                            JSONObject dataObject = data_sale_invoice_list.getJSONObject(i);
                            int customerNo = dataObject.getInt("customerno");
                            String sDate = dataObject.getString("sdate");
                            String dueDate = dataObject.getString("duedate");
                            int currency = dataObject.getInt("currency");
                            int statusId = dataObject.getInt("statusid");
                            int orgNo = dataObject.getInt("orgno");
                            int invoiceNo = dataObject.getInt("invoiceno");
                            double discount = dataObject.getDouble("discount");
                            double paid = dataObject.getDouble("paid");
                            int addedBy = dataObject.getInt("addedby");
                            double deduction = dataObject.getDouble("deduction");
                            double amount = dataObject.getDouble("amount");
                            String cShortText = dataObject.getString("cshorttext");
                            String cText = dataObject.getString("ctext");
                            String name = dataObject.getString("name");
                            double totalAmountToPay = dataObject.getDouble("total_amount_to_pay");
                            double unPaidAmount = dataObject.getDouble("unpaid_amount");

                            JSONArray jsonArrayPayments = dataObject.getJSONArray("payments");
                            ArrayList<String> arrayListPayments = new ArrayList<>();
                            for (int j=0; j<jsonArrayPayments.length(); j++)
                            {
                                JSONObject objectPayments = jsonArrayPayments.getJSONObject(j);
                                String payment_amount = objectPayments.getString("accno");
                                arrayListPayments.add(payment_amount);
                            }

                            saleInvoiceModelList.add(new InvoiceModel(customerNo, sDate, dueDate, currency, statusId, orgNo, invoiceNo,
                                    discount, paid, addedBy, deduction, amount, cShortText, cText, name, totalAmountToPay, unPaidAmount, arrayListPayments));
                            rvAdapter_selectedProductDetailsView.notifyDataSetChanged();
                        }
                    } else
                        Toast.makeText(SaleInvoiceViewerActivity.this, "No more data load.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SaleInvoiceViewerActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SaleInvoiceViewerActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SaleInvoiceViewerActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapForScroll);

    }

    private void handleAppbar() {
        this.setSupportActionBar(toolbar);
        this.setTitle("Sale Invoices");

        //add backPress button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void rvHandler() {
        recyclerViewInvoiceCardView.setHasFixedSize(true);
        recyclerViewInvoiceCardView.setLayoutManager(new LinearLayoutManager(this));
        saleInvoiceModelList = new ArrayList<>();
        rvAdapter_selectedProductDetailsView = new SaleInvoiceCardViewAdapter(saleInvoiceModelList, this);
        recyclerViewInvoiceCardView.setAdapter(rvAdapter_selectedProductDetailsView);
        rvAdapter_selectedProductDetailsView.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
               /* Intent intentSaleScanner = new Intent(getApplicationContext(), FullScannerActivitySale.class);
                intentSaleScanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentSaleScanner);*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      /*  Intent intentSaleScanner = new Intent(getApplicationContext(), FullScannerActivitySale.class);
        intentSaleScanner.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSaleScanner);*/
        this.finish();
    }


}