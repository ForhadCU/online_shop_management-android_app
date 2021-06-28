package com.agamilabs.smartshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallbackClickHandler;
import com.agamilabs.smartshop.Interfaces.IClickHandler;
import com.agamilabs.smartshop.adapter.RvAdapterBrandSearch;
import com.agamilabs.smartshop.adapter.RvAdapterCatSearch;
import com.agamilabs.smartshop.adapter.RvAdapterGenericNameSearch;
import com.agamilabs.smartshop.adapter.RvAdapterProductSearch;
import com.agamilabs.smartshop.constants.Array_JSON;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.ProductBrandModel;
import com.agamilabs.smartshop.model.ProductCatModel;
import com.agamilabs.smartshop.model.ProductGenNameModel;
import com.agamilabs.smartshop.model.ProductUnitModel;
import com.agamilabs.smartshop.model.Products;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddNewProductActivity extends BaseScannerActivity implements View.OnClickListener, ICallbackClickHandler, IClickHandler,
        MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener{

    /**
     * For Scanner..
     */
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 7;
    private ViewGroup contentFrame;
    private ImageButton imageButtonFlashOn, imageButtonFlashOff, imageButtonFocusOn, imageButtonFocusOff;
    private ProgressBar progressBarSkuRequest;
    private AlertDialog.Builder builder;
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    /**
     * End
     */

    private static final String API_KEY = "apikey";
    private static final String SEARCH_KEY = "search_key";
    private static final String PAGE_NO = "page";
    private static final String LIMIT = "limit";
    private static final String CAT_NAME = "categoryname";
    private static final String BRAND_NAME = "brandname";
    private static final String GENERIC_NAME = "genericname";
    private String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    //    private String url_2 = "http://pharmacy.egkroy.com/app-module/php/get_org_items_by_name.php";
    private String url_2 = "http://pharmacy.egkroy.com/app-module/php/get_org_items_by_name.php";

    private Toolbar toolbar;
    private LinearLayout llItemName, llExpandItemNameSearch, llCatName, llExpandCatNameSearch, llBrandName, llExpandBrandNameSearch, llGenericName, llExpandGenericNameSearch;
    private CheckBox checkBoxHaveExpiry;
    private EditText editTextProductSearch, editTextCatSearch, editTextBrandSearch, editTextGenericNameSearch;
    private RecyclerView recyclerViewProductSearch, recyclerViewFilteredProduct, recyclerViewCatSearch,
            recyclerViewFilteredCat, recyclerViewBrandSearch, recyclerViewFilteredBrand, recyclerViewGenericNameSearch, recyclerViewFilteredGenericName;
    private ProgressBar progressBar2;
    private TextView textViewProductName, textViewCatName, textViewBrandName, textViewGenericName;
    private RadioButton radioButtonInventory, radioButtonNonInventory, radioButtonService;
    private ImageView imageViewGetLink;
    private Button buttonCancel, buttonPreview, buttonSave;
    private Spinner unitSpinner;
    private Dialog dialogGetLink, dialogAddNewCatBrandGen;
    private ImageView imageViewAddNewCat, imageViewAddNewBrand, imageViewAddNewGenericName, imageViewScanner;
    private EditText editTextUpc, editTextInitialQty, editTextReorderPts, editTextDiscountRate, editTextPurchaseRate, editTextSaleRate;
    private CheckBox checkBoxPurchase, checkBoxSale;
    private LinearLayout llAddProductMainPage;
    private FrameLayout flScanner;

    private ArrayList<Products> productsArrayList;
    private ArrayList<ProductCatModel> productCatModelArrayList;
    private ArrayList<ProductBrandModel> productBrandModelArrayList;
    private ArrayList<ProductGenNameModel> productGenNameModelArrayList;
    private ArrayList<ProductUnitModel> productUnitModelArrayList;
    private Products products;
    private ProductCatModel productCatModel;
    private ProductBrandModel productBrandModel;
    private ProductGenNameModel productGenNameModel;
    private ProductUnitModel productUnitModel;
    private RvAdapterProductSearch rvAdapterProductSearch;
    private RvAdapterCatSearch rvAdapterCatSearch;
    private RvAdapterBrandSearch rvAdapterBrandSearch;
    private RvAdapterGenericNameSearch rvAdapterGenericNameSearch;

    private int productPageNo = 1, productFilteredPageNo = 1, catPageNo = 1, catFilteredPageNo = 1,
            brandPageNo = 1, brandFilteredPageNo = 1, genericPageNo = 1, genericFilteredPageNo = 1;
    private boolean isLoading1ProductName = false;
    private boolean isLoading2ProductName = false;
    private boolean isLoading3ProductName = false;
    private boolean isLoading1CatName = false;
    private boolean isLoading2CatName = false;
    private boolean isLoading3CatName = false;
    private boolean isLoading1BrandName = false;
    private boolean isLoading2BrandName = false;
    private boolean isLoading3BrandName = false;
    private boolean isLoading1GenericName = false;
    private boolean isLoading2GenericName = false;
    private boolean isLoading3GenericName = false;
    private String newCatName, newBrandName, newGenericName;
    private String typeId, unitId, hasexpiry, itemno, reorderpoint,
            remainingquantity, purchaserate, salerate, taxtype, category,
            brand, generic, upc, discount_rate;

    @Override
    protected void onCreate(Bundle state) {
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
        setContentView(R.layout.activity_add_new_product);

        /**
         * For scanner
         */
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);

        imageButtonFlashOn = findViewById(R.id.imgBtn_flash_on);
        imageButtonFlashOff = findViewById(R.id.imgBtn_flash_off);
        imageButtonFocusOn = findViewById(R.id.imgBtn_focus_on);
        imageButtonFocusOff = findViewById(R.id.imgBtn_focus_off);
        progressBarSkuRequest = findViewById(R.id.progress_skuRequest);

        builder = new AlertDialog.Builder(this);

        setupFormats();
        contentFrame.addView(mScannerView);

        imageButtonFocusOn.setOnClickListener(this);
        imageButtonFocusOff.setOnClickListener(this);
        imageButtonFlashOn.setOnClickListener(this);
        imageButtonFlashOff.setOnClickListener(this);
        /**
         * End
         */

        toolbar = findViewById(R.id.main_app_bar);
        llExpandItemNameSearch = findViewById(R.id.llExpandItemNameSearch);
        llExpandCatNameSearch = findViewById(R.id.llExpandCatNameSearch);
        llExpandBrandNameSearch = findViewById(R.id.llExpandBrandNameSearch);
        llExpandGenericNameSearch = findViewById(R.id.llExpandGenericNameSearch);

        llItemName = findViewById(R.id.llItemName);
        llCatName = findViewById(R.id.llSelectCat);
        llBrandName = findViewById(R.id.llSelectBrand);
        llGenericName = findViewById(R.id.llSelectGenericName);

        editTextProductSearch = findViewById(R.id.edtTxt_productSearch);
        editTextCatSearch = findViewById(R.id.edtTxt_catSearch);
        editTextBrandSearch = findViewById(R.id.edtTxt_brandSearch);
        editTextGenericNameSearch = findViewById(R.id.edtTxt_genericSearch);

        textViewProductName = findViewById(R.id.tvProductName);
        textViewCatName = findViewById(R.id.tv_cat);
        textViewBrandName = findViewById(R.id.tv_brand);
        textViewGenericName = findViewById(R.id.tv_genericName);

        recyclerViewProductSearch = findViewById(R.id.lv_searchableProductView);
        recyclerViewFilteredProduct = findViewById(R.id.lv_filteredProductView);
        recyclerViewCatSearch = findViewById(R.id.lv_searchableCatView);
        recyclerViewFilteredCat = findViewById(R.id.lv_filteredCatView);
        recyclerViewBrandSearch = findViewById(R.id.lv_searchableBrandView);
        recyclerViewFilteredBrand = findViewById(R.id.lv_filteredBrandView);
        recyclerViewGenericNameSearch = findViewById(R.id.lv_searchableGenericView);
        recyclerViewFilteredGenericName = findViewById(R.id.lv_filteredGenView);

        progressBar2 = findViewById(R.id.progressBar);
        radioButtonInventory = findViewById(R.id.radioBtn_inventory);
        radioButtonNonInventory = findViewById(R.id.radioBtn_nonInventory);
        radioButtonService = findViewById(R.id.radioBtn_service);
        imageViewGetLink = findViewById(R.id.imgV_linkIcon);
        buttonCancel = findViewById(R.id.btn_cancelProduct);
        buttonPreview = findViewById(R.id.btn_previewAddProduct);
        buttonSave = findViewById(R.id.btn_saveProduct);
        checkBoxHaveExpiry = findViewById(R.id.checkbox_haveExpiry);
        unitSpinner = findViewById(R.id.spinnerUnit);
        imageViewAddNewCat = findViewById(R.id.imgV_addCat);
        imageViewAddNewBrand = findViewById(R.id.imgV_addBrand);
        imageViewAddNewGenericName = findViewById(R.id.imgV_addGenName);

        editTextUpc = findViewById(R.id.edtTxt_upc);
        editTextInitialQty = findViewById(R.id.edtTxt_initialQty);
        editTextReorderPts = findViewById(R.id.edtTxt_reorderPts);
        editTextDiscountRate = findViewById(R.id.edtTxt_discountRate);
        editTextPurchaseRate = findViewById(R.id.edtTxt_purchaseRate);
        editTextSaleRate = findViewById(R.id.edtTxt_saleRate);
        checkBoxPurchase = findViewById(R.id.checkbox_purchaseThis);
        checkBoxSale = findViewById(R.id.checkbox_saleThis);
        imageViewScanner = findViewById(R.id.imgV_upcScan);
        llAddProductMainPage = findViewById(R.id.llAddProductMainPage);
        flScanner = findViewById(R.id.flScanner);


        handleAppbar();
        handleRecyclerView();
        handleUnitSpinner();
        setOnclick();


    }

    private void handleUnitSpinner() {
        HashMap<String, String> mapForScroll = new HashMap<>();
        mapForScroll.put(API_KEY, apikey);

        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(Array_JSON.unit_json);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        productUnitModelArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject productData = jsonArray.getJSONObject(i);
                                productUnitModel = new ProductUnitModel(productData.getString("unitid"), productData.getString("unittextshort")
                                        , productData.getString("unittext"), productData.getString("parent_unitid"), productData.getString("multiplier"));
                                productUnitModelArrayList.add(productUnitModel);
                            }
                        }

                        ArrayAdapter<ProductUnitModel> arrayAdapter = new ArrayAdapter<ProductUnitModel>(
                                AddNewProductActivity.this, android.R.layout.simple_spinner_item, productUnitModelArrayList);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        unitSpinner.setAdapter(arrayAdapter);

                        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ProductUnitModel current = (ProductUnitModel) parent.getSelectedItem();
                                unitId = current.getUnitid();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapForScroll);

    }

    private void setOnclick() {
        llItemName.setOnClickListener(this);
        llExpandItemNameSearch.setOnClickListener(this);

        llCatName.setOnClickListener(this);
        llExpandCatNameSearch.setOnClickListener(this);

        llBrandName.setOnClickListener(this);
        llExpandBrandNameSearch.setOnClickListener(this);

        llGenericName.setOnClickListener(this);
        llExpandGenericNameSearch.setOnClickListener(this);

        radioButtonNonInventory.setOnClickListener(this);
        radioButtonInventory.setOnClickListener(this);
        radioButtonService.setOnClickListener(this);
        imageViewGetLink.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonPreview.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        imageViewAddNewCat.setOnClickListener(this);
        imageViewAddNewBrand.setOnClickListener(this);
        imageViewAddNewGenericName.setOnClickListener(this);
        imageViewScanner.setOnClickListener(this);
    }

    private void handleRecyclerView() {
        recyclerViewProductSearch.setHasFixedSize(true);
        recyclerViewFilteredProduct.setHasFixedSize(true);
        recyclerViewProductSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFilteredProduct.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewCatSearch.setHasFixedSize(true);
        recyclerViewFilteredCat.setHasFixedSize(true);
        recyclerViewCatSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFilteredCat.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewBrandSearch.setHasFixedSize(true);
        recyclerViewFilteredBrand.setHasFixedSize(true);
        recyclerViewBrandSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFilteredBrand.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewGenericNameSearch.setHasFixedSize(true);
        recyclerViewFilteredGenericName.setHasFixedSize(true);
        recyclerViewGenericNameSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFilteredGenericName.setLayoutManager(new LinearLayoutManager(this));
    }

    private void handleAppbar() {
        this.setSupportActionBar(toolbar);
        this.setTitle("Add New Medicine To Store");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
       /*     case R.id.btn_save:
                break;
            case R.id.btn_previewAddProduct:
                break;
            case R.id.btn_cancelProduct:
                break;*/
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

            case R.id.imgV_upcScan:
                llAddProductMainPage.setVisibility(View.GONE);
                flScanner.setVisibility(View.VISIBLE);
                break;
            case R.id.radioBtn_inventory:
                radioButtonInventory.setChecked(true);
                radioButtonNonInventory.setChecked(false);
                radioButtonService.setChecked(false);
                typeId = "1";
                break;
            case R.id.radioBtn_nonInventory:
                radioButtonInventory.setChecked(false);
                radioButtonNonInventory.setChecked(true);
                radioButtonService.setChecked(false);
                typeId = "2";
                break;
            case R.id.radioBtn_service:
                radioButtonInventory.setChecked(false);
                radioButtonNonInventory.setChecked(false);
                radioButtonService.setChecked(true);
                typeId = "3";
                break;
            case R.id.imgV_linkIcon:
                dialogGetLink = new Dialog(this);
                dialogGetLink.setContentView(R.layout.dialog_get_link_layout);
//                dialogGetLink.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogGetLink.show();
                break;

            case R.id.imgV_addCat:
            case R.id.imgV_addBrand:
            case R.id.imgV_addGenName:
                dialogAddNewCatBrandGen = new Dialog(this);
                dialogAddNewCatBrandGen.setContentView(R.layout.dialog_add_new_cat_brand_gen_layout);
                dialogAddNewCatBrandGen.show();

                Button saveCat = dialogAddNewCatBrandGen.findViewById(R.id.btn_saveCat);
                Button saveBrand = dialogAddNewCatBrandGen.findViewById(R.id.btn_saveBrand);
                Button saveGeneric = dialogAddNewCatBrandGen.findViewById(R.id.btn_saveGeneric);
                EditText editTextNewCat = dialogAddNewCatBrandGen.findViewById(R.id.edtText_newCat);
                EditText editTextNewBrand = dialogAddNewCatBrandGen.findViewById(R.id.edtText_newBrand);
                EditText editTextNewGen = dialogAddNewCatBrandGen.findViewById(R.id.edtText_newGenericName);

                newCatName = editTextNewCat.getText().toString().trim();
                newBrandName = editTextNewBrand.getText().toString().trim();
                newGenericName = editTextNewGen.getText().toString().trim();

                saveCat.setOnClickListener(this);
                saveBrand.setOnClickListener(this);
                saveGeneric.setOnClickListener(this);
                break;

            case R.id.btn_saveCat:
                if (!TextUtils.isEmpty(newCatName)) {
                    HashMap<String, String> mapInput = new HashMap<>();
                    mapInput.put(API_KEY, apikey);
                    mapInput.put(CAT_NAME, newCatName);

                    insert(mapInput);

                } else {
                    Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_saveBrand:
                if (!TextUtils.isEmpty(newBrandName)) {
                    HashMap<String, String> mapInput = new HashMap<>();
                    mapInput.put(API_KEY, apikey);
                    mapInput.put(BRAND_NAME, newBrandName);

                    insert(mapInput);
                } else {
                    Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_saveGeneric:
                if (!TextUtils.isEmpty(newGenericName)) {
                    HashMap<String, String> mapInput = new HashMap<>();
                    mapInput.put(API_KEY, apikey);
                    mapInput.put(GENERIC_NAME, newGenericName);

                    insert(mapInput);

                } else {
                    Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_saveProduct:


                break;

            case R.id.llItemName:
                if (llExpandItemNameSearch.getVisibility() != View.VISIBLE) {
                    llExpandItemNameSearch.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    textViewProductName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_float, 0);
                } else {
                    llExpandItemNameSearch.setVisibility(View.GONE);
                    textViewProductName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
                }
                mGetProductList();

                recyclerViewProductSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading2ProductName) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsArrayList.size() - 1) {
                                //bottom of list! and start loading
                                productsArrayList.add(new Products(null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterProductSearch.notifyItemInserted(productsArrayList.size());
                                productPageNo++;
                                Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

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
                                        mapForScroll.put(PAGE_NO, String.valueOf(productPageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
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
                                                        isLoading2ProductName = false;
                                                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                        progressBar2.setVisibility(View.GONE);
                                                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);
                                isLoading2ProductName = true;
                            }
                        }
                    }
                });

                editTextProductSearch.addTextChangedListener(new TextWatcher() {
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

                                    if (!isLoading3ProductName) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsArrayList.size() - 1) {
                                            //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            finalFilteredArray.add(new Products(null, null));
                                            rvAdapterProductSearch.notifyItemInserted(finalFilteredArray.size());
                                            productFilteredPageNo++;
                                            Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

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
                                                    mapForScroll.put(PAGE_NO, String.valueOf(productFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
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
                                                                    isLoading3ProductName = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                                    progressBar2.setVisibility(View.GONE);
                                                                    Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            isLoading3ProductName = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetProductList();
                        } else
                            progressBar2.setVisibility(View.GONE);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            /**
             * Category Searching...
             */
            case R.id.llSelectCat:
                if (llExpandCatNameSearch.getVisibility() != View.VISIBLE) {
                    llExpandCatNameSearch.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);
                    textViewCatName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_float, 0);
                } else {
                    llExpandCatNameSearch.setVisibility(View.GONE);
                    textViewCatName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
                }
                mGetCatList();

                recyclerViewCatSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading2CatName) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productCatModelArrayList.size() - 1) {
                                //bottom of list! and start loading
                                productCatModelArrayList.add(new ProductCatModel(null, null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterCatSearch.notifyItemInserted(productCatModelArrayList.size());
                                catPageNo++;
                                Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        productCatModelArrayList.remove(productCatModelArrayList.size() - 1);
                                        int scrollPosition = productCatModelArrayList.size();
                                        rvAdapterCatSearch.notifyItemRemoved(scrollPosition);
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
                                        mapForScroll.put(PAGE_NO, String.valueOf(catPageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {


                                                    JSONObject jsonObject = new JSONObject(Array_JSON.category_json);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        if (jsonArray.length() > 0) {
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject productData = jsonArray.getJSONObject(i);
                                                                productCatModel = new ProductCatModel(productData.getString("catid"), productData.getString("cattext"), productData.getString("parentcatid"));
                                                                productCatModelArrayList.add(productCatModel);
                                                            }
                                                        }/* else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/
                                                        rvAdapterCatSearch.notifyDataSetChanged();
                                                        isLoading2CatName = false;
                                                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                        progressBar2.setVisibility(View.GONE);
                                                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);
                                isLoading2CatName = true;
                            }
                        }
                    }
                });

                editTextCatSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filter arrayList
                        recyclerViewCatSearch.setVisibility(View.VISIBLE);
                        recyclerViewFilteredCat.setVisibility(View.GONE);

                        filterCat(s.toString());

                        if (s.length() >= 3) {
                            ArrayList<ProductCatModel> finalFilteredArray = new ArrayList<>();
                            mGetFilteredCatList(s, progressBar2, recyclerViewFilteredCat, recyclerViewCatSearch, finalFilteredArray);

                            recyclerViewFilteredCat.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading3CatName) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productCatModelArrayList.size() - 1) {
                                            //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            finalFilteredArray.add(new ProductCatModel(null, null, null));
                                            rvAdapterCatSearch.notifyItemInserted(finalFilteredArray.size());
                                            catFilteredPageNo++;
                                            Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
                                                    rvAdapterCatSearch.notifyItemRemoved(scrollPosition);
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
                                                    mapForScroll.put(PAGE_NO, String.valueOf(catFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject productData = jsonArray.getJSONObject(i);
                                                                        ProductCatModel products = new ProductCatModel(productData.getString("catid"), productData.getString("cattext"),
                                                                                productData.getString("parentcatid"));
                                                                        finalFilteredArray.add(products);
                                                                    }
                                                                    rvAdapterCatSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading3CatName = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                                    progressBar2.setVisibility(View.GONE);
                                                                    Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            isLoading3CatName = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetCatList();
                        } else
                            progressBar2.setVisibility(View.GONE);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            /**
             * Brand Searching...
             */
            case R.id.llSelectBrand:
                if (llExpandBrandNameSearch.getVisibility() != View.VISIBLE) {
                    llExpandBrandNameSearch.setVisibility(View.VISIBLE);
                    textViewBrandName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_float, 0);
                } else {
                    llExpandBrandNameSearch.setVisibility(View.GONE);
                    textViewBrandName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
                }
                mGetBrandList();

                recyclerViewBrandSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading2BrandName) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productBrandModelArrayList.size() - 1) {
                                //bottom of list! and start loading
                                productBrandModelArrayList.add(new ProductBrandModel(null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterBrandSearch.notifyItemInserted(productBrandModelArrayList.size());
                                brandPageNo++;
                                Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        productBrandModelArrayList.remove(productBrandModelArrayList.size() - 1);
                                        int scrollPosition = productBrandModelArrayList.size();
                                        rvAdapterBrandSearch.notifyItemRemoved(scrollPosition);
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
                                        mapForScroll.put(PAGE_NO, String.valueOf(brandPageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {


                                                    JSONObject jsonObject = new JSONObject(Array_JSON.brand_json);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        if (jsonArray.length() > 0) {
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject productData = jsonArray.getJSONObject(i);
                                                                productBrandModel = new ProductBrandModel(productData.getString("brandno"), productData.getString("brandno"));
                                                                productBrandModelArrayList.add(productBrandModel);
                                                            }
                                                        }/* else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/
                                                        rvAdapterBrandSearch.notifyDataSetChanged();
                                                        isLoading2BrandName = false;
                                                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                        progressBar2.setVisibility(View.GONE);
                                                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);
                                isLoading2BrandName = true;
                            }
                        }
                    }
                });

                editTextBrandSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filter arrayList
                        recyclerViewBrandSearch.setVisibility(View.VISIBLE);
                        recyclerViewFilteredBrand.setVisibility(View.GONE);

                        filterBrands(s.toString());

                        if (s.length() >= 3) {
                            ArrayList<ProductBrandModel> finalFilteredArray = new ArrayList<>();
                            mGetFilteredBrandList(s, progressBar2, recyclerViewFilteredBrand, recyclerViewBrandSearch, finalFilteredArray);

                            recyclerViewFilteredBrand.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading3BrandName) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productBrandModelArrayList.size() - 1) {
                                            //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            finalFilteredArray.add(new ProductBrandModel(null, null));
                                            rvAdapterBrandSearch.notifyItemInserted(finalFilteredArray.size());
                                            brandFilteredPageNo++;
                                            Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
                                                    rvAdapterBrandSearch.notifyItemRemoved(scrollPosition);
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
                                                    mapForScroll.put(PAGE_NO, String.valueOf(brandFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject productData = jsonArray.getJSONObject(i);
                                                                        ProductBrandModel brands = new ProductBrandModel(productData.getString("brandno"), productData.getString("brandname"));
                                                                        finalFilteredArray.add(brands);
                                                                    }
                                                                    rvAdapterBrandSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading3BrandName = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                                    progressBar2.setVisibility(View.GONE);
                                                                    Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            isLoading3BrandName = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetBrandList();
                        } else
                            progressBar2.setVisibility(View.GONE);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            /**
             * GenericName Searching...
             */
            case R.id.llSelectGenericName:
                if (llExpandGenericNameSearch.getVisibility() != View.VISIBLE) {
                    llExpandGenericNameSearch.setVisibility(View.VISIBLE);
                    textViewGenericName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow_float, 0);
                } else {
                    llExpandGenericNameSearch.setVisibility(View.GONE);
                    textViewGenericName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
                }
                mGetGenericNameList();

                recyclerViewGenericNameSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                        if (!isLoading2GenericName) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productGenNameModelArrayList.size() - 1) {
                                //bottom of list! and start loading
                                productGenNameModelArrayList.add(new ProductGenNameModel(null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                                rvAdapterGenericNameSearch.notifyItemInserted(productGenNameModelArrayList.size());
                                genericPageNo++;
                                Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        productGenNameModelArrayList.remove(productGenNameModelArrayList.size() - 1);
                                        int scrollPosition = productGenNameModelArrayList.size();
                                        rvAdapterGenericNameSearch.notifyItemRemoved(scrollPosition);
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
                                        mapForScroll.put(PAGE_NO, String.valueOf(genericPageNo));
                                        mapForScroll.put(LIMIT, String.valueOf(10));


                                        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {


                                                    JSONObject jsonObject = new JSONObject(Array_JSON.genericName_json);
                                                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                        if (jsonArray.length() > 0) {
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject productData = jsonArray.getJSONObject(i);
                                                                productGenNameModel = new ProductGenNameModel(productData.getString("genericid"), productData.getString("genericname"));
                                                                productGenNameModelArrayList.add(productGenNameModel);
                                                            }
                                                        }/* else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/
                                                        rvAdapterGenericNameSearch.notifyDataSetChanged();
                                                        isLoading2GenericName = false;
                                                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                        progressBar2.setVisibility(View.GONE);
                                                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, mapForScroll);
                                    }
                                }, 1500);
                                isLoading2GenericName = true;
                            }
                        }
                    }
                });

                editTextGenericNameSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //filter arrayList
                        recyclerViewGenericNameSearch.setVisibility(View.VISIBLE);
                        recyclerViewFilteredGenericName.setVisibility(View.GONE);

                        filterGenericName(s.toString());

                        if (s.length() >= 3) {
                            ArrayList<ProductGenNameModel> finalFilteredArray = new ArrayList<>();
                            mGetFilteredGenericNameList(s, progressBar2, recyclerViewFilteredGenericName, recyclerViewGenericNameSearch, finalFilteredArray);

                            recyclerViewFilteredGenericName.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                                    if (!isLoading3GenericName) {
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productGenNameModelArrayList.size() - 1) {
                                            //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                            finalFilteredArray.add(new ProductGenNameModel(null, null));
                                            rvAdapterGenericNameSearch.notifyItemInserted(finalFilteredArray.size());
                                            genericFilteredPageNo++;
                                            Toast.makeText(AddNewProductActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                                    int scrollPosition = finalFilteredArray.size();
                                                    rvAdapterGenericNameSearch.notifyItemRemoved(scrollPosition);
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
                                                    mapForScroll.put(PAGE_NO, String.valueOf(genericFilteredPageNo));
                                                    mapForScroll.put(LIMIT, String.valueOf(10));
                                                    mapForScroll.put(SEARCH_KEY, s.toString());


                                                    AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            try {

                                                                JSONObject jsonObject = new JSONObject(response);
                                                                if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                                        JSONObject productData = jsonArray.getJSONObject(i);
                                                                        ProductGenNameModel genNameModel = new ProductGenNameModel(productData.getString("genericid"), productData.getString("genericname"));
                                                                        finalFilteredArray.add(genNameModel);
                                                                    }
                                                                    rvAdapterGenericNameSearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                                    isLoading3GenericName = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                                } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                                    progressBar2.setVisibility(View.GONE);
                                                                    Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                                            isLoading3GenericName = true;
                                        }
                                    }
                                }
                            });

                        } else if (s.length() == 0) {
                            mGetGenericNameList();
                        } else
                            progressBar2.setVisibility(View.GONE);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

        }
    }

    private void insert(HashMap<String, String> mapInput) {
        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapInput);


    }


    private void mGetFilteredProductList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredProduct, RecyclerView recyclerViewProductSearch, ArrayList<Products> finalFilteredArray) {
        productFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(productFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(SEARCH_KEY, s.toString());
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
                            products = new Products(productData.getString("id"), productData.getString("text"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            finalFilteredArray.add(products);
                            rvAdapterProductSearch = new RvAdapterProductSearch(finalFilteredArray, AddNewProductActivity.this, productPageNo);
                            recyclerViewFilteredProduct.setAdapter(rvAdapterProductSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewProductActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
    }

    private void mGetFilteredCatList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredCat, RecyclerView recyclerViewCatSearch, ArrayList<ProductCatModel> finalFilteredArray) {
        catFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(catFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(SEARCH_KEY, s.toString());
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
                            recyclerViewFilteredCat.setVisibility(View.VISIBLE);
                            recyclerViewCatSearch.setVisibility(View.GONE);
                            JSONObject productData = productDataArray.getJSONObject(i);
                            productCatModel = new ProductCatModel(productData.getString("catid"), productData.getString("cattext"),
                                    productData.getString("parentcatid"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            finalFilteredArray.add(productCatModel);
                            rvAdapterCatSearch = new RvAdapterCatSearch(finalFilteredArray, AddNewProductActivity.this, catPageNo);
                            recyclerViewFilteredCat.setAdapter(rvAdapterCatSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewProductActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
    }

    private void mGetFilteredBrandList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredBrand, RecyclerView recyclerViewBrandSearch, ArrayList<ProductBrandModel> finalFilteredArray) {
        brandFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(brandFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(SEARCH_KEY, s.toString());
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
                            recyclerViewFilteredBrand.setVisibility(View.VISIBLE);
                            recyclerViewBrandSearch.setVisibility(View.GONE);
                            JSONObject productData = productDataArray.getJSONObject(i);
                            productBrandModel = new ProductBrandModel(productData.getString("brandno"), productData.getString("brandname"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            finalFilteredArray.add(productBrandModel);
                            rvAdapterBrandSearch = new RvAdapterBrandSearch(finalFilteredArray, AddNewProductActivity.this, brandPageNo);
                            recyclerViewFilteredBrand.setAdapter(rvAdapterBrandSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewProductActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
    }

    private void mGetFilteredGenericNameList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredGenericName, RecyclerView recyclerViewGenericNameSearch, ArrayList<ProductGenNameModel> finalFilteredArray) {
        genericFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(genericFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(SEARCH_KEY, s.toString());
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
                            recyclerViewFilteredGenericName.setVisibility(View.VISIBLE);
                            recyclerViewGenericNameSearch.setVisibility(View.GONE);
                            JSONObject productData = productDataArray.getJSONObject(i);
                            productGenNameModel = new ProductGenNameModel(productData.getString("genericid"), productData.getString("genericname"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            finalFilteredArray.add(productGenNameModel);
                            rvAdapterGenericNameSearch = new RvAdapterGenericNameSearch(finalFilteredArray, AddNewProductActivity.this, genericPageNo);
                            recyclerViewFilteredGenericName.setAdapter(rvAdapterGenericNameSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(AddNewProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddNewProductActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
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

    private ArrayList<ProductCatModel> filterCat(String text) {
        ArrayList<ProductCatModel> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (ProductCatModel item : productCatModelArrayList) {
            if (item.getCattext().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterCatSearch.filterList(filteredList);
        return filteredList;
    }

    private ArrayList<ProductBrandModel> filterBrands(String text) {
        ArrayList<ProductBrandModel> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (ProductBrandModel item : productBrandModelArrayList) {
            if (item.getBrandname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterBrandSearch.filterList(filteredList);
        return filteredList;
    }

    private ArrayList<ProductGenNameModel> filterGenericName(String text) {
        ArrayList<ProductGenNameModel> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (ProductGenNameModel item : productGenNameModelArrayList) {
            if (item.getGenericname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterGenericNameSearch.filterList(filteredList);
        return filteredList;
    }

    private void mGetProductList() {

        productPageNo = 1;
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(productPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        productsArrayList = new ArrayList<>();
                        progressBar2.setVisibility(View.GONE);

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            JSONObject productData = customerDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            products = new Products(productData.getString("id"), productData.getString("text"));
                            productsArrayList.add(products);
                            rvAdapterProductSearch = new RvAdapterProductSearch(productsArrayList, AddNewProductActivity.this, productPageNo);
                            recyclerViewProductSearch.setAdapter(rvAdapterProductSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                    llExpandItemNameSearch.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
                llExpandItemNameSearch.setVisibility(View.GONE);
            }
        }, mapGetProducts);
    }


    private void mGetCatList() {
        catPageNo = 1;
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(catPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(Array_JSON.category_json);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        productCatModelArrayList = new ArrayList<>();
                        progressBar2.setVisibility(View.GONE);

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            JSONObject productData = customerDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            productCatModel = new ProductCatModel(productData.getString("catid"), productData.getString("cattext"),
                                    productData.getString("parentcatid"));
                            productCatModelArrayList.add(productCatModel);
                            rvAdapterCatSearch = new RvAdapterCatSearch(productCatModelArrayList, AddNewProductActivity.this, catPageNo);
                            recyclerViewCatSearch.setAdapter(rvAdapterCatSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                    llExpandCatNameSearch.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
                llExpandCatNameSearch.setVisibility(View.GONE);
            }
        }, mapGetProducts);
    }

    private void mGetBrandList() {
        brandPageNo = 1;
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(catPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(Array_JSON.brand_json);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        productBrandModelArrayList = new ArrayList<>();

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            progressBar2.setVisibility(View.GONE);
                            JSONObject productData = customerDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            productBrandModel = new ProductBrandModel(productData.getString("brandno"), productData.getString("brandname"));
                            productBrandModelArrayList.add(productBrandModel);
                            rvAdapterBrandSearch = new RvAdapterBrandSearch(productBrandModelArrayList, AddNewProductActivity.this, brandPageNo);
                            recyclerViewBrandSearch.setAdapter(rvAdapterBrandSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                    llExpandBrandNameSearch.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
                llExpandGenericNameSearch.setVisibility(View.GONE);
            }
        }, mapGetProducts);
    }

    private void mGetGenericNameList() {
        genericPageNo = 1;
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PAGE_NO, String.valueOf(catPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
        AppController.getAppController().getAppNetworkController().makeRequest(url_2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(Array_JSON.genericName_json);
                    if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                        JSONArray customerDataArray = jsonObject.getJSONArray("data");
                        productGenNameModelArrayList = new ArrayList<>();

                        for (int i = 0; i < customerDataArray.length(); i++) {
                            progressBar2.setVisibility(View.GONE);
                            JSONObject productData = customerDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                            productGenNameModel = new ProductGenNameModel(productData.getString("genericid"), productData.getString("genericname"));
                            productGenNameModelArrayList.add(productGenNameModel);
                            rvAdapterGenericNameSearch = new RvAdapterGenericNameSearch(productGenNameModelArrayList, AddNewProductActivity.this, genericPageNo);
                            recyclerViewGenericNameSearch.setAdapter(rvAdapterGenericNameSearch);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar2.setVisibility(View.GONE);
                    llExpandGenericNameSearch.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddNewProductActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                progressBar2.setVisibility(View.GONE);
                llExpandGenericNameSearch.setVisibility(View.GONE);
            }
        }, mapGetProducts);
    }


    @Override
    public void customerClickHandler(String id, String name) {
    }

    @Override
    public void productClickHandler(String name, String id) {
        itemno = id;
        textViewProductName.setText(name);
        llExpandItemNameSearch.setVisibility(View.GONE);
        textViewProductName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
    }

    @Override
    public void mCatClick(String name, String id, String pId) {
        category = id;
        textViewCatName.setText(name);
        llExpandCatNameSearch.setVisibility(View.GONE);
        textViewCatName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);

    }

    @Override
    public void mBrandClick(String name, String id) {
        brand = id;
        textViewBrandName.setText(name);
        llExpandBrandNameSearch.setVisibility(View.GONE);
        textViewBrandName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
    }

    @Override
    public void mGenericClick(String name, String id) {
        generic = id;
        textViewGenericName.setText(name);
        llExpandGenericNameSearch.setVisibility(View.GONE);
        textViewGenericName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow_float, 0);
    }

    @Override
    public void catClickHandler(String id, String name) {
    }

    @Override
    public void dataParsedStockLots(String itemName, ArrayList<LotsModel> lotsModelArrayList) {
    }

    @Override
    public void handleBottomSheet() {
    }

/**
 * For Scanner
 */

@Override
public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

    } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

    }
}


    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this, "onStart called", Toast.LENGTH_SHORT).show();
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


    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }
        progressBarSkuRequest.setVisibility(View.VISIBLE);

        mJSON_parser(rawResult);
    }

    private void mJSON_parser(Result rawResult) {
        progressBarSkuRequest.setVisibility(View.GONE);
        String scanResult = rawResult.getText().toString();
        mScannerView.resumeCameraPreview(this);
        flScanner.setVisibility(View.GONE);
        llAddProductMainPage.setVisibility(View.VISIBLE);
        editTextUpc.setText(scanResult);
    }

    //00001




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

}