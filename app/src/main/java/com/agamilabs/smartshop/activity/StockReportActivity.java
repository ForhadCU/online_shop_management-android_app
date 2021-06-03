package com.agamilabs.smartshop.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.agamilabs.smartshop.Interfaces.ICallbackClickHandler;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.RvAdapterCategorySearch;
import com.agamilabs.smartshop.adapter.RvAdapterProductSearch;
import com.agamilabs.smartshop.adapter.StockLotsAdapter;
import com.agamilabs.smartshop.adapter.StockReportAdapter;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.CategoryModel;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.Products;
import com.agamilabs.smartshop.model.StockReportModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class StockReportActivity extends AppCompatActivity implements ICallbackClickHandler, View.OnClickListener {

    private String GET_ITEMS_URL = "http://pharmacy.egkroy.com/app-module/php/get_org_items_by_name.php";
    private String SHOW_STOCK_URL = "http://pharmacy.egkroy.com/app-module/php/get_stock_info.php";
    private String CATEGORY_URL = "http://pharmacy.egkroy.com/app-module/php/get_item_category_modified.php";
    //    private String STOCK_URL = "http://192.168.1.3/android/AgamiLab/agami-logbook/view_section.php";
    private static final String API_KEY = "apikey";
    private static final String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc";
    private static final String PRODUCT_SEARCH_KEY = "search_key";
    private static final String PRODUCT_PAGE_NO = "page";
    private static final String LIMIT = "limit";
    private static final String CAT_PAGE_NO = "pageno";
    private static final String CAT_NAME = "name";
    private static final String CAT_SEARCH_KEY = "searchkey";
    private static final String CAT_ID = "catid";
    private static final String ITEM_NO = "itemno";
    private static final String SKU = "sku";
    private static final String REORDER = "reorder";
    private static final String START_DATE = "startdate";
    private static final String END_DATE = "enddate";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mStockRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CardView mCardView;
    private TextInputEditText mProductEdit;
    private TextView tvDateFrom, tvDateTo;
    private TextView mCategoryTextView;
    private Spinner mReorderSpin;
    private LinearLayout mDateFromLinear, mDateToLinear;
    private ImageButton mImgBtnDF, mImgBtnDT;
    private Calendar calendar;
    private TextInputEditText mSearchEditext;
    private ListView mSearchListView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private ProgressBar mProgressbar;
    private TextView textViewSpanTest;
    private Dialog dialogProductSearch, dialogCategorySearch;
    private FloatingActionButton fabStockSearch;
    private Toolbar toolbar;
    private LinearLayout llFilterLayout;
    private TextView tvProductName, tvCategoryName, tvShowStockBtn;
    private LinearLayout llProductSearch, llCategorySearch;
    private ImageView imgV_productSearchDownIcon, imgV_productSearchCrossIcon, imgV_categorySearchDownIcon, imgV_categorySearchCrossIcon;
    private SimpleDateFormat simpleDateFormat;
    private DatePickerDialog datePickerDialog;
    private RecyclerView rvStockReport;
    private LinearLayout llDateFrom, llDateTo;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout rlBottomSheet;
    private FrameLayout frameLayoutDarkBg;
    private ImageView imgV_stockLots;
    private RecyclerView rvStockLotsList;
    private LinearLayout llDiscardBottomSheet;

    private ArrayList<CategoryModel> categoryModelArrayList;
    private ArrayList<Products> productsArrayList;
    private RvAdapterCategorySearch rvAdapterCategorySearch;
    private RvAdapterProductSearch rvAdapterProductSearch;
    private CategoryModel categoryModel;
    private Products products;
    private ArrayList<StockReportModel> stockReportModelList;
    private ArrayList<LotsModel> lotsModelArrayList;
    private ArrayList<String> mCategoryList;
    private StockReportAdapter mStockAdapter;
    private StockLotsAdapter stockLotsAdapter;

    private int count = 0, pageNo = 1, catFilteredPageNo = 1;
    private int productPageNo = 1, productFilteredPageNo = 1;
    private boolean isLoading = false;
    private boolean isLoading2 = false;
    private boolean isLoading3 = false;
    private boolean isLoading4 = false;
    private int year, month, day;
    private int catPageNo;
    private double discount, deduction, total = 0;
    private double subTotal = 0.00;
    private int testLast = 0, lengthArray = 0;
    private int totalItemCount, pastVisiblesItems, visibleItemCount, page = 1, previousTotal;
    private boolean loading = true;
    private String dateTo, dateFrom;
    private String catId = "-1", itemNo = "-1", reorder = "-1";
    private String skuNum = " ";

    //top
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);

        mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
        mCardView = findViewById(R.id.cardView_stock_report);
        tvProductName = findViewById(R.id.tv_productName);
        tvCategoryName = findViewById(R.id.tv_category);
//        mCategoryTextView = findViewById(R.id.text_category);
        mReorderSpin = findViewById(R.id.spinner_reorder_point);
        tvDateFrom = findViewById(R.id.text_date_from);
        tvDateTo = findViewById(R.id.text_date_to);
        mImgBtnDF = findViewById(R.id.imagebtn_date_from);
        mImgBtnDT = findViewById(R.id.imagebtn_date_to);
        mProgressbar = findViewById(R.id.progressbar);
        toolbar = findViewById(R.id.main_app_bar);
        llFilterLayout = findViewById(R.id.llFilterLayout);
        llProductSearch = findViewById(R.id.llProductSearch);
        llCategorySearch = findViewById(R.id.llCategorySearch);
        tvShowStockBtn = findViewById(R.id.tv_showStock);
        imgV_productSearchDownIcon = findViewById(R.id.imgV_productSearchDownIcon);
        imgV_productSearchCrossIcon = findViewById(R.id.imgV_productSearchCrossIcon);
        imgV_categorySearchDownIcon = findViewById(R.id.imgV_categorySearchDownIcon);
        imgV_categorySearchCrossIcon = findViewById(R.id.imgV_categorySearchCrossIcon);
        rvStockReport = findViewById(R.id.rvStockReport);
        llDateFrom = findViewById(R.id.linear_date_from);
        llDateTo = findViewById(R.id.linear_date_to);
        rlBottomSheet = findViewById(R.id.bottom_sheet_details_of_stock_lots);
        bottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        frameLayoutDarkBg = findViewById(R.id.purchaseBillViewerDarkBg);
        rvStockLotsList = findViewById(R.id.rv_stockLotsList);
        llDiscardBottomSheet = findViewById(R.id.ll_discardBottomSheet);

        mSheetBehaviorHandler();
        mHandleAppBar();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        getCurrentDate();
        getPreviousDate();
        dialogBuilder = new AlertDialog.Builder(this);

        DatePickerDialog.OnDateSetListener myDateListener = null;
        mImgBtnDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });
        mImgBtnDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(2);
            }
        });


        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.DKGRAY);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCategoryList.clear();
//                loadProducts();
//                loadCategory(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mStockAdapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });

        llProductSearch.setOnClickListener(this);
        llCategorySearch.setOnClickListener(this);
        tvShowStockBtn.setOnClickListener(this);
        imgV_productSearchDownIcon.setOnClickListener(this);
        imgV_productSearchCrossIcon.setOnClickListener(this);
        imgV_categorySearchDownIcon.setOnClickListener(this);
        imgV_categorySearchCrossIcon.setOnClickListener(this);
        llDateFrom.setOnClickListener(this);
        llDateTo.setOnClickListener(this);
        mImgBtnDF.setOnClickListener(this);
        mImgBtnDT.setOnClickListener(this);
        llDiscardBottomSheet.setOnClickListener(this);
        frameLayoutDarkBg.setOnClickListener(this);
    }

    private void mSheetBehaviorHandler() {
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

    private void getPreviousDate() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 30);
        dateFrom = simpleDateFormat.format(calendar.getTime());
        tvDateFrom.setText(dateFrom);
    }

    private void getCurrentDate() {
        calendar = Calendar.getInstance();
        dateTo = simpleDateFormat.format(calendar.getTime());
        tvDateTo.setText(dateTo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rvHandler();
        mLoadStocks(catId, itemNo, skuNum, reorder, dateFrom, dateTo);
    }

    private void rvHandler() {
        rvStockReport.setHasFixedSize(true);
        rvStockReport.setLayoutManager(new LinearLayoutManager(this));
        rvStockLotsList.setHasFixedSize(true);
        rvStockLotsList.setLayoutManager(new LinearLayoutManager(this));
        stockReportModelList = new ArrayList<>();
        mStockAdapter = new StockReportAdapter(this, stockReportModelList, this);
        rvStockReport.setAdapter(mStockAdapter);
    }

    private void mLoadStocks(String catId, String itemNo, String skuNum, String reorderPoint, String dateFrom, String dateTo) {
        HashMap<String, String> mapLoadStocks = new HashMap<>();
        mapLoadStocks.put(API_KEY, apikey);
        mapLoadStocks.put(CAT_ID, catId);
        mapLoadStocks.put(ITEM_NO, itemNo);
//        mapLoadStocks.put(SKU, skuNum);
        mapLoadStocks.put(REORDER, reorderPoint);
        mapLoadStocks.put(START_DATE, dateFrom);
        mapLoadStocks.put(END_DATE, dateTo);

        Log.d("loadStockInput", "mLoadStocks: " + mapLoadStocks.toString());

        AppController.getAppController().getAppNetworkController().makeRequest(SHOW_STOCK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("StockInfoResponse", "onResponse: " + response);
                try {
                    JSONObject data = new JSONObject(response);
                    stockReportModelList.clear();
                    JSONObject jsonObjectData = data.getJSONObject("data");
                    if (data.getString("error").equalsIgnoreCase("false")) {
                        JSONArray jsonArrayReport = jsonObjectData.getJSONArray("report");

                        for (int i = 0; i < jsonArrayReport.length(); i++) {
                            JSONObject jsonObject = jsonArrayReport.getJSONObject(i);

                            int orgno = jsonObject.getInt("orgno");
                            int itemno = jsonObject.getInt("itemno");
                            String reorderpoint = jsonObject.getString("reorderpoint");
                            int remainingqty = jsonObject.getInt("remainingqty");
                            int stockinqty = jsonObject.getInt("stockinqty");
                            int stockoutqty = jsonObject.getInt("stockoutqty");
                            double initialqty = jsonObject.getDouble("initialqty");
                            double prate = jsonObject.getDouble("prate");
                            double salerate = jsonObject.getDouble("salerate");
                            double stockamount = jsonObject.getDouble("stockamount");
                            double saleamount = jsonObject.getDouble("saleamount");
                            String itemname = jsonObject.getString("itemname");

                            lotsModelArrayList = new ArrayList<>();
                            JSONArray jsonArrayLots = jsonObject.getJSONArray("lots");
                            for (int j = 0; j < jsonArrayLots.length(); j++) {
                                JSONObject jsonObject1 = jsonArrayLots.getJSONObject(j);
                                String lotno = jsonObject1.getString("lotno");
                                String sku = jsonObject1.getString("sku");
                                String qty = jsonObject1.getString("qty");
                                String expirydate = jsonObject1.getString("expirydate");
                                String last_purchaserate = jsonObject1.getString("last_purchaserate");
                                String lastlot_purchaserate = jsonObject1.getString("lastlot_purchaserate");
                                lotsModelArrayList.add(new LotsModel(lotno, sku, qty, expirydate, last_purchaserate, lastlot_purchaserate));
                            }
                            stockReportModelList.add(new StockReportModel(orgno, itemno, reorderpoint, remainingqty, stockinqty, stockoutqty,
                                    initialqty, prate, salerate, stockamount, saleamount, itemname, lotsModelArrayList));
                        }
                        mStockAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(StockReportActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(StockReportActivity.this, "JSONExp: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StockReportActivity.this, "VolleyError : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapLoadStocks);
    }

    private void mHandleAppBar() {
        this.setSupportActionBar(toolbar);
        this.setTitle("Stock reports");
    }

    private void getProducts() {
        dialogProductSearch = new Dialog(StockReportActivity.this);
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
                        Toast.makeText(StockReportActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

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


                                AppController.getAppController().getAppNetworkController().makeRequest(GET_ITEMS_URL, new Response.Listener<String>() {
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
                                                Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(StockReportActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

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


                                            AppController.getAppController().getAppNetworkController().makeRequest(GET_ITEMS_URL, new Response.Listener<String>() {
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
                                                        } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                            progressBar2.setVisibility(View.GONE);
                                                            Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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


    }

    private void getCategory() {
        dialogCategorySearch = new Dialog(StockReportActivity.this);
        dialogCategorySearch.setContentView(R.layout.dialog_stock_category_search_layout);

        WindowManager.LayoutParams wmlp1 = dialogCategorySearch.getWindow().getAttributes();
                /*wmlp1.gravity = Gravity.BOTTOM | Gravity.LEFT;
                wmlp1.x = 30;
                wmlp1.y = 230;*/
        wmlp1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        dialogCategorySearch.getWindow().setLayout(650, 800);
//                dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialogCategorySearch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCategorySearch.show();
        dialogCategorySearch.setCancelable(true);


        EditText editTextProduct = dialogCategorySearch.findViewById(R.id.edtTxt_catSearch);
        RecyclerView recyclerViewCategorySearch = dialogCategorySearch.findViewById(R.id.lv_searchableProductView);
        RecyclerView recyclerViewFilteredCategory = dialogCategorySearch.findViewById(R.id.lv_filteredProductView);
        ProgressBar progressBar = dialogCategorySearch.findViewById(R.id.progressBar);
        recyclerViewCategorySearch.setHasFixedSize(true);
        recyclerViewFilteredCategory.setHasFixedSize(true);
        recyclerViewCategorySearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFilteredCategory.setLayoutManager(new LinearLayoutManager(this));

        mGetCategoryList(recyclerViewCategorySearch, progressBar);


        recyclerViewCategorySearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading3) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == categoryModelArrayList.size() - 1) {
                        //bottom of list! and start loading
                        categoryModelArrayList.add(new CategoryModel(null, null));
//                                productsArrayList.add(new Products(null, null, null, null, null));
                        rvAdapterCategorySearch.notifyItemInserted(categoryModelArrayList.size());
                        catPageNo++;
                        Toast.makeText(StockReportActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                categoryModelArrayList.remove(categoryModelArrayList.size() - 1);
                                int scrollPosition = categoryModelArrayList.size();
                                rvAdapterCategorySearch.notifyItemRemoved(scrollPosition);
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
                                mapForScroll.put(CAT_PAGE_NO, String.valueOf(catPageNo));
                                mapForScroll.put(LIMIT, String.valueOf(10));

                                AppController.getAppController().getAppNetworkController().makeRequest(CATEGORY_URL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                if (jsonArray.length() > 0) {
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject data = jsonArray.getJSONObject(i);
                                                        categoryModel = new CategoryModel(data.getString("id"), data.getString("text"));
                                                        categoryModelArrayList.add(categoryModel);
                                                    }
                                                }/* else
                                                            Toast.makeText(FullScannerActivitySale.this, "No more data to load", Toast.LENGTH_SHORT).show();
*/
                                                rvAdapterCategorySearch.notifyDataSetChanged();
                                                isLoading3 = false;
                                            } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                recyclerViewCategorySearch.setVisibility(View.VISIBLE);
                recyclerViewFilteredCategory.setVisibility(View.GONE);

                filterCategory(s.toString());

                if (s.length() >= 3) {
                    ArrayList<CategoryModel> finalFilteredArray = new ArrayList<>();
                    mGetFilteredCatList(s, progressBar, recyclerViewFilteredCategory, recyclerViewCategorySearch, finalFilteredArray);

                    recyclerViewFilteredCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                            if (!isLoading4) {
                                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == categoryModelArrayList.size() - 1) {
                                    //bottom of list!
//                                            finalFilteredArray.add(new Products(null, null, null, null, null));
                                    finalFilteredArray.add(new CategoryModel(null, null));
                                    rvAdapterCategorySearch.notifyItemInserted(finalFilteredArray.size());
                                    catFilteredPageNo++;
                                    Toast.makeText(StockReportActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finalFilteredArray.remove(finalFilteredArray.size() - 1);
                                            int scrollPosition = finalFilteredArray.size();
                                            rvAdapterCategorySearch.notifyItemRemoved(scrollPosition);
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
                                            mapForScroll.put(CAT_PAGE_NO, String.valueOf(catFilteredPageNo));
                                            mapForScroll.put(LIMIT, String.valueOf(10));
                                            mapForScroll.put(CAT_SEARCH_KEY, s.toString());


                                            AppController.getAppController().getAppNetworkController().makeRequest(CATEGORY_URL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {

                                                        JSONObject jsonObject = new JSONObject(response);
                                                        if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                JSONObject data = jsonArray.getJSONObject(i);
                                                                CategoryModel categoryModel = new CategoryModel(data.getString("id"), data.getString("text"));
                                                                finalFilteredArray.add(categoryModel);
                                                            }
                                                            rvAdapterCategorySearch.notifyDataSetChanged();
                                                                    /*rvAdapterPersonSearch = new RvAdapterPersonSearch(finalFilteredArray, FullScannerActivity.this);
                                                                    recyclerViewPersonSearch.setAdapter(rvAdapterPersonSearch);*/
                                                            isLoading4 = false;

//                                                                    Toast.makeText(FullScannerActivitySale.this, "after: " + String.valueOf(finalFilteredArray.size()), Toast.LENGTH_SHORT).show();
                                                        } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                    mGetCategoryList(recyclerViewCategorySearch, progressBar);
                } else
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                        Toast.makeText(FullScannerActivity.this, "afterTextChanged: "+ s.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void mGetFilteredCatList(CharSequence s, ProgressBar progressBar, RecyclerView recyclerViewFilteredCategory, RecyclerView recyclerViewCategorySearch, ArrayList<CategoryModel> finalFilteredArray) {
        {
            catFilteredPageNo = 1;
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> mapGetCategroy = new HashMap<>();
            mapGetCategroy.put(CAT_PAGE_NO, String.valueOf(catFilteredPageNo));
            mapGetCategroy.put(LIMIT, String.valueOf(10));
            mapGetCategroy.put(CAT_SEARCH_KEY, s.toString());
            mapGetCategroy.put(API_KEY, apikey);
            AppController.getAppController().getAppNetworkController().makeRequest(CATEGORY_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
//                                            customerArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                progressBar.setVisibility(View.GONE);
                                recyclerViewFilteredCategory.setVisibility(View.VISIBLE);
                                recyclerViewCategorySearch.setVisibility(View.GONE);
                                JSONObject data = dataArray.getJSONObject(i);
                                categoryModel = new CategoryModel(data.getString("id"), data.getString("text"));
//                                                products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                finalFilteredArray.add(categoryModel);
                                rvAdapterCategorySearch = new RvAdapterCategorySearch(finalFilteredArray, StockReportActivity.this, productPageNo);
                                recyclerViewFilteredCategory.setAdapter(rvAdapterCategorySearch);

                            }
                        } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(StockReportActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }, mapGetCategroy);
        }
    }

    private void mGetCategoryList(RecyclerView recyclerViewCategorySearch, ProgressBar progressBar) {
        {
            catPageNo = 1;
            HashMap<String, String> mapGetCategory = new HashMap<>();
            mapGetCategory.put(CAT_PAGE_NO, String.valueOf(catPageNo));
            mapGetCategory.put(LIMIT, String.valueOf(10));
            mapGetCategory.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
            AppController.getAppController().getAppNetworkController().makeRequest(CATEGORY_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equalsIgnoreCase("false")) {
                            JSONArray catDataArray = jsonObject.getJSONArray("data");
                            categoryModelArrayList = new ArrayList<>();

                            for (int i = 0; i < catDataArray.length(); i++) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject catData = catDataArray.getJSONObject(i);
//                                    products = new Products(productData.getString("id"), productData.getString("sku"), productData.getString("lot_num"), productData.getString("text"), productData.getString("qty"));
                                categoryModel = new CategoryModel(catData.getString("id"), catData.getString("text"));
                                categoryModelArrayList.add(categoryModel);
                                rvAdapterCategorySearch = new RvAdapterCategorySearch(categoryModelArrayList, StockReportActivity.this, productPageNo);
                                recyclerViewCategorySearch.setAdapter(rvAdapterCategorySearch);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }, mapGetCategory);
        }
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

    private ArrayList<CategoryModel> filterCategory(String text) {
        ArrayList<CategoryModel> filteredList = new ArrayList<>();
//        ArrayList<ExampleItem> filteredList = new ArrayList<>();
        for (CategoryModel item : categoryModelArrayList) {
            if (item.getCatName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        rvAdapterCategorySearch.filterList(filteredList);
        return filteredList;
    }

    private void mGetProductList(RecyclerView recyclerViewProductSearch, ProgressBar progressBar2) {
        {
            productPageNo = 1;
            HashMap<String, String> mapGetProducts = new HashMap<>();
            mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productPageNo));
            mapGetProducts.put(LIMIT, String.valueOf(10));
            mapGetProducts.put(API_KEY, apikey);
//                mapGetProducts.put("itemno", "2");
            AppController.getAppController().getAppNetworkController().makeRequest(GET_ITEMS_URL, new Response.Listener<String>() {
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
                                rvAdapterProductSearch = new RvAdapterProductSearch(productsArrayList, StockReportActivity.this, productPageNo);
                                recyclerViewProductSearch.setAdapter(rvAdapterProductSearch);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar2.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    progressBar2.setVisibility(View.GONE);
                }
            }, mapGetProducts);
        }
    }

    private void mSpanTesting() {
        String currentText = textViewSpanTest.getText().toString();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(currentText);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF4CAF50),
                currentText.indexOf("(") + 1,
                currentText.length() - 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.replace(currentText.indexOf(":") + 2, currentText.indexOf(" ("),
                "800");
        textViewSpanTest.setText(spannableStringBuilder);
    }

    private void createPopupDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_popup, null);
        mSearchEditext = view.findViewById(R.id.inputedit_popup_category);
        mSearchListView = view.findViewById(R.id.listview_popup);


        dialogBuilder.setView(view);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

//        loadCategory(true);

/*
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategoryTextView.setText((String) parent.getItemAtPosition(position));
                alertDialog.cancel();
            }
        });
*/

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this,
                    myDateListener1, year, month, day);
        } else if (id == 2) {
            return new DatePickerDialog(this,
                    myDateListener2, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener1 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(tvDateFrom, arg1, arg2 + 1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(tvDateTo, arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(TextView textView, int year, int month, int day) {
        textView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    private void mGetFilteredProductList(CharSequence s, ProgressBar progressBar2, RecyclerView recyclerViewFilteredProduct, RecyclerView recyclerViewProductSearch, ArrayList<Products> finalFilteredArray) {
        productFilteredPageNo = 1;
        progressBar2.setVisibility(View.VISIBLE);
        HashMap<String, String> mapGetProducts = new HashMap<>();
        mapGetProducts.put(PRODUCT_PAGE_NO, String.valueOf(productFilteredPageNo));
        mapGetProducts.put(LIMIT, String.valueOf(10));
        mapGetProducts.put(PRODUCT_SEARCH_KEY, s.toString());
        mapGetProducts.put(API_KEY, apikey);
        AppController.getAppController().getAppNetworkController().makeRequest(GET_ITEMS_URL, new Response.Listener<String>() {
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
                            rvAdapterProductSearch = new RvAdapterProductSearch(finalFilteredArray, StockReportActivity.this, productPageNo);
                            recyclerViewFilteredProduct.setAdapter(rvAdapterProductSearch);

                        }
                    } else if (jsonObject.getString("error").equalsIgnoreCase("true")) {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(StockReportActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(StockReportActivity.this, "JSONException : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StockReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mapGetProducts);
    }

    @Override
    public void catClickHandler(String id, String name) {
        tvCategoryName.setText(name);
        catId = id;
        if (imgV_categorySearchCrossIcon.getVisibility() != View.VISIBLE) {
            imgV_categorySearchCrossIcon.setVisibility(View.VISIBLE);
            imgV_categorySearchDownIcon.setVisibility(View.GONE);
        }

        dialogCategorySearch.dismiss();
    }

    @Override
    public void dataParsedStockLots(ArrayList<LotsModel> lotsModelArrayList) {
        mStockLotsAdapterHandler(lotsModelArrayList);
    }

    private void mStockLotsAdapterHandler(ArrayList<LotsModel> lotsModelArrayList) {
        if (lotsModelArrayList.size() > 0) {
            stockLotsAdapter = new StockLotsAdapter(this, lotsModelArrayList, this);
            rvStockLotsList.setAdapter(stockLotsAdapter);
            stockLotsAdapter.notifyDataSetChanged();
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
    public void customerClickHandler(String id, String name) {

    }

    @Override
    public void productClickHandler(String name, String id) {
        tvProductName.setText(name);
        itemNo = id;
        if (imgV_productSearchCrossIcon.getVisibility() != View.VISIBLE) {
            imgV_productSearchCrossIcon.setVisibility(View.VISIBLE);
            imgV_productSearchDownIcon.setVisibility(View.GONE);
        }
        dialogProductSearch.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgV_productSearchDownIcon:
            case R.id.llProductSearch:
                getProducts();
                break;

            case R.id.imgV_categorySearchDownIcon:
            case R.id.llCategorySearch:
                getCategory();
                break;

            case R.id.tv_showStock:

                reorder = mGetReorderPts();
                mLoadStocks(catId, itemNo, skuNum, reorder,dateFrom, dateTo);

                break;

            case R.id.imgV_productSearchCrossIcon:
                tvProductName.setText("Search product...");
                itemNo = "-1";
                if (imgV_productSearchDownIcon.getVisibility() != View.VISIBLE) {
                    imgV_productSearchDownIcon.setVisibility(View.VISIBLE);
                    imgV_productSearchCrossIcon.setVisibility(View.GONE);
                }
                break;

            case R.id.imgV_categorySearchCrossIcon:
                tvCategoryName.setText("Select category...");
                catId = "-1";
                if (imgV_categorySearchDownIcon.getVisibility() != View.VISIBLE) {
                    imgV_categorySearchDownIcon.setVisibility(View.VISIBLE);
                    imgV_categorySearchCrossIcon.setVisibility(View.GONE);
                }
                break;

            case R.id.imagebtn_date_from:
            case R.id.linear_date_from:
                mEditDate("from");

                break;

            case R.id.imagebtn_date_to:
            case R.id.linear_date_to:
                mEditDate("to");
                break;

            case R.id.purchaseBillViewerDarkBg:
            case R.id.ll_discardBottomSheet:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            this.finish();
            super.onBackPressed();
        }
    }

    private void mEditDate(String str) {
        if (str.equalsIgnoreCase("from")) {
            int year = Integer.parseInt(tvDateFrom.getText().toString().substring(0, 4));
            int month = Integer.parseInt(tvDateFrom.getText().toString().substring(5, 7));
            int day = Integer.parseInt(tvDateFrom.getText().toString().substring(8));

            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            tvDateFrom.setText(simpleDateFormat.format(calendar.getTime()));
                        }
                    }, year, month - 1, day);
        } else if (str.equalsIgnoreCase("to")) {
            int year = Integer.parseInt(tvDateTo.getText().toString().substring(0, 4));
            int month = Integer.parseInt(tvDateTo.getText().toString().substring(5, 7));
            int day = Integer.parseInt(tvDateTo.getText().toString().substring(8));

            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            tvDateTo.setText(simpleDateFormat.format(calendar.getTime()));
                        }
                    }, year, month - 1, day);
        }
        Objects.requireNonNull(datePickerDialog).show();

    }

    private String mGetReorderPts() {
        String str = mReorderSpin.getSelectedItem().toString();
        if (str.equalsIgnoreCase("All"))
            return "-1";
        else if (str.equalsIgnoreCase("Reorder Level Red"))
            return "1";
        else if (str.equalsIgnoreCase("Reorder Level Yellow"))
            return "2";
//        else if (str.equalsIgnoreCase("Reorder Level Green"))
        else
            return "3";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_stock_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                if (llFilterLayout.getVisibility() == View.VISIBLE) {
                    llFilterLayout.setVisibility(View.GONE);
                } else
                    llFilterLayout.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}