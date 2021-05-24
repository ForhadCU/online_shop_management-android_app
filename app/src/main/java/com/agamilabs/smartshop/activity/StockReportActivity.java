package com.agamilabs.smartshop.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.StockReportAdapter;
import com.agamilabs.smartshop.model.StockReportModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.data.SingleRefDataBufferIterator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StockReportActivity extends AppCompatActivity {

    private String STOCK_URL = "http://192.168.0.105/android/AgamiLab/smart_shop/stock.json";
    private String CATEGORY_URL = "http://192.168.0.105/android/AgamiLab/smart_shop/category.json";
//    private String STOCK_URL = "http://192.168.1.3/android/AgamiLab/agami-logbook/view_section.php";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mStockRecyclerView;
    private List<StockReportModel> mStockList;
    private List<String> mCategoryList  ;

    private StockReportAdapter mStockAdapter ;
    private GridLayoutManager manager ;
    private CardView mCardView ;
    private TextInputEditText mProductEdit;
    private TextView mDateFrom, mDateTo;
    private Button  mFilterBtn ;
    private TextView mCategoryTextView;
    private Spinner mReorderSpin ;
    private LinearLayout mDateFromLinear, mDateToLinear ;
    private ImageButton mImgBtnDF, mImgBtnDT;

    private Calendar calendar;
    private int year, month, day;

    //popup dialog
    TextInputEditText mSearchEditext ;
    ListView mSearchListView ;

    private AlertDialog.Builder dialogBuilder  ;
    private AlertDialog dialog;

    private int testLast = 0, lengthArray=0;
    private int totalItemCount, pastVisiblesItems,  visibleItemCount, page =1, previousTotal ;
    private boolean loading = true ;
    private ProgressBar mProgressbar;
    private TextView textViewSpanTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        setTitle("Stock Report");

        mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
        mCardView=findViewById(R.id.cardView_stock_report) ;
        mProductEdit = findViewById(R.id.edit_product_name);
        mCategoryTextView = findViewById(R.id.text_category);
        mReorderSpin = findViewById(R.id.spinner_reorder_point);
        mDateFromLinear = findViewById(R.id.linear_date_from);
        mDateToLinear = findViewById(R.id.linear_date_to);
        mDateFrom = findViewById(R.id.text_date_from);
        mDateTo = findViewById(R.id.text_date_to);
        mFilterBtn = findViewById(R.id.btn_filter);
        mImgBtnDF = findViewById(R.id.imagebtn_date_from);
        mImgBtnDT = findViewById(R.id.imagebtn_date_to);
        mProgressbar = findViewById(R.id.progressbar) ;
//        textViewSpanTest =findViewById(R.id.tv_testSpan);



//        mSpanTesting();



        dialogBuilder = new AlertDialog.Builder(this);
        mStockRecyclerView = findViewById(R.id.recycler_stock_report) ;
        mStockRecyclerView.setHasFixedSize(true);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        showDate(mDateFrom,year, month+1, day);
//        showDate(mDateTo,year, month+1, day);


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
        mCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupDialog();
            }
        });

        mStockList = new ArrayList<>() ;
        mCategoryList = new ArrayList<>() ;
        loadProducts("","","","","",0);
        loadCategory(false);


        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mStockList.clear();
                mCategoryList.clear();
                loadProducts("","","","","",0);
                loadCategory(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mStockAdapter.notifyDataSetChanged();
                    }
                }, 1000);


            }
        });




    }

    private void mSpanTesting() {
        String currentText = textViewSpanTest.getText().toString();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(currentText);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF4CAF50),
                currentText.indexOf("(")+1,
                currentText.length()-1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.replace(currentText.indexOf(":")+2, currentText.indexOf(" ("),
                "800");
        textViewSpanTest.setText(spannableStringBuilder);
    }

    private void createPopupDialog()
    {
        View view = getLayoutInflater().inflate(R.layout.layout_popup, null);
        mSearchEditext = view.findViewById(R.id.inputedit_popup_category) ;
        mSearchListView = view.findViewById(R.id.listview_popup) ;


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        loadCategory(true) ;

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategoryTextView.setText((String) parent.getItemAtPosition(position));
                dialog.cancel();
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this,
                    myDateListener1, year, month, day);
        }else if (id == 2) {
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
                    showDate(mDateFrom,arg1, arg2+1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(mDateTo,arg1, arg2+1, arg3);
                }
            };
    private void showDate(TextView textView, int year, int month, int day) {
        textView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }



    private void loadProducts(String item_name, String category_name, String reorder_point, String date_from, String date_to, int number) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, STOCK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "response123: "+ response) ;
                        mCardView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray stockArray = object.getJSONArray("report");

                            lengthArray = stockArray.length() ;
                            ForLoadMoreData(stockArray, number) ; //initial load data
//                            Log.e("TAG", "mStockList: "+ mStockList) ;

                            mStockAdapter = new StockReportAdapter(getApplicationContext(), mStockList);
                            mStockRecyclerView.setAdapter(mStockAdapter);
                            manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
                            mStockRecyclerView.setLayoutManager(manager);
                            mStockAdapter.notifyDataSetChanged();
                            loadMoreData(stockArray) ;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("item_name",  item_name );
//                parameters.put("category_name",  category_name );
//                parameters.put("reorder_point",  reorder_point );
//                parameters.put("date_from",  date_from );
//                parameters.put("date_to",  date_to );
//                return parameters;
//            }
//
//        };


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    private void ForLoadMoreData(JSONArray stockArray, int number) {
        if(number==lengthArray){
            return;
        }
        for(int i=number; i<number+ 5; i++){
            testLast = i ;
            JSONObject mStockObject = null;
            try {
                mStockObject = stockArray.getJSONObject(i);
                StockReportModel aStockModel = new StockReportModel() ;
                Field[] fields =  aStockModel.getAllFields() ;

                for(int j=0; j<fields.length; j++ ){
                    String fieldName = fields[j].getName() ;
                    String fieldValueInJson =mStockObject.has(fieldName)? mStockObject.getString(fieldName) : "" ;
                    try{
                        fields[j].set(aStockModel, fieldValueInJson) ;
                    }catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                mStockList.add(aStockModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    private void loadMoreData(final JSONArray array) {
        mStockRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0)
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-1) {
                            loading = false;
                            mProgressbar.setVisibility(View.VISIBLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadNextDataFromApi(array);
                                }
                            }, 1000);

                        }
                    }
                }
            }
        });
    }
    public void loadNextDataFromApi(JSONArray array) {
        loading = true;
        int j = testLast+1;
        ForLoadMoreData(array, j); //  2nd --- final load data
        mProgressbar.setVisibility(View.GONE);
        mStockAdapter.notifyDataSetChanged();
    }


    private void loadCategory(boolean dialogClick) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.e("TAG", "response123: "+ response) ;

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray mStockCategoryArray = object.getJSONArray("category");

                            for(int i=0;i<mStockCategoryArray.length();i++){
                                JSONObject mCategoryObject = mStockCategoryArray.getJSONObject(i);
                                mCategoryList.add(mCategoryObject.getString("itemname")) ;

                            }
                            if(dialogClick==true){
                                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, mCategoryList);
                                mSearchListView.setAdapter(adapter);
                            }else{
                                mCategoryTextView.setText(mCategoryList.get(0));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("item_name",  item_name );
//                parameters.put("category_name",  category_name );
//                parameters.put("reorder_point",  reorder_point );
//                parameters.put("date_from",  date_from );
//                parameters.put("date_to",  date_to );
//                return parameters;
//            }
//
//        };


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }




}