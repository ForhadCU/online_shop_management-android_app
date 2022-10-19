package com.agamilabs.smartshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromFullScannerActivitySale;
import com.agamilabs.smartshop.Interfaces.ICallbackClickHandler;
import com.agamilabs.smartshop.Interfaces.ProductDetailsInterfaceSale;
import com.agamilabs.smartshop.adapter.RvAdapterAddedItemViewSale;
import com.agamilabs.smartshop.adapter.RvAdapterPersonSearch;
import com.agamilabs.smartshop.adapter.RvAdapterProductSearch;
import com.agamilabs.smartshop.adapter.SaleInvoiceCardViewAdapter;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.database.DbHelper;
import com.agamilabs.smartshop.model.Customer;
import com.agamilabs.smartshop.model.InvoiceItemModel;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.Products;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FullScannerActivitySale extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener, ProductDetailsInterfaceSale, View.OnClickListener, ICallBackFromFullScannerActivitySale, ICallbackClickHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    final static String outputFilePath = "/home/forhad/Desktop/write.txt";
    static final int READ_BLOCK_SIZE = 100;


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private RecyclerView rv_selectedProduct;
    private RvAdapterAddedItemViewSale rvAdapter_selectedProductView;
    private Dialog dialog_cross;
    private BottomSheetBehavior sheetBehavior;
    private RelativeLayout bottomsSheetLayout;
    private EditText editText_discount, editText_deduction;
    private TextView textView_subTotal, textView_total, textViewSubtotalScannerDisplay;
    private ViewGroup contentFrame;
    private Button buttonSaveInvoice;
    private ImageButton imageButtonFlashOn, imageButtonFlashOff, imageButtonFocusOn, imageButtonFocusOff, imageButtonPersonSearch, imageButtonProductSearch;
    private ImageButton imageButtonDiscountIncrease, imageButtonDiscountDecrease;
    private Dialog dialog, dialogAddCustomer, dialogProductSearch;
    private TextView textViewCustomerName, textViewCustomerNameScannerDisplay, textViewDiscountOverAll;
    private LinearLayout relativeLayoutBottomSheetComponents;
    private LinearLayout linearLayoutBottomSheetCustomerName;
    private TextView textViewCartBadge;
    private RadioGroup radioGroupDiscount;
    private RadioButton discountRadioButton;
    private ProgressBar progressBarSkuRequest;
    private Button buttonCashPaid, buttonBKashPaid, buttonBankPaid;
    private LinearLayout llDiscardInvoice;
    private TextView tv_AdjustFraction;
    private AlertDialog.Builder builder;
    private FrameLayout frameLayoutCart;

    private DbHelper dbHelper;
    private List<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItemModel> invoiceItemModelList;
    private ArrayList<Customer> customerArrayList;
    private ArrayList<Products> productsArrayList;
    private ArrayList<LotsModel> lotsModelArrayList;
    //    private ArrayList<String> productArrayList;
    private ArrayList<String> userList;
    private InvoiceModel invoiceModel;
    private RvAdapterPersonSearch rvAdapterPersonSearch;
    private RvAdapterProductSearch rvAdapterProductSearch;
    private SaleInvoiceCardViewAdapter saleInvoiceCardViewAdapter;

    private Customer customer;
    private Products products;

    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private int count = 0, pageNo = 1, customerFilteredPageNo = 1;
    private int productPageNo = 1, productFilteredPageNo = 1;
    private boolean isLoading = false;
    private boolean isLoading2 = false;
    private boolean isLoading3 = false;
    private boolean isLoading4 = false;

    private double discount, deduction, total = 0;
    private double subTotal = 0.00;
    ICallBackFromFullScannerActivitySale icallBackTest;

    private String url_get_details_of_item_of_sku = "http://pharmacy.egkroy.com/app-module/php/get_details_of_item_of_sku.php";
    private String url_get_org_items_by_name = "http://pharmacy.egkroy.com/app-module/php/get_org_items_by_name.php";
    private String url_get_filtered_clients = "http://pharmacy.egkroy.com/app-module/php/get_filtered_clients.php";
    private String url_insert_a_customer = "http://pharmacy.egkroy.com/app-module/php/insert_a_customer.php";
    private String url_insert_a_saleinvoice = "http://pharmacy.egkroy.com/app-module/php/insert_a_saleinvoice.php";
    private String url_get_details_of_item = "http://pharmacy.egkroy.com/app-module/php/get_details_of_item.php";
    private String url_7_all_customer_and_supplier = "http://pharmacy.egkroy.com/app-module/php/get_clients_count.php";
    private static final String API_KEY = "apikey";
    private static final String PRODUCT_SEARCH_KEY = "search_key";
    private static final String PRODUCT_PAGE_NO = "page";
    private static final String LIMIT = "limit";
    private static final String CUSTOMER_PAGE_NO = "pageno";
    private static final String CUSTOMER_NAME = "name";
    private static final String CUSTOMER_MOBILE_NO = "mobileno";
    private static final String TYPE = "type";
    private static final String type_name = "1";
    private static final String CUSTOMER_SEARCH_KEY = "searchkey";

    private String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private String customer_no;
    private String currentDate;


    //apex
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        checkAndroidVersion();
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }


        setContentView(R.layout.activity_simple_scanner_sale);
//        setupToolbar();
//        Objects.requireNonNull(getSupportActionBar()).hide();

        /**
         * pick current date
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(new Date());

        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        bottomsSheetLayout = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomsSheetLayout);
        sheetBehaviorHandler();

        invoiceItemModelList = new ArrayList<>();
        mScannerView = new ZXingScannerView(this);

        rv_selectedProduct = findViewById(R.id.rv_productView);
        textView_subTotal = findViewById(R.id.tv_subTotal);
        textView_total = findViewById(R.id.tv_Total);
        textViewSubtotalScannerDisplay = findViewById(R.id.tv_subTotalScannerDisplay);
        textViewCustomerNameScannerDisplay = findViewById(R.id.tv_cusNameScannerDisplay);
        textViewDiscountOverAll = findViewById(R.id.tv_discountOverAll);
        tv_AdjustFraction = findViewById(R.id.tv_adjustFraction);
        editText_discount = findViewById(R.id.edtTxt_discount);
        editText_deduction = findViewById(R.id.edtTxt_deduction);
        imageButtonFlashOn = findViewById(R.id.imgBtn_flash_on);
        imageButtonFlashOff = findViewById(R.id.imgBtn_flash_off);
        imageButtonFocusOn = findViewById(R.id.imgBtn_focus_on);
        imageButtonFocusOff = findViewById(R.id.imgBtn_focus_off);
        imageButtonPersonSearch = findViewById(R.id.imgBtn_personSearch);
        imageButtonProductSearch = findViewById(R.id.imgBtn_prodcutSearch);
        imageButtonDiscountIncrease = findViewById(R.id.btn_discountIncrease);
        imageButtonDiscountDecrease = findViewById(R.id.btn_discountDecrease);
        buttonCashPaid = findViewById(R.id.btn_cashPaid);
        buttonBKashPaid = findViewById(R.id.btn_bkashPaid);
        buttonBankPaid = findViewById(R.id.btn_bankPaid);
        llDiscardInvoice = findViewById(R.id.ll_discardInvoice);
        textViewCustomerName = findViewById(R.id.tv_bottomSheetCustomerName);
        relativeLayoutBottomSheetComponents = findViewById(R.id.l_bottomSheet_components);
        linearLayoutBottomSheetCustomerName = findViewById(R.id.l_bottomSheet_customerName);
        textViewCartBadge = findViewById(R.id.tv_cartBadge);
        radioGroupDiscount = findViewById(R.id.radioGroupDiscount);
        progressBarSkuRequest = findViewById(R.id.progress_skuRequest);
        frameLayoutCart = findViewById(R.id.l3);


        builder = new AlertDialog.Builder(this);

//        userList();
//        productList();
        recyclerViewHandler();
        setupFormats();
        contentFrame.addView(mScannerView);

        imageButtonFocusOn.setOnClickListener(this);
        imageButtonFocusOff.setOnClickListener(this);
        imageButtonFlashOn.setOnClickListener(this);
        imageButtonFlashOff.setOnClickListener(this);
        imageButtonPersonSearch.setOnClickListener(this);
        imageButtonProductSearch.setOnClickListener(this);
        imageButtonDiscountIncrease.setOnClickListener(this);
        imageButtonDiscountDecrease.setOnClickListener(this);
        buttonCashPaid.setOnClickListener(this);
        buttonBKashPaid.setOnClickListener(this);
        buttonBankPaid.setOnClickListener(this);
        llDiscardInvoice.setOnClickListener(this);
        tv_AdjustFraction.setOnClickListener(this);
        frameLayoutCart.setOnClickListener(this);

//        defaultClientSelection();
//        totalBillHandler();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }
    }

    private void defaultClientSelection() {
        HashMap<String, String> mapGetClients = new HashMap<>();
        mapGetClients.put(CUSTOMER_PAGE_NO, String.valueOf(1));
        mapGetClients.put(LIMIT, String.valueOf(10));
        mapGetClients.put(API_KEY, apikey);
        mapGetClients.put(TYPE, type_name);

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_clients, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        JSONObject customerData = customerDataArray.getJSONObject(0);
//                        customerClickHandler(, customerData.getString("name"));
                        customer_no = customerData.getString("id");
                        textViewCustomerName.setText(customerData.getString("name"));
                        textViewCustomerNameScannerDisplay.setText(customerData.getString("name"));
                        linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);
                        textViewCustomerNameScannerDisplay.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
            }
        }, mapGetClients);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this, "onStart called", Toast.LENGTH_SHORT).show();
    }

    public void totalBillHandler() {
        subTotal = 0;
        textViewCartBadge.setText(String.valueOf(invoiceItemModelList.size()));
        if (invoiceItemModelList.size() == 0 && relativeLayoutBottomSheetComponents.getVisibility() == View.VISIBLE) {
            relativeLayoutBottomSheetComponents.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < invoiceItemModelList.size(); i++) {
                InvoiceItemModel current = invoiceItemModelList.get(i);
                subTotal = subTotal + current.getItem_bill();
                String num = String.valueOf((int) current.getItem_bill());
            }
            textViewSubtotalScannerDisplay.setText(String.format("%.2f", subTotal));
            textView_subTotal.setText(String.format("%.2f", subTotal));
            textView_total.setText(String.format("%.2f", subTotal));

            editText_discount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int discountTypeSelected = radioGroupDiscount.getCheckedRadioButtonId();
                    discountRadioButton = findViewById(discountTypeSelected);

                    if (TextUtils.isEmpty(s.toString().trim())) {
                        discount = 0;
                        textView_total.setText(String.format("%.2f", (subTotal - deduction)));
                        textViewDiscountOverAll.setText(String.format("%.2f", discount));
                    } else {
                        if (discountRadioButton.getText().equals("%")) {
                            double tempDisc = Double.parseDouble(s.toString());
                            if (tempDisc <= 100) {
                                discount = (tempDisc * subTotal) / 100;
                                double temp = subTotal - (discount + deduction);
                                total = temp;
                                textView_total.setText(String.format("%.2f", temp));
                                textViewDiscountOverAll.setText(String.format("%.2f", discount));
                            } else {
                                discount = (tempDisc * subTotal) / 100;
                                textView_total.setText("0.00");
                                textViewDiscountOverAll.setText(String.format("%.2f", discount));
                            }

                        } else if (discountRadioButton.getText().equals("Tk")) {
                            discount = Double.parseDouble(s.toString());
                            if (discount <= subTotal) {
                                double temp = subTotal - (discount + deduction);
                                total = temp;
                                textView_total.setText(String.format("%.2f", temp));
                                textViewDiscountOverAll.setText(String.format("%.2f", discount));
                            } else {
                                textView_total.setText("0.00");
                                textViewDiscountOverAll.setText(String.format("%.2f", discount));
                            }

                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editText_deduction.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        deduction = 0;
                        textView_total.setText(String.format("%.2f", subTotal - discount));
                    } else {
                        deduction = Double.parseDouble(s.toString());
                        double temp = subTotal - (deduction + discount);
                        total = temp;
                        textView_total.setText(String.format("%.2f", temp));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


    }

    private void sheetBehaviorHandler() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void recyclerViewHandler() {
        rv_selectedProduct.setHasFixedSize(true);
        rv_selectedProduct.setLayoutManager(new LinearLayoutManager(this));
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();

        } else {
            // code for lollipop and pre-lollipop devices
        }

    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
//        Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
        defaultClientSelection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dbHelper.mDeleteProductList();
        invoiceItemModelList.clear();
//        Toast.makeText(this, "onDestroy!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        dbHelper.mDeleteProductList();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
//        Toast.makeText(this, "onPause!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(this, "onStop!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }
*/

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/


    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }

//        mJSON_parser(rawResult);
//        progressBarSkuRequest.setVisibility(View.VISIBLE);
//        mSendVolleyRequest(rawResult);
    }

    //00001
    private void mSendVolleyRequest(Result rawResult) {
        HashMap<String, String> map = new HashMap<>();
        map.put(API_KEY, apikey);
        map.put("sku", "15167");
        Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_details_of_item_of_sku, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    Toast.makeText(FullScannerActivitySale.this, response, Toast.LENGTH_SHORT).show();

                    JSONObject object = new JSONObject(response);
                    if (object.getString("error").equalsIgnoreCase("false")) {
                        progressBarSkuRequest.setVisibility(View.GONE);
                        JSONObject data = object.getJSONObject("data");
                        lotsModelArrayList = new ArrayList<>();
                        String itemNo = data.getString("itemno");
                        int itemOrgno = data.getInt("orgno");
                        String product_name = data.getString("itemname");
                        String productRate = data.getString("salerate");
                        String item_id = data.getString("upc");
                        String expirydate = data.getString("hasexpiry");
                        String discount_percentage = data.getString("discount_rate");
                        String unitid = data.getString("unitid");

                        //                        lotsModelArrayList = mGetLotsArrayList(itemNo);
                        HashMap<String, String> mapGetProductById = new HashMap<>();
                        mapGetProductById.put(API_KEY, apikey);
                        mapGetProductById.put("itemno", itemNo);
                        AppController.getAppController().getAppNetworkController().makeRequest(url_get_details_of_item, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                Toast.makeText(FullScannerActivitySale.this, response, Toast.LENGTH_LONG).show();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                        lotsModelArrayList = new ArrayList<>();
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        JSONArray jsonArraySku = data.getJSONArray("lots");

                                        for (int i = 0; i < jsonArraySku.length(); i++) {
                                            JSONObject object = jsonArraySku.getJSONObject(i);
                                            if (object.getInt("qty") > 0) {

                                                int itemno = object.getInt("itemno");
                                                int orgno = object.getInt("orgno");
                                                int lotno = object.getInt("lotno");
                                                String sku = object.getString("sku");
                                                int qty = object.getInt("qty");
                                                String expirydateOfLots = object.getString("expirydate");
                                                lotsModelArrayList.add(new LotsModel(itemno, orgno, lotno, sku, qty, expirydateOfLots, false, false, 0));
                                            }
                                        }
                                        showMessageDialog(itemOrgno, itemNo, product_name, productRate, unitid, expirydate, discount_percentage, lotsModelArrayList);

                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(FullScannerActivitySale.this, "JsonException: " + e.toString(), Toast.LENGTH_SHORT).show();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }, mapGetProductById);
//                        showMessageDialog(itemNo, product_name, productRate, unitid, expirydate, discount_percentage, lotsModelArrayList);

/* showMessageDialog("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString(),
                                itemNo, product_name, productRate, item_id, unitid, expirydate, discount_percentage);*/

                    } else if (object.getString("error").equalsIgnoreCase("true")) {
                        Toast.makeText(FullScannerActivitySale.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullScannerActivitySale.this, "JSONException: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, map);

    }

/*
    private ArrayList<LotsModel> mGetLotsArrayList(String itemNo) {
        HashMap<String, String> mapGetProductById = new HashMap<>();
        mapGetProductById.put(API_KEY, apikey);
        mapGetProductById.put("itemno", itemNo);

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_details_of_item, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<LotsModel> lotsModelArrayList1 = new ArrayList<>();

                    Toast.makeText(FullScannerActivitySale.this, response, Toast.LENGTH_SHORT).show();
                    if (jsonObject.getString("error").equalsIgnoreCase("false"))
                    {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray jsonArraySku = data.getJSONArray("lots");

                        for (int i = 0; i < jsonArraySku.length(); i++) {
                            JSONObject object = jsonArraySku.getJSONObject(i);
                            if (object.getInt("qty") > 0) {

                                int itemno = object.getInt("itemno");
                                int orgno = object.getInt("orgno");
                                int lotno = object.getInt("lotno");
                                String sku = object.getString("sku");
                                int qty = object.getInt("qty");
                                String expirydateOfLots = object.getString("expirydate");
                                lotsModelArrayList1.add(new LotsModel(itemno, orgno, lotno, sku, qty, expirydateOfLots, false));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullScannerActivitySale.this, "JSONException: "+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, "VolleyError: "+ error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProductById);
        return lotsModelArrayList1 ;
    }
*/

    /*    public void showMessageDialogByScan(String no, String itemNo, String product_name, String productRate, String unitid, String expirydate, String discount_percentage, ArrayList<String> arrayListSku) {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            ScanerDilogFragmentActivity scanerDilogFragmentActivity = new ScanerDilogFragmentActivity(this, itemNo, product_name, productRate, unitid, expirydate, discount_percentage, arrayListSku);
            scanerDilogFragmentActivity.show(fragmentManager, "scannerDialogFragmentActivity");

        }*/
    public void showMessageDialog(int orgNo, String itemNo, String product_name, String productRate, String unitid, String expirydate,
                                  String discount_percentage, ArrayList<LotsModel> arrayListSku) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        ScanerDilogFragmentActivitySale scanerDilogFragmentActivitySale = new ScanerDilogFragmentActivitySale(this, orgNo, itemNo, product_name,
                productRate, unitid, expirydate, discount_percentage, arrayListSku, invoiceItemModelList);
        scanerDilogFragmentActivitySale.show(fragmentManager, "scannerDialogFragmentActivity");

    }

/*    public void showMessageDialog(String itemNo, String product_name, String saleRate) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        ScanerDilogFragmentActivity scanerDilogFragmentActivity = new ScanerDilogFragmentActivity(itemNo, product_name, saleRate);
        scanerDilogFragmentActivity.show(fragmentManager, "scannerDialogFragmentActivity");
    }*/

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }


    @Override
    public void dataParsingMethod(boolean continueScanning, int orgNo, String itemNo, String productName, String productQuantity, String product_price, String totalBill,
                                  ArrayList<String> item_id, String unitid, String expirydate, String discount_percentage) {
        //product save via Model
        boolean hasItem = false;
        for (int i = 0; i < invoiceItemModelList.size(); i++) {
            InvoiceItemModel current = invoiceItemModelList.get(i);
            if (current.getItemno() == Integer.parseInt(itemNo) && current.getOrgno() == orgNo) {
                hasItem = true;
                current.setItem_id(item_id);
                current.setQty(Integer.parseInt(productQuantity));
                current.setRate(Integer.parseInt(product_price));
                current.setDiscount_percentage(discount_percentage);
                current.setExpirydate(expirydate);
                current.setItem_bill(Double.parseDouble(totalBill));
                current.setItem_bill(Double.parseDouble(totalBill));
            }
        }
        if (!hasItem) {
            invoiceItemModelList.add(new InvoiceItemModel(orgNo, Integer.parseInt(itemNo), item_id, Integer.parseInt(productQuantity),
                    Double.parseDouble(product_price), discount_percentage, expirydate, productName, Double.parseDouble(totalBill)));
        }
        if (invoiceItemModelList.size() > 0) {
            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
            relativeLayoutBottomSheetComponents.setVisibility(View.VISIBLE);
            linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);

            mAdapterHandler();
            totalBillHandler();
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && !continueScanning)
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            mScannerView.resumeCameraPreview(this);
        } else {
            Toast.makeText(getApplicationContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
/*            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && !continueScanning)
            {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }*/
            mScannerView.resumeCameraPreview(this);
        }


        //Product save via sqlite
/*        long id = dbHelper.mAddSelectedProduct(productID, productName, productQuantity, product_price, totalBill);
        if (id < 0) {
            Toast.makeText(getApplicationContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
*//*            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && !continueScanning)
            {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }*//*
            mScannerView.resumeCameraPreview(this);
        } else {
            Toast.makeText(getApplicationContext(), "Successfully added product into db", Toast.LENGTH_SHORT).show();
            mAdapterHandler();
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && !continueScanning)
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mScannerView.resumeCameraPreview(this);
        }*/
    }

    @Override
    public void dialogDismissHandler() {
        mScannerView.resumeCameraPreview(this);
    }

    private void mAdapterHandler() {
//        recyclerViewHandler();
        if (invoiceItemModelList.size() > 0) {
            rvAdapter_selectedProductView = new RvAdapterAddedItemViewSale(invoiceItemModelList, this, this);
            rv_selectedProduct.setAdapter(rvAdapter_selectedProductView);
            rvAdapter_selectedProductView.notifyDataSetChanged();
        } else
            textViewSubtotalScannerDisplay.setText("0.00");
//        setupFormats();
//        contentFrame.addView(mScannerView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtn_flash_off:
            case R.id.imgBtn_flash_on:
                mFlash = !mFlash;
                if (mFlash) {
                    imageButtonFlashOn.setVisibility(View.VISIBLE);
                    imageButtonFlashOff.setVisibility(View.GONE);
                } else {
                    imageButtonFlashOn.setVisibility(View.GONE);
                    imageButtonFlashOff.setVisibility(View.VISIBLE);
                }
                mScannerView.setFlash(mFlash);
                break;
            case R.id.imgBtn_focus_on:
            case R.id.imgBtn_focus_off:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    imageButtonFocusOn.setVisibility(View.VISIBLE);
                    imageButtonFocusOff.setVisibility(View.GONE);
                } else {
                    imageButtonFocusOn.setVisibility(View.GONE);
                    imageButtonFocusOff.setVisibility(View.VISIBLE);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                break;

            case R.id.l3:
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.imgBtn_personSearch:
                dialog = new Dialog(FullScannerActivitySale.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner_layout);

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
               /* wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 30;
                wmlp.y = 100;*/

                dialog.getWindow().setLayout(650, 800);
                wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                //                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.edtTxt_searchableText);
                RecyclerView recyclerViewPersonSearch = dialog.findViewById(R.id.lv_searchableCustomerView);
                RecyclerView recyclerViewFilteredPerson = dialog.findViewById(R.id.lv_filteredCustomerView);
                ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                recyclerViewPersonSearch.setHasFixedSize(true);
                recyclerViewFilteredPerson.setHasFixedSize(true);
                recyclerViewFilteredPerson.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewPersonSearch.setLayoutManager(new LinearLayoutManager(this));
                ImageButton imageButtonAddCustomer = dialog.findViewById(R.id.imgBtn_addCustomer);

                mGetCustomerList(recyclerViewPersonSearch, progressBar);

                recyclerViewPersonSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customerArrayList.size() - 1) {
                                //bottom of list!
                                customerArrayList.add(new Customer(null));
                                rvAdapterPersonSearch.notifyItemInserted(customerArrayList.size());
                                pageNo++;
                                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        customerArrayList.remove(customerArrayList.size() - 1);
                                        int scrollPosition = customerArrayList.size();
                                        rvAdapterPersonSearch.notifyItemRemoved(scrollPosition);
/*                                        int currentSize = scrollPosition + 1;
                                        int nextLimit = currentSize + 10;
                                        pageNo++;
                                        while (currentSize < nextLimit) {
                                            customer = new Customer("Item " + currentSize + ", PageNo: " + pageNo);
                                            customerArrayList.add(customer);
                                            currentSize++;
                                        }
                                        rvAdapterPersonSearch.notifyDataSetChanged();
                                        isLoading = false;*/

                                        HashMap<String, String> mapForScroll = new HashMap<>();
                                        mapForScroll.put(API_KEY, apikey);
                                        mapForScroll.put(CUSTOMER_PAGE_NO, String.valueOf(pageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_clients, new Response.Listener<String>() {


                                            @Override
                                            public void onResponse(String response) {
                                                try {

                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        if (jsonArray.length() > 0) {

                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject data = jsonArray.getJSONObject(i);
                                                                Customer customer = new Customer(data.getString("id"), data.getString("name"));
                                                                customerArrayList.add(customer);
                                                            }

                                                        } else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
                                                        rvAdapterPersonSearch.notifyDataSetChanged();
                                                    /*    rvAdapterPersonSearch = new RvAdapterPersonSearch(customerArrayList, FullScannerActivity.this);
                                                        recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                        isLoading = false;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(FullScannerActivitySale.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);

                                isLoading = true;
                            }
                        }
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filter arrayList
                        recyclerViewPersonSearch.setVisibility(View.VISIBLE);
                        recyclerViewFilteredPerson.setVisibility(View.GONE);
                    /*    ArrayList<Customer> filteredArray = new ArrayList<>();
                        filteredArray = filter(s.toString());*/


                        filterCustomers(s.toString());

                        if (s.length() >= 3) {
                            ArrayList<Customer> finalFilteredArray = new ArrayList<>();
                            mGetFilteredCustomerList(s, progressBar, recyclerViewFilteredPerson, recyclerViewPersonSearch, finalFilteredArray);

                            recyclerViewFilteredPerson.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading2) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == finalFilteredArray.size() - 1) {
                                            //bottom of list!
                                            finalFilteredArray.add(new Customer(null, null));
                                            rvAdapterPersonSearch.notifyItemInserted(finalFilteredArray.size());
                                            customerFilteredPageNo++;
                                            Toast.makeText(FullScannerActivitySale.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
                                                    rvAdapterPersonSearch.notifyItemRemoved(scrollPosition);
                                                    HashMap<String, String> mapForScroll = new HashMap<>();
                                                    mapForScroll.put(API_KEY, apikey);
                                                    mapForScroll.put(CUSTOMER_PAGE_NO, String.valueOf(customerFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(CUSTOMER_SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_clients, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject data = jsonArray.getJSONObject(i);
                                                                        Customer customer = new Customer(data.getString("id"), data.getString("name"));
                                                                        finalFilteredArray.add(customer);
                                                                    }
                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                } else
                                                                    Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
                                                                rvAdapterPersonSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                isLoading2 = false;

                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    }, mapForScroll);
                                                }
                                            }, 1500);
                                            isLoading2 = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetCustomerList(recyclerViewPersonSearch, progressBar);
                        } else
                            progressBar.setVisibility(View.GONE);

                       /* if (s.length() == 0)
                        {
                            pageNo = 1;
                        }*/

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
             /*   recyclerViewPersonSearch.(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);
                        textViewCustomerNameScannerDisplay.setVisibility(View.VISIBLE);
                        textViewCustomerName.setText(adapter.getItem(position));
                        textViewCustomerNameScannerDisplay.setText(adapter.getItem(position));
//                        Toast.makeText(FullScannerActivity.this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });*/

                imageButtonAddCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dialogAddCustomer = new Dialog(FullScannerActivitySale.this);
                        dialogAddCustomer.setContentView(R.layout.dialog_add_customer_layout);

                        WindowManager.LayoutParams wmlp = dialogAddCustomer.getWindow().getAttributes();
                      /*  wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                        wmlp.x = 30;
                        wmlp.y = 100;
*/
                        dialogAddCustomer.getWindow().setLayout(650, 700);
                        wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                        dialogAddCustomer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogAddCustomer.show();
                        dialogAddCustomer.setCancelable(true);

                        EditText editTextCustomerName = dialogAddCustomer.findViewById(R.id.edtTxt_customerName);
                        EditText editTextCustomerPhone = dialogAddCustomer.findViewById(R.id.edtTxt_customerPhoneNum);
                        Button buttonSaveCustomer = dialogAddCustomer.findViewById(R.id.btn_addCustomer);
                        ProgressBar progressBar1 = dialogAddCustomer.findViewById(R.id.progressbarAddCustomer);

                        buttonSaveCustomer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buttonSaveCustomer.setVisibility(View.GONE);
                                progressBar1.setVisibility(View.VISIBLE);
                                String customerName = editTextCustomerName.getText().toString();
                                String customerPhone = editTextCustomerPhone.getText().toString();
                                if (!TextUtils.isEmpty(customerName) && !TextUtils.isEmpty(customerPhone)) {

                                    HashMap<String, String> mapInsertCustomer = new HashMap<>();
                                    mapInsertCustomer.put(API_KEY, apikey);
                                    mapInsertCustomer.put(CUSTOMER_NAME, customerName);
                                    mapInsertCustomer.put(CUSTOMER_MOBILE_NO, customerPhone);
                                    mapInsertCustomer.put(TYPE, type_name);

                                    AppController.getAppController().getAppNetworkController().makeRequest(url_insert_a_customer, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {

                                                    String id = jsonObject.getString("id");
                                                    String name = jsonObject.getString("name");

                                                    customerClickHandler(id, name);

                                                    textViewCustomerName.setText(customerName);
                                                    linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);
                                                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                                                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                                    textViewCustomerNameScannerDisplay.setVisibility(View.VISIBLE);
                                                    textViewCustomerNameScannerDisplay.setText(customerName);
                                                    progressBar1.setVisibility(View.GONE);
                                                    buttonSaveCustomer.setVisibility(View.VISIBLE);

                                                    Toast.makeText(FullScannerActivitySale.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    dialogAddCustomer.dismiss();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            progressBar1.setVisibility(View.GONE);
                                            buttonSaveCustomer.setVisibility(View.VISIBLE);
                                        }
                                    }, mapInsertCustomer);

                                }
                            }
                        });
                    }
                });
                break;
            case R.id.imgBtn_prodcutSearch:
                dialogProductSearch = new Dialog(FullScannerActivitySale.this);
                dialogProductSearch.setContentView(R.layout.dialog_product_search_layout);

                WindowManager.LayoutParams wmlp1 = dialogProductSearch.getWindow().getAttributes();
                /*wmlp1.gravity = Gravity.BOTTOM | Gravity.LEFT;
                wmlp1.x = 30;
                wmlp1.y = 230;*/
                wmlp1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                dialogProductSearch.getWindow().setLayout(650, 800);
//                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialogProductSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogProductSearch.show();
                dialogProductSearch.setCancelable(true);

                EditText editTextProduct = dialogProductSearch.findViewById(R.id.edtTxt_productSearch);
                RecyclerView recyclerViewProductSearch = dialogProductSearch.findViewById(R.id.lv_searchableProductView);
                RecyclerView recyclerViewFilteredProduct = dialogProductSearch.findViewById(R.id.lv_filteredProductView);
                ProgressBar progressBar2 = dialogProductSearch.findViewById(R.id.progressBar);
                recyclerViewProductSearch.setHasFixedSize(true);
                recyclerViewFilteredProduct.setHasFixedSize(true);
                recyclerViewProductSearch.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewFilteredProduct.setLayoutManager(new LinearLayoutManager(this));

                mGetProductList(recyclerViewProductSearch, progressBar2);


                recyclerViewProductSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading3) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsArrayList.size() - 1) {
                                //bottom of list! and start loading
                                productsArrayList.add(new Products(null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterProductSearch.notifyItemInserted(productsArrayList.size());
                                productPageNo++;
                                Toast.makeText(FullScannerActivitySale.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        productsArrayList.remove(productsArrayList.size() - 1);
                                        int scrollPosition = productsArrayList.size();
                                        rvAdapterProductSearch.notifyItemRemoved(scrollPosition);
/*                                        int currentSize = scrollPosition + 1;
                                        int nextLimit = currentSize + 10;
                                        pageNo++;
                                        while (currentSize < nextLimit) {
                                            customer = new Customer("Item " + currentSize + ", PageNo: " + pageNo);
                                            customerArrayList.add(customer);
                                            currentSize++;
                                        }
                                        rvAdapterPersonSearch.notifyDataSetChanged();
                                        isLoading = false;*/

                                        HashMap<String, String> mapForScroll = new HashMap<>();
                                        mapForScroll.put(API_KEY, apikey);
                                        mapForScroll.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_get_org_items_by_name, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {


                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        if (jsonArray.length() > 0) {
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject productData = jsonArray.getJSONObject(i);
                                                                products = new Products(productData.getString("id"), productData.getString("text"));
                                                                productsArrayList.add(products);
                                                            }
                                                        }/* else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/
                                                        rvAdapterProductSearch.notifyDataSetChanged();
                                                        isLoading3 = false;
                                                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                        progressBar2.setVisibility(View.GONE);
                                                        Toast.makeText(FullScannerActivitySale.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);
                                isLoading3 = true;
                            }
                        }
                    }
                });

                editTextProduct.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filter arrayList
                        recyclerViewProductSearch.setVisibility(View.VISIBLE);
                        recyclerViewFilteredProduct.setVisibility(View.GONE);

                        filterProducts(s.toString());

                        if (s.length() >= 3) {
                            ArrayList<Products> finalFilteredArray = new ArrayList<>();
                            mGetFilteredProductList(s, progressBar2, recyclerViewFilteredProduct, recyclerViewProductSearch, finalFilteredArray);

                            recyclerViewFilteredProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading4) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsArrayList.size() - 1) {
                                            //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            finalFilteredArray.add(new Products(null, null));
                                            rvAdapterProductSearch.notifyItemInserted(finalFilteredArray.size());
                                            productFilteredPageNo++;
                                            Toast.makeText(FullScannerActivitySale.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
                                                    rvAdapterProductSearch.notifyItemRemoved(scrollPosition);
/*
                                        int currentSize = scrollPosition + 1;
                                        int nextLimit = currentSize + 10;
                                        pageNo++;
                                        while (currentSize < nextLimit) {
                                            customer = new Customer("Item " + currentSize + ", PageNo: " + pageNo);
                                            customerArrayList.add(customer);
                                            currentSize++;
                                        }
                                        rvAdapterPersonSearch.notifyDataSetChanged();
                                        isLoading = false;
*/
                                                    HashMap<String, String> mapForScroll = new HashMap<>();
                                                    mapForScroll.put(API_KEY, apikey);
                                                    mapForScroll.put(PRODUCT_PAGE_NO, String.valueOf(productFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(PRODUCT_SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_get_org_items_by_name, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject productData = jsonArray.getJSONObject(i);
                                                                        Products products = new Products(productData.getString("id"), productData.getString("text"));
                                                                        finalFilteredArray.add(products);
                                                                    }
                                                                    rvAdapterProductSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading4 = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                }/* else
                                                                    Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/ else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                                    progressBar2.setVisibility(View.GONE);
                                                                    Toast.makeText(FullScannerActivitySale.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                        }
                                                    }, mapForScroll);
                                                }
                                            }, 1500);
                                            isLoading4 = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetProductList(recyclerViewProductSearch, progressBar2);
                        } else
                            progressBar2.setVisibility(View.GONE);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.btn_discountIncrease:
                String getDisc = editText_discount.getText().toString();
                int selectedDiscountRadioBtn = radioGroupDiscount.getCheckedRadioButtonId();
                discountRadioButton = findViewById(selectedDiscountRadioBtn);

                if (!TextUtils.isEmpty(getDisc)) {
                    double incDisc = Double.parseDouble(getDisc);
                    incDisc = incDisc + 1.00;
                    if (incDisc <= subTotal) {
//                        editText_discount.setText(String.format("%.2f", incDisc));
                        deduction = 0.00;
                        editText_deduction.setText(String.format("%.2f", deduction));
                        editText_discount.setText(String.format("%.2f", incDisc));
                    }
                } else {
//                    editText_discount.setText("1.00");
                    double incDisc = 0.00;
                    incDisc = incDisc + 1.00;
                    if (incDisc <= subTotal) {
//                        editText_discount.setText(String.format("%.2f", incDisc));
                        deduction = 0.00;
                        editText_deduction.setText(String.format("%.2f", deduction));
                        editText_discount.setText(String.format("%.2f", incDisc));
                    }
                }
                break;
            case R.id.btn_discountDecrease:
                String getDisc2 = editText_discount.getText().toString();

                if (!TextUtils.isEmpty(getDisc2)) {
                    double decDisc = Double.parseDouble(getDisc2);
                    decDisc = decDisc - 1.00;
                    if (decDisc > 0) {
                        deduction = 0.00;
                        editText_deduction.setText(String.format("%.2f", deduction));
                        editText_discount.setText(String.format("%.2f", decDisc));
//                        editText_discount.setText(String.format("%.2f", decDisc));
                    }
                }
                break;

            /**
             * with payment option
             */
            case R.id.btn_cashPaid:
                JSONArray jsonArrayCashPaid = new JSONArray();
                JSONObject jsonObjectCashPaid = new JSONObject();

                try {
                    jsonObjectCashPaid.put("accno", 1120);
                    jsonObjectCashPaid.put("amount", mGetTotal());

                    jsonArrayCashPaid.put(jsonObjectCashPaid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                invoiceSave(jsonArrayCashPaid);

                break;


            case R.id.btn_bankPaid:
                JSONArray jsonArrayBankPaid = new JSONArray();
                JSONObject jsonObjectBankPaid = new JSONObject();

                try {
                    jsonObjectBankPaid.put("accno", 1110);
                    jsonObjectBankPaid.put("amount", mGetTotal());

                    jsonArrayBankPaid.put(jsonObjectBankPaid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                invoiceSave(jsonArrayBankPaid);
                break;

            case R.id.btn_bkashPaid:
                JSONArray jsonArrayBKashPaid = new JSONArray();
                JSONObject jsonObjectBKashPaid = new JSONObject();
                try {
                    jsonObjectBKashPaid.put("accno", 1160);
                    jsonObjectBKashPaid.put("amount", mGetTotal());

                    jsonArrayBKashPaid.put(jsonObjectBKashPaid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                invoiceSave(jsonArrayBKashPaid);
                break;

            case R.id.ll_discardInvoice:
              /*  dialog = new Dialog(FullScannerActivitySale.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner_layout);

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 30;
                wmlp.y = 100;

                dialog.getWindow().setLayout(650, 800);
                //                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();*/

                builder.setMessage("Optional message").setTitle("Optional title");

                builder.setMessage("Do you want to discard this invoice?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textViewSubtotalScannerDisplay.setText("0.00");
                                textViewCustomerNameScannerDisplay.setText("");
                                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                                textViewCustomerName.setText("");
                                linearLayoutBottomSheetCustomerName.setVisibility(View.GONE);
                                relativeLayoutBottomSheetComponents.setVisibility(View.GONE);
                                textViewCartBadge.setText("0");
                                editText_deduction.setText("0.00");
                                editText_discount.setText("0.00");
                                invoiceItemModelList.clear();
                                total = 0.00;
                                defaultClientSelection();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Alert!!");
                alertDialog.show();
                break;

            case R.id.tv_adjustFraction:

                String tempTotal = textView_total.getText().toString();
                double fractionalValue = (subTotal - discount) % 1;
                total = Double.parseDouble(tempTotal) - fractionalValue;
                deduction = fractionalValue;

                editText_deduction.setText(String.format("%.2f", deduction));
                textView_total.setText(String.format("%.2f", total));

                break;
        }
    }

    private double mGetTotal() {
        if (deduction == 0 && discount == 0) {
            return subTotal;
        } else
            return total;
    }

    private void mGetFilteredProductList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredProduct, RecyclerView recyclerViewProductSearch, ArrayList<Products> finalFilteredArray) {
        productFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(PRODUCT_SEARCH_KEY, s.toString());
        mapGetProducts.put(API_KEY, apikey);
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_org_items_by_name, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray productDataArray = jsonObject.getJSONArray("data");
//                                            customerArrayList = new ArrayList<>();

                        for (int i = 0; i < productDataArray.length(); i++) {
                            progressBar2.setVisibility(View.GONE);
                            recyclerViewFilteredProduct.setVisibility(View.VISIBLE);
                            recyclerViewProductSearch.setVisibility(View.GONE);
                            JSONObject productData = productDataArray.getJSONObject(i);
                            products = new Products(productData.getString("id"), productData.getString("text"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            finalFilteredArray.add(products);
                            rvAdapterProductSearch = new RvAdapterProductSearch(finalFilteredArray, FullScannerActivitySale.this, productPageNo);
                            recyclerViewFilteredProduct.setAdapter(rvAdapterProductSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(FullScannerActivitySale.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullScannerActivitySale.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
    }

    private void mGetProductList(RecyclerView recyclerViewProductSearch, ProgressBar progressBar2) {
        productPageNo = 1;
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
//        Toast.makeText(this, mapGetProducts.toString(), Toast.LENGTH_SHORT).show();
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_org_items_by_name, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        productsArrayList = new ArrayList<>();

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            progressBar2.setVisibility(View.GONE);
                            JSONObject productData = customerDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            products = new Products(productData.getString("id"), productData.getString("text"));
                            productsArrayList.add(products);
                            rvAdapterProductSearch = new RvAdapterProductSearch(productsArrayList, FullScannerActivitySale.this, productPageNo);
                            recyclerViewProductSearch.setAdapter(rvAdapterProductSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                    dialogProductSearch.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
                dialogProductSearch.dismiss();
            }
        }, mapGetProducts);
    }

    private void mGetFilteredCustomerList(CharSequence s, ProgressBar progressBar, RecyclerView recyclerViewFilteredPerson, RecyclerView recyclerViewPersonSearch, ArrayList<Customer> finalFilteredArray) {
        customerFilteredPageNo = 1;
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, String> mapGetClients = new HashMap<>();
        mapGetClients.put(CUSTOMER_PAGE_NO, String.valueOf(customerFilteredPageNo));
        mapGetClients.put(LIMIT, String.valueOf(10));
        mapGetClients.put(CUSTOMER_SEARCH_KEY, s.toString());
        mapGetClients.put(API_KEY, apikey);
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_clients, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
//                                            customerArrayList = new ArrayList<>();

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            progressBar.setVisibility(View.GONE);
                            recyclerViewFilteredPerson.setVisibility(View.VISIBLE);
                            recyclerViewPersonSearch.setVisibility(View.GONE);
                            JSONObject customerData = customerDataArray.getJSONObject(i);
                            customer = new Customer(customerData.getString("id"), customerData.getString("name"));
                            finalFilteredArray.add(customer);
                            rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivitySale.this);
                            recyclerViewFilteredPerson.setAdapter(rvAdapterPersonSearch);

                        }
//                                            rvAdapterPersonSearch.notifyDataSetChanged();
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(FullScannerActivitySale.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, mapGetClients);
    }

    private void mGetCustomerList(RecyclerView recyclerViewPersonSearch, ProgressBar progressBar) {
        pageNo = 1;
        HashMap<String, String> mapGetClients = new HashMap<>();
        mapGetClients.put(CUSTOMER_PAGE_NO, String.valueOf(pageNo));
        mapGetClients.put(LIMIT, String.valueOf(10));
        mapGetClients.put(API_KEY, apikey);
        mapGetClients.put(TYPE, type_name);

        AppController.getAppController().getAppNetworkController().makeRequest(url_get_filtered_clients, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        customerArrayList = new ArrayList<>();

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            progressBar.setVisibility(View.GONE);
                            JSONObject customerData = customerDataArray.getJSONObject(i);
                            customer = new Customer(customerData.getString("id"), customerData.getString("name"));
                            customerArrayList.add(customer);
                            rvAdapterPersonSearch = new RvAdapterPersonSearch(customerArrayList, FullScannerActivitySale.this);
                            recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }, mapGetClients);
    }

    private void invoiceSave(JSONArray jsonArrayPayments) {
        String getCustomerName = textViewCustomerName.getText().toString();
        if (!TextUtils.isEmpty(getCustomerName)) {
            JSONArray jsonArrayInvoiceItem = new JSONArray();
            for (int i = 0; i < invoiceItemModelList.size(); i++) {
                InvoiceItemModel current = invoiceItemModelList.get(i);
                JSONObject jsonObjectTemp = new JSONObject();

                JSONArray skuJsonArray = new JSONArray();
                for (int j = 0; j < current.getItem_id().size(); j++) {
                    try {
                        skuJsonArray.put(current.getItem_id().get(j));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                System.out.println(Arrays.toString(words));
                try {
                    jsonObjectTemp.put("itemno", current.getItemno());
                    jsonObjectTemp.put("item_id", skuJsonArray);
                    jsonObjectTemp.put("expirydate", current.getExpirydate());
                    jsonObjectTemp.put("unitid", current.getSelling_unitid());
                    jsonObjectTemp.put("qty", current.getQty());
                    jsonObjectTemp.put("unitprice", current.getRate());
                    jsonObjectTemp.put("taxrate", current.getTaxrate());
                    jsonObjectTemp.put("discount_percentage", current.getDiscount_percentage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArrayInvoiceItem.put(jsonObjectTemp);
            }
            JSONObject jsonObjectInvoiceItem = new JSONObject();
            try {
                jsonObjectInvoiceItem.put("customerno", customer_no);
                jsonObjectInvoiceItem.put("sdate", currentDate);
                jsonObjectInvoiceItem.put("duedate", currentDate);
                jsonObjectInvoiceItem.put("currency", 1);
                jsonObjectInvoiceItem.put("discount", discount);
                jsonObjectInvoiceItem.put("deduction", deduction);
                jsonObjectInvoiceItem.put("items", jsonArrayInvoiceItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, jsonArrayInvoiceItem.toString(), Toast.LENGTH_LONG).show();
            HashMap<String, String> mapTemp = new HashMap<>();
            mapTemp.put(API_KEY, apikey);
            mapTemp.put("invoice", jsonObjectInvoiceItem.toString());
            mapTemp.put("status", "1");
            mapTemp.put("payments", jsonArrayPayments.toString());
            Log.d("InvoiceSave", "invoiceSave: "+mapTemp.toString());
//            Toast.makeText(this, mapTemp.toString(), Toast.LENGTH_LONG).show();
//            mGenerateMapTextFile(mapTemp);
            AppController.getAppController().getAppNetworkController().makeRequest(url_insert_a_saleinvoice, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject temp = new JSONObject(response);

                        if (temp.getString("error").equalsIgnoreCase("false")) {
                            Toast.makeText(FullScannerActivitySale.this, temp.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FullScannerActivitySale.this, SaleInvoiceViewerActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            textViewSubtotalScannerDisplay.setText("0.00");
                            textViewCustomerNameScannerDisplay.setText("");
                            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                            textViewCustomerName.setText("");
                            linearLayoutBottomSheetCustomerName.setVisibility(View.GONE);
                            relativeLayoutBottomSheetComponents.setVisibility(View.GONE);
                            textViewCartBadge.setText("0");
                            editText_deduction.setText("0.00");
                            editText_discount.setText("0.00");
                            invoiceItemModelList.clear();
                        } else if (temp.getString("error").equalsIgnoreCase("true")) {
                            Toast.makeText(FullScannerActivitySale.this, temp.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(FullScannerActivitySale.this, "JSONexception: " + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(FullScannerActivitySale.this, "ErrorListener: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }, mapTemp);
        } else
            Toast.makeText(this, "Please, select a customer", Toast.LENGTH_SHORT).show();
    }

    private void mGenerateMapTextFile(HashMap<String, String> mapTemp) {
        try {
            FileOutputStream fileout = openFileOutput("mytextfile.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(mapTemp.toString());
            outputWriter.close();
            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        File file = new File(outputFilePath);
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, String> entry :
                    mapTemp.entrySet()) {

                // put key and value separated by a colon
                bufferedWriter.write(entry.getValue());

                // new line
                bufferedWriter.newLine();
            }

            bufferedWriter.flush();

        } catch (IOException e) {
            Toast.makeText(this, "BufferWriterError: "+e.toString(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                Objects.requireNonNull(bufferedWriter).flush();
            } catch (IOException e) {
                Toast.makeText(this, "Error in finally: "+e.toString() , Toast.LENGTH_SHORT).show();
            }
        }

    */
    }

/*
    private void loadMore(ArrayList<Customer> rowsArrayList, RvAdapterPersonSearch rvAdapterPersonSearch) {
        rowsArrayList.add(null);
        Toast.makeText(FullScannerActivity.this, "in loadMore: " + String.valueOf(rowsArrayList.size()), Toast.LENGTH_SHORT).show();

        rvAdapterPersonSearch.notifyItemInserted(rowsArrayList.size());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rowsArrayList.remove(rowsArrayList.size() - 1);
                int scrollPosition = rowsArrayList.size();
                rvAdapterPersonSearch.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition + 1;
                int nextLimit = currentSize + 10;
                pageNo++;
                while (currentSize < nextLimit) {
                    customer = new Customer("Item " + currentSize + ", PageNo: " + pageNo);
                    rowsArrayList.add(customer);
                    currentSize++;
                }

                rvAdapterPersonSearch.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }
*/

    public void onRadioBtnDiscount(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioBtn_tk:
                if (checked) {
                    String tempStr = editText_discount.getText().toString().trim();
                    if (!TextUtils.isEmpty(tempStr)) {
                        discount = Double.parseDouble(tempStr);
                        if (discount <= subTotal) {
                            double temp = subTotal - (discount + deduction);
                            total = temp;
                            textView_total.setText(String.format("%.2f", temp));
                            textViewDiscountOverAll.setText(String.format("%.2f", discount));
                        } else {
                            textView_total.setText("0.00");
                            textViewDiscountOverAll.setText(String.format("%.2f", discount));
                        }
                    }

                }
                break;

            case R.id.radioBtn_percent:
                if (checked) {
                    String tempStr = editText_discount.getText().toString().trim();
                    if (!TextUtils.isEmpty(tempStr)) {
                        double tempDisc = Double.parseDouble(tempStr);
                        if (tempDisc <= 100) {
                            discount = (tempDisc * subTotal) / 100;
                            double temp = subTotal - (discount + deduction);
                            total = temp;
                            textView_total.setText(String.format("%.2f", temp));
                            textViewDiscountOverAll.setText(String.format("%.2f", discount));
                        } else {
                            discount = (tempDisc * subTotal) / 100;
                            textView_total.setText("0.00");
                            textViewDiscountOverAll.setText(String.format("%.2f", discount));
                        }
                    }

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        Intent mainActivity = new Intent(this, MainActivity.class);
//        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainActivity);

        ScanerDilogFragmentActivitySale scanerDilogFragmentActivitySale = (ScanerDilogFragmentActivitySale) getSupportFragmentManager().findFragmentByTag("scannerDialogFragmentActivity");
        if (scanerDilogFragmentActivitySale != null && scanerDilogFragmentActivitySale.isVisible()) {
            scanerDilogFragmentActivitySale.dismiss();
            finish();
        } else
            super.onBackPressed();

    }

    @Override
    public void mCallbackQty() {
        mTest();
    }

    private void mTest() {
        mAdapterHandler();
        totalBillHandler();
    }

    private ArrayList<Customer> filterCustomers(String text) {
        ArrayList<Customer> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (Customer item : customerArrayList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterPersonSearch.filterList(filteredList);
        return filteredList;
    }

    private ArrayList<Products> filterProducts(String text) {
        ArrayList<Products> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (Products item : productsArrayList) {
            if (item.getItem_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterProductSearch.filterList(filteredList);
        return filteredList;
    }

    @Override
    public void customerClickHandler(String id, String name) {
        customer_no = id;
        linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);
        textViewCustomerNameScannerDisplay.setVisibility(View.VISIBLE);
        textViewCustomerName.setText(name);
        textViewCustomerNameScannerDisplay.setText(name);
//       Toast.makeText(FullScannerActivity.this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void productClickHandler(String name, String id) {
        HashMap<String, String> mapGetProductById = new HashMap<>();
        mapGetProductById.put(API_KEY, apikey);
        mapGetProductById.put("itemno", id);
//        mapGetProductById.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
        AppController.getAppController().getAppNetworkController().makeRequest(url_get_details_of_item, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(FullScannerActivitySale.this, response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
//                        assert arrayListSku == null;
                        lotsModelArrayList = new ArrayList<>();
                        JSONObject data = jsonObject.getJSONObject("data");
                        String itemNo = data.getString("itemno");
                        int itemOrgno = data.getInt("orgno");
                        String product_name = data.getString("itemname");
                        String productRate = data.getString("salerate");
//                        String item_id = data.getString("upc");
                        String expirydate = data.getString("hasexpiry");
                        String discount_percentage = data.getString("discount_rate");
                        String unitid = data.getString("unitid");
                        JSONArray jsonArraySku = data.getJSONArray("lots");

                        for (int i = 0; i < jsonArraySku.length(); i++) {
                            JSONObject object = jsonArraySku.getJSONObject(i);
                            if (object.getInt("qty") > 0) {

                                int itemno = object.getInt("itemno");
                                int orgno = object.getInt("orgno");
                                int lotno = object.getInt("lotno");
                                String sku = object.getString("sku");
                                int qty = object.getInt("qty");
                                String expirydateOfLots = object.getString("expirydate");
                                lotsModelArrayList.add(new LotsModel(itemno, orgno, lotno, sku, qty, expirydateOfLots, false, false, 0));
                            }


                        }
//                        showMessageDialog(id, name, saleRate);
                        showMessageDialog(itemOrgno, itemNo, product_name, productRate, unitid, expirydate, discount_percentage, lotsModelArrayList);

                    }
                } catch (JSONException e) {
                    Toast.makeText(FullScannerActivitySale.this, "JsonException: " + e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivitySale.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProductById);


        dialogProductSearch.dismiss();
    }

    @Override
    public void catClickHandler(String id, String name) {}

    @Override
    public void dataParsedStockLots(String name, ArrayList<LotsModel> lotsModelArrayList) {

    }

    @Override
    public void handleBottomSheet() {

    }
}