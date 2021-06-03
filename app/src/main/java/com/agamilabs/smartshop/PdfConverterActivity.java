package com.agamilabs.smartshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class PdfConverterActivity extends AppCompatActivity {
    WebView webView;
    String url;
    String invoiceNo;
    String orgNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_converter);

        Intent intentData = getIntent();
         invoiceNo = intentData.getStringExtra("invoiceNo");
         orgNo = intentData.getStringExtra("orgNo");

        url = "http://pharmacy.egkroy.com/reports/iframe_invoice_report.php?invoiceno="+invoiceNo+"&orgno="+orgNo;

    /*    url = "http://pharmacy.egkroy.com/reports/iframe_invoice_report.php?invoiceno="+invoiceNo+"&orgno="+orgNo;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));*/
        //Initializing the WebView
        final WebView webView=(WebView)findViewById(R.id.webViewMain);

        //Initializing the Button
        Button savePdfBtn=(Button)findViewById(R.id.savePdfBtn);

        //Setting we View Client
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //initializing the printWeb Object
                PdfConverterActivity.this.webView = webView;
            }
        });

        //loading the URL
//        webView.loadUrl("https://www.google.com");
        Log.d("invoiceNoChecking", "onCreate: "+"http://pharmacy.egkroy.com/reports/iframe_invoice_report.php?invoiceno="+invoiceNo+"&orgno="+orgNo);
        webView.loadUrl(url);
        //setting clickListener for Save Pdf Button
        savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PdfConverterActivity.this.webView !=null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //Calling createWebPrintJob()
                        PrintTheWebPage(PdfConverterActivity.this.webView);
                    }else
                    {
                        //Showing Toast message to user
                        Toast.makeText(PdfConverterActivity.this, "Not available for device below Android LOLLIPOP", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Showing Toast message to user
                    Toast.makeText(PdfConverterActivity.this, "WebPage not fully loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //object of print job
    PrintJob printJob;

    //a boolean to check the status of printing
    boolean printBtnPressed=false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void PrintTheWebPage(WebView webView) {

        //set printBtnPressed true
        printBtnPressed=true;

        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        //setting the name of job
//        String jobName = getString(R.string.app_name) + " webpage"+webView.getUrl();
        String jobName = "invoice - "+invoiceNo;

        // Creating  PrintDocumentAdapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        assert printManager != null;
        printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        if(printJob!=null &&printBtnPressed) {
            if (printJob.isCompleted()) {
                //Showing Toast Message
                Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
            } else if (printJob.isStarted()) {
                //Showing Toast Message
                Toast.makeText(this, "isStarted", Toast.LENGTH_SHORT).show();

            } else if (printJob.isBlocked()) {
                //Showing Toast Message
                Toast.makeText(this, "isBlocked", Toast.LENGTH_SHORT).show();

            } else if (printJob.isCancelled()) {
                //Showing Toast Message
                Toast.makeText(this, "isCancelled", Toast.LENGTH_SHORT).show();

            } else if (printJob.isFailed()) {
                //Showing Toast Message
                Toast.makeText(this, "isFailed", Toast.LENGTH_SHORT).show();

            } else if (printJob.isQueued()) {
                //Showing Toast Message
                Toast.makeText(this, "isQueued", Toast.LENGTH_SHORT).show();

            }
            //set printBtnPressed false
            printBtnPressed=false;
        }
    }
}