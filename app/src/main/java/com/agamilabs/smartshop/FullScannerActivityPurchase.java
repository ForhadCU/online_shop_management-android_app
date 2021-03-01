package com.agamilabs.smartshop;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromFullScannerActivity;
import com.agamilabs.smartshop.Interfaces.ICallbackCustomerSearchClickHandler;
import com.agamilabs.smartshop.Interfaces.ProductDetailsInterface;
import com.agamilabs.smartshop.adapter.RvAdapterPersonSearch;
import com.agamilabs.smartshop.adapter.RvAdapterProductSearch;
import com.agamilabs.smartshop.adapter.RvAdapter_selectedProductView;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.database.DbHelper;
import com.agamilabs.smartshop.model.Customer;
import com.agamilabs.smartshop.model.InvoiceItem;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.agamilabs.smartshop.model.Products;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FullScannerActivityPurchase extends BaseScannerActivity implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener, ProductDetailsInterface, View.OnClickListener, ICallBackFromFullScannerActivity, ICallbackCustomerSearchClickHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private RecyclerView rv_selectedProduct;
    private RvAdapter_selectedProductView rvAdapter_selectedProductView;
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
    private RelativeLayout relativeLayoutBottomSheetComponents;
    private LinearLayout linearLayoutBottomSheetCustomerName;
    private TextView textViewCartBadge;
    private RadioGroup radioGroupDiscount;
    private RadioButton discountRadioButton;
    private ProgressBar progressBarSkuRequest;

    private DbHelper dbHelper;
    private List<InvoiceModel> invoiceModelList;
    private ArrayList<InvoiceItem> invoiceItemList;
    private ArrayList<Customer> customerArrayList;
    private ArrayList<Products> productsArrayList;
    //    private ArrayList<String> productArrayList;
    private ArrayList<String> userList;
    private InvoiceModel invoiceModel;
    private RvAdapterPersonSearch rvAdapterPersonSearch;
    private RvAdapterProductSearch rvAdapterProductSearch;

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
    ICallBackFromFullScannerActivity icallBackTest;

    private String url_1 = "http://pharmacy.egkroy.com/app-module/php/get_details_of_item_of_sku.php";
    private String url_2 = "http://pharmacy.egkroy.com/app-module/php/get_org_items_by_name.php";
    private String url_3 = " http://pharmacy.egkroy.com/app-module/php/get_filtered_clients.php";
    private String url_4 = "http://pharmacy.egkroy.com/app-module/php/insert_a_customer.php";
    private String url_5 = "http://pharmacy.egkroy.com/app-module/php/insert_a_purchaseinvoice.php";
    private static final String API_KEY = "apikey";
    private static final String PRODUCT_SEARCH_KEY = "search_key";
    private static final String PRODUCT_PAGE_NO = "page";
    private static final String LIMIT = "limit";
    private static final String CUSTOMER_PAGE_NO = "pageno";
    private static final String CUSTOMER_NAME = "name";
    private static final String CUSTOMER_MOBILE_NO = "mobileno";
    private static final String CUSTOMER_TYPE = "type";
    private static final String CUSTOMER_SEARCH_KEY = "searchkey";

    private String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private String customer_no;
    private String currentDate;

//    private int pageno = 1;


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

        setContentView(R.layout.activity_simple_scanner_purchase);
//        setupToolbar();
//        Objects.requireNonNull(getSupportActionBar()).hide();

        /**
         * pick current date
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(new Date());

        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        bottomsSheetLayout = findViewById(R.id.bottom_sheet_purchase);
        sheetBehavior = BottomSheetBehavior.from(bottomsSheetLayout);
        sheetBehaviorHandler();

        invoiceItemList = new ArrayList<>();
        mScannerView = new ZXingScannerView(this);

        rv_selectedProduct = findViewById(R.id.rv_productView);
        textView_subTotal = findViewById(R.id.tv_subTotal);
        textView_total = findViewById(R.id.tv_Total);
        textViewSubtotalScannerDisplay = findViewById(R.id.tv_subTotalScannerDisplay);
        textViewCustomerNameScannerDisplay = findViewById(R.id.tv_cusNameScannerDisplay);
        textViewDiscountOverAll = findViewById(R.id.tv_discountOverAll);
        editText_discount = findViewById(R.id.edtTxt_discount);
        editText_deduction = findViewById(R.id.edtTxt_deduction);
        buttonSaveInvoice = findViewById(R.id.btn_invoiceSave);
        imageButtonFlashOn = findViewById(R.id.imgBtn_flash_on);
        imageButtonFlashOff = findViewById(R.id.imgBtn_flash_off);
        imageButtonFocusOn = findViewById(R.id.imgBtn_focus_on);
        imageButtonFocusOff = findViewById(R.id.imgBtn_focus_off);
        imageButtonPersonSearch = findViewById(R.id.imgBtn_personSearch);
        imageButtonProductSearch = findViewById(R.id.imgBtn_prodcutSearch);
        imageButtonDiscountIncrease = findViewById(R.id.btn_discountIncrease);
        imageButtonDiscountDecrease = findViewById(R.id.btn_discountDecrease);
        textViewCustomerName = findViewById(R.id.tv_bottomSheetCustomerName);
        relativeLayoutBottomSheetComponents = findViewById(R.id.l_bottomSheet_components);
        linearLayoutBottomSheetCustomerName = findViewById(R.id.l_bottomSheet_customerName);
        textViewCartBadge = findViewById(R.id.tv_cartBadge);
        radioGroupDiscount = findViewById(R.id.radioGroupDiscount);
        progressBarSkuRequest = findViewById(R.id.progress_skuRequest);


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
        buttonSaveInvoice.setOnClickListener(this);
//        totalBillHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void totalBillHandler() {
        subTotal = 0;
        textViewCartBadge.setText(String.valueOf(invoiceItemList.size()));
        if (invoiceItemList.size() == 0 && relativeLayoutBottomSheetComponents.getVisibility() == View.VISIBLE) {
            relativeLayoutBottomSheetComponents.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < invoiceItemList.size(); i++) {
                InvoiceItem current = invoiceItemList.get(i);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dbHelper.mDeleteProductList();
        invoiceItemList.clear();
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
        progressBarSkuRequest.setVisibility(View.VISIBLE);
        mSendVolleyRequest(rawResult);
    }

    private void mSendVolleyRequest(Result rawResult) {
        HashMap<String, String> map = new HashMap<>();
        map.put(API_KEY, apikey);
        map.put("sku", "10258");

        AppController.getAppController().getAppNetworkController().makeRequest(url_1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    Toast.makeText(FullScannerActivity.this, response, Toast.LENGTH_SHORT).show();

                    JSONObject object = new JSONObject(response);
                    if (object.getString("error").equalsIgnoreCase("false")) {
                        progressBarSkuRequest.setVisibility(View.GONE);
                        JSONObject data = object.getJSONObject("data");

                        String itemNo = data.getString("itemno");
                        String product_name = data.getString("itemname");
                        String productRate = data.getString("salerate");
                        String item_id = data.getString("upc");
                        String expirydate = data.getString("hasexpiry");
                        String discount_percentage = data.getString("discount_rate");
                        String unitid = data.getString("unitid");

/* showMessageDialog("Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat().toString(),
                                itemNo, product_name, productRate, item_id, unitid, expirydate, discount_percentage);*/

                        showMessageDialog(itemNo, item_id, product_name, productRate, unitid, expirydate, discount_percentage);
                    } else {
                        Toast.makeText(FullScannerActivityPurchase.this, "response error True", Toast.LENGTH_SHORT).show();
                    }
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, map);

    }

    public void showMessageDialog(String itemNo, String item_id, String product_name, String productRate, String unitid, String expirydate, String discount_percentage) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        ScanerDilogFragmentActivity scanerDilogFragmentActivity = new ScanerDilogFragmentActivity(itemNo, item_id, product_name, productRate, unitid, expirydate, discount_percentage);
        scanerDilogFragmentActivity.show(fragmentManager, "scannerDialogFragmentActivity");

/*        dialog_cross = new Dialog(FullScannerActivity.this);
        dialog_cross.setContentView(R.layout.scanner_dialog_frag_cross);
        dialog_cross.getWindow().setLayout(150, 150);
//        dialog_cross.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_cross.getWindow().setGravity(Gravity.BOTTOM);
        dialog_cross.show();
        dialog_cross.setCancelable(false);
        ImageButton imageButton_cross = dialog_cross.findViewById(R.id.imgBtn_scannerDialogCross);
        imageButton_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_cross.dismiss();
                scanerDilogFragmentActivity.dismiss();
                dialogDismissHandler();
            }
        });*/

    }

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
    public void dataParsingMethod(boolean continueScanning, String productID, String productName, String productQuantity, String product_price, String totalBill, String item_id, String unitid, String expirydate, String discount_percentage) {
        //product save via Model
        invoiceItemList.add(new InvoiceItem(productName, productID, item_id, expirydate, unitid, Double.parseDouble(product_price), null, discount_percentage, Double.parseDouble(productQuantity), Double.parseDouble(totalBill)));

        if (invoiceItemList.size() > 0) {
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
        if (invoiceItemList.size() > 0) {
            rvAdapter_selectedProductView = new RvAdapter_selectedProductView(invoiceItemList, this, this);
            rv_selectedProduct.setAdapter(rvAdapter_selectedProductView);
            rvAdapter_selectedProductView.notifyDataSetChanged();

        }
//        setupFormats();
//        contentFrame.addView(mScannerView);
    }

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

            case R.id.imgBtn_personSearch:
                dialog = new Dialog(FullScannerActivityPurchase.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner_layout);

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 30;
                wmlp.y = 100;

                dialog.getWindow().setLayout(650, 800);
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

                pageNo = 1;
                HashMap<String, String> mapGetClients = new HashMap<>();
                mapGetClients.put(CUSTOMER_PAGE_NO, String.valueOf(pageNo));
//                mapGetClients.put(LIMIT, String.valueOf(10));
                mapGetClients.put(API_KEY, apikey);
                AppController.getAppController().getAppNetworkController().makeRequest(url_3, new Response.Listener<String>() {
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
                                    rvAdapterPersonSearch = new RvAdapterPersonSearch(customerArrayList, FullScannerActivityPurchase.this);
                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);
                                }
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
                                customerArrayList.add(new Customer(null,null));
                                rvAdapterPersonSearch.notifyItemInserted(customerArrayList.size());
                                pageNo++;
                                Toast.makeText(FullScannerActivityPurchase.this, "Please wait...", Toast.LENGTH_SHORT).show();

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


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_3, new Response.Listener<String>() {
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
                                                            Toast.makeText(FullScannerActivityPurchase.this, "No more data to load", Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(FullScannerActivityPurchase.this, "Loaded", Toast.LENGTH_SHORT).show();
                                                        rvAdapterPersonSearch.notifyDataSetChanged();
                                                    /*    rvAdapterPersonSearch = new RvAdapterPersonSearch(customerArrayList, FullScannerActivity.this);
                                                        recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                        isLoading = false;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(FullScannerActivityPurchase.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 4000);
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
                        customerFilteredPageNo = 1;

                        if (s.length() >= 3) {
                            progressBar.setVisibility(View.VISIBLE);
                            ArrayList<Customer> finalFilteredArray = new ArrayList<>();
                            HashMap<String, String> mapGetClients = new HashMap<>();
                            mapGetClients.put(CUSTOMER_PAGE_NO, String.valueOf(customerFilteredPageNo));
                            mapGetClients.put(LIMIT, String.valueOf(10));
                            mapGetClients.put(CUSTOMER_SEARCH_KEY, s.toString());
                            mapGetClients.put(API_KEY, apikey);
                            AppController.getAppController().getAppNetworkController().makeRequest(url_3, new Response.Listener<String>() {
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
                                                RvAdapterPersonSearch adapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivityPurchase.this);
                                                recyclerViewFilteredPerson.setAdapter(adapterPersonSearch);

                                            }
//                                            rvAdapterPersonSearch.notifyDataSetChanged();
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


                            recyclerViewPersonSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading2) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == customerArrayList.size() - 1) {
                                            //bottom of list!
                                            finalFilteredArray.add(new Customer(null, null));
                                            rvAdapterPersonSearch.notifyItemInserted(customerArrayList.size());
                                            customerFilteredPageNo++;
                                            Toast.makeText(FullScannerActivityPurchase.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
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
                                                    mapForScroll.put(CUSTOMER_PAGE_NO, String.valueOf(customerFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(CUSTOMER_SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_3, new Response.Listener<String>() {
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
                                                                    rvAdapterPersonSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading2 = false;
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
                                            }, 4000);
                                            isLoading2 = true;
                                        }
                                    }
                                }
                            });

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
                        dialogAddCustomer = new Dialog(FullScannerActivityPurchase.this);
                        dialogAddCustomer.setContentView(R.layout.dialog_add_customer_layout);

                        WindowManager.LayoutParams wmlp = dialogAddCustomer.getWindow().getAttributes();
                        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                        wmlp.x = 30;
                        wmlp.y = 100;

                        dialogAddCustomer.getWindow().setLayout(650, 700);
                        dialogAddCustomer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogAddCustomer.show();
//                        dialogAddCustomer.setCancelable(false);

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

                                    AppController.getAppController().getAppNetworkController().makeRequest(url_4, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                textViewCustomerName.setText(customerName);
                                                linearLayoutBottomSheetCustomerName.setVisibility(View.VISIBLE);
                                                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                                                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                                JSONObject jsonObject = new JSONObject(response);

                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                    textViewCustomerNameScannerDisplay.setVisibility(View.VISIBLE);
                                                    textViewCustomerNameScannerDisplay.setText(customerName);
                                                    Toast.makeText(FullScannerActivityPurchase.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    progressBar1.setVisibility(View.GONE);
                                                    buttonSaveCustomer.setVisibility(View.VISIBLE);
                                                    dialogAddCustomer.dismiss();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                dialogProductSearch = new Dialog(FullScannerActivityPurchase.this);
                dialogProductSearch.setContentView(R.layout.dialog_product_search_layout);

                WindowManager.LayoutParams wmlp1 = dialogProductSearch.getWindow().getAttributes();
                wmlp1.gravity = Gravity.BOTTOM | Gravity.LEFT;
                wmlp1.x = 30;
                wmlp1.y = 230;

                dialogProductSearch.getWindow().setLayout(650, 800);
//                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialogProductSearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogProductSearch.show();
//                dialogProductSearch.setCancelable(false);

                EditText editTextProduct = dialogProductSearch.findViewById(R.id.edtTxt_productSearch);
                RecyclerView recyclerViewProductSearch = dialogProductSearch.findViewById(R.id.lv_searchableProductView);
                RecyclerView recyclerViewFilteredProduct = dialogProductSearch.findViewById(R.id.lv_filteredProductView);
                ProgressBar progressBar2 = dialogProductSearch.findViewById(R.id.progressBar);
                recyclerViewProductSearch.setHasFixedSize(true);
                recyclerViewFilteredProduct.setHasFixedSize(true);
                recyclerViewProductSearch.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewFilteredProduct.setLayoutManager(new LinearLayoutManager(this));

                productPageNo = 1;
                HashMap<String, String> mapGetProducts = new HashMap<>();
                mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
                mapGetProducts.put(LIMIT, String.valueOf(10));
                mapGetProducts.put(API_KEY, apikey);
                AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
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
                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                    productsArrayList.add(products);
                                    rvAdapterProductSearch = new RvAdapterProductSearch(productsArrayList, FullScannerActivityPurchase.this, productPageNo);
                                    recyclerViewProductSearch.setAdapter(rvAdapterProductSearch);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, mapGetProducts);

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
                                //bottom of list!
                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterProductSearch.notifyItemInserted(productsArrayList.size());
                                productPageNo++;
                                Toast.makeText(FullScannerActivityPurchase.this, "Please wait...", Toast.LENGTH_SHORT).show();

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


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {


                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                            JSONObject productData = jsonArray.getJSONObject(i);
                                                            products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                                            productsArrayList.add(products);
                                                        }
                                                        rvAdapterProductSearch.notifyDataSetChanged();
                                                      /*  rvAdapterProductSearch = new RvAdapterProductSearch(productsArrayList, FullScannerActivity.this);
                                                        recyclerViewProductSearch.setAdapter(rvAdapterProductSearch);*/
                                                        isLoading3 = false;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 4000);
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
                        productFilteredPageNo = 1;

                        if (s.length() >= 3) {
                            progressBar2.setVisibility(View.VISIBLE);
                            ArrayList<Products> finalFilteredArray = new ArrayList<>();
                            HashMap<String, String> mapGetProducts = new HashMap<>();
                            mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productFilteredPageNo));
                            mapGetProducts.put(LIMIT, String.valueOf(10));
                            mapGetProducts.put(PRODUCT_SEARCH_KEY, s.toString());
                            mapGetProducts.put(API_KEY, apikey);
                            AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
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
                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                                finalFilteredArray.add(products);
                                                RvAdapterProductSearch adapterProductSearch = new RvAdapterProductSearch(finalFilteredArray, FullScannerActivityPurchase.this, productPageNo);
                                                recyclerViewFilteredProduct.setAdapter(adapterProductSearch);

                                            }
//                                            rvAdapterPersonSearch.notifyDataSetChanged();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }, mapGetProducts);


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
                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            rvAdapterProductSearch.notifyItemInserted(finalFilteredArray.size());
                                            productFilteredPageNo++;
                                            Toast.makeText(FullScannerActivityPurchase.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
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
                                                    mapForScroll.put(PRODUCT_PAGE_NO, String.valueOf(productFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(PRODUCT_SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_3, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject productData = jsonArray.getJSONObject(i);
                                                                        products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                                                        finalFilteredArray.add(products);
                                                                    }
                                                                    rvAdapterProductSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading4 = false;
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
                                            }, 4000);
                                            isLoading4 = true;
                                        }
                                    }
                                }
                            });

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
                    incDisc = incDisc + 1;
                    if (incDisc <= subTotal) {
//                        editText_discount.setText(String.format("%.2f", incDisc));
                        editText_discount.setText(String.valueOf((int) incDisc));
                    }
                }
                break;
            case R.id.btn_discountDecrease:
                String getDisc2 = editText_discount.getText().toString();

                if (!TextUtils.isEmpty(getDisc2)) {
                    double decDisc = Double.parseDouble(getDisc2);
                    decDisc = decDisc - 1;
                    if (decDisc > -1) {
                        editText_discount.setText(String.valueOf((int) decDisc));
//                        editText_discount.setText(String.format("%.2f", decDisc));
                    }
                }
                break;

            case R.id.btn_invoiceSave:
             /*   String customerName = textViewCustomerName.getText().toString();
                if (!TextUtils.isEmpty(customerName)) {
                    invoiceModelList = new ArrayList<>();
                    invoiceModel = new InvoiceModel("1", customerName, "27 Jan 2021", "29 Jan 2021", invoiceItemList, discount, deduction, total);
                    invoiceModelList.add(invoiceModel);
                    InvoiceModel current = invoiceModelList.get(0);
                    Toast.makeText(this, current.getCustomerName(), Toast.LENGTH_SHORT).show();

                    if (invoiceModelList.size() > 0) {
                        Intent invoiceActivity = new Intent(this, InvoiceViewerActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("selectedProductList", (Serializable) invoiceModelList);
                        invoiceActivity.putExtra("BUNDLE", args);
//                    invoiceActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(invoiceActivity);
//                    Toast.makeText(this, "array.length", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(this, "No item found!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "Please check customer name.", Toast.LENGTH_SHORT).show();*/

                String getCustomerName = textViewCustomerName.getText().toString();
                if (!TextUtils.isEmpty(getCustomerName))
                {
                    JSONArray jsonArrayInvoiceItem = new JSONArray();
                    for (int i = 0; i < invoiceItemList.size(); i++)
                    {
                        InvoiceItem current = invoiceItemList.get(i);
                        JSONObject jsonObjectTemp = new JSONObject();
                        try {
                            jsonObjectTemp.put("itemno", current.getItemno());
                            jsonObjectTemp.put("item_id", current.getItem_id());
                            jsonObjectTemp.put("expirydate", current.getExpirydate());
                            jsonObjectTemp.put("unitid", current.getUnitid());
                            jsonObjectTemp.put("qty", current.getQty());
                            jsonObjectTemp.put("unitprice", current.getUnitprice());
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
                        jsonObjectInvoiceItem.put("currency", "1");
                        jsonObjectInvoiceItem.put("discount", "0");
                        jsonObjectInvoiceItem.put("deduction", "0.00");
                        jsonObjectInvoiceItem.put("items", jsonArrayInvoiceItem);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HashMap<String, String > mapTemp = new HashMap<>();
                    mapTemp.put(API_KEY, apikey);
                    mapTemp.put("invoice", jsonObjectInvoiceItem.toString());
                    mapTemp.put("status", "1");
                    AppController.getAppController().getAppNetworkController().makeRequest(url_5, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                        Toast.makeText(FullScannerActivity.this, response, Toast.LENGTH_LONG).show();
                            try {
                                JSONObject temp = new JSONObject(response);

                                if (temp.getString("error").equalsIgnoreCase("false"))
                                {
                                    Toast.makeText(FullScannerActivityPurchase.this, temp.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }, mapTemp);
                } else
                    Toast.makeText(this, "Please, select a customer", Toast.LENGTH_SHORT).show();



                break;

                /*case R.id.btn_nxtProductList:
                invoiceItemList = new ArrayList<>();
                invoiceItemList = dbHelper.invoiceItemTable();
                if (invoiceItemList.size() > 0) {
                    Intent invoiceActivity = new Intent(this, InvoiceActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("selectedProductList", (Serializable) invoiceItemList);
                    invoiceActivity.putExtra("BUNDLE", args);
                    startActivity(invoiceActivity);
                } else
                    Toast.makeText(this, "No item found!", Toast.LENGTH_SHORT).show();
                break;*/


            //For InvoiceViewActivity
           /*     searchListItemList = new ArrayList<>();
                searchListItemList = dbHelper.showAllSelectedProductsInInvoiceActivity();
                if (searchListItemList.size() > 0) {
                    Intent invoiceActivity = new Intent(this, InvoiceViewerActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("selectedProductList", (Serializable) searchListItemList);
                    invoiceActivity.putExtra("BUNDLE", args);
//                    invoiceActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    dbHelper.mDeleteProductList();
                    startActivity(invoiceActivity);
//                    Toast.makeText(this, "array.length", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "No item found!", Toast.LENGTH_SHORT).show();
*/
        }
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

        ScanerDilogFragmentActivity scanerDilogFragmentActivity = (ScanerDilogFragmentActivity) getSupportFragmentManager().findFragmentByTag("scannerDialogFragmentActivity");
        if (scanerDilogFragmentActivity != null && scanerDilogFragmentActivity.isVisible()) {
            scanerDilogFragmentActivity.dismiss();
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
    public void productClickHandler(String name, String id, int productPageNo) {
        HashMap<String, String> mapGetProductById = new HashMap<>();
        mapGetProductById.put(API_KEY, apikey);
//        mapGetProductById.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
        AppController.getAppController().getAppNetworkController().makeRequest(url_1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(FullScannerActivityPurchase.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullScannerActivityPurchase.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProductById);
        showMessageDialog("itemNo", "item_id", "product_name", "500", "unitid", "expirydate", "discount_percentage");


        dialogProductSearch.dismiss();
    }
}