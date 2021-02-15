package com.agamilabs.smartshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.agamilabs.smartshop.adapter.RvAdapter_selectedProductDetailsView;
import com.agamilabs.smartshop.model.InvoiceModel;

import java.util.ArrayList;
import java.util.List;

public class InvoiceViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerViewInvoiceCardView;
    private RvAdapter_selectedProductDetailsView rvAdapter_selectedProductDetailsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_viewer);

        recyclerViewInvoiceCardView = findViewById(R.id.rv_invoiceCardView);

        rvHandler();
    }

    private void rvHandler() {
        recyclerViewInvoiceCardView.setHasFixedSize(true);
        recyclerViewInvoiceCardView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        ArrayList<InvoiceModel> invoiceModelList = (ArrayList<InvoiceModel>) args.getSerializable("selectedProductList");
        rvAdapter_selectedProductDetailsView = new RvAdapter_selectedProductDetailsView(invoiceModelList, this);
        recyclerViewInvoiceCardView.setAdapter(rvAdapter_selectedProductDetailsView);
        rvAdapter_selectedProductDetailsView.notifyDataSetChanged();
    }
}