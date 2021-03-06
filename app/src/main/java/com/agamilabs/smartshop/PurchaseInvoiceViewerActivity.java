package com.agamilabs.smartshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PurchaseInvoiceViewerActivity extends AppCompatActivity {
    private static final String url_get_filtered_purchase_invoice = "http://pharmacy.egkroy.com/app-module/php/get_filtered_purchase_invoices.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_invoice_viewer);
    }
}