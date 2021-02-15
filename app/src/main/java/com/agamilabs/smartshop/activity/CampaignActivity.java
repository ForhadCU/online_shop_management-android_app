package com.agamilabs.smartshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.agamilabs.smartshop.Interfaces.CallBack;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.CampaignAdapter;
import com.agamilabs.smartshop.adapter.CampaignStatisticsAdapter;
import com.agamilabs.smartshop.model.CampaignItem;
import com.agamilabs.smartshop.model.CampaignStatItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CampaignActivity extends AppCompatActivity implements CallBack {
    private RecyclerView rvCampaign,rvCampaignStat;
    private String urlCampaign = "http://192.168.0.105/smartShop/campaign.json";
    private String urlCampaignStatistics = "http://192.168.0.105/smartShop/campaign_statistics_2.json";
    private String urlCampaignStatusChange = "http://192.168.0.119/smartShop/campaign_statistics.json";
    private ArrayList<CampaignItem> mCampaignList;
    private ArrayList<CampaignStatItem> mCampaignStatList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mRequestQueueCampaign,mRequestQueueCampaignStat,mRequestQueueCampaignChangeStatus;
    private CampaignAdapter mCampaignAdapter;
    private CampaignStatisticsAdapter mCampaignStatAdapter;
    LinearLayoutManager managerCampaign;
    GridLayoutManager managerStat;
    private NestedScrollView scroller;
    private int lengthArrayStat,testLastCampaign,testLastStat,lengthArrayCampaign;
    private String running="CampaignStat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        rvCampaign = findViewById(R.id.recycler_view_campaign);
        rvCampaignStat = findViewById(R.id.recycler_view_campaign_stat);
        scroller = findViewById(R.id.nestedScroll);


        mCampaignList = new ArrayList<CampaignItem>();
        mCampaignStatList = new ArrayList<CampaignStatItem>();

        mRequestQueueCampaign = Volley.newRequestQueue(this);
        mRequestQueueCampaignStat = Volley.newRequestQueue(this);
        mRequestQueueCampaignChangeStatus = Volley.newRequestQueue(this);

        rvCampaign.setHasFixedSize(true);
        rvCampaign.setLayoutManager(new LinearLayoutManager(this));

        rvCampaignStat.setHasFixedSize(true);
        rvCampaignStat.setLayoutManager(new LinearLayoutManager(this));

        setTitle("Campaign");

        swipeRefreshLayout.setColorSchemeResources(R.color.darkblue);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCampaignList.clear();
                mCampaignStatList.clear();
                loadCampaignStat(0);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        mCampaignAdapter.notifyDataSetChanged();
                        mCampaignStatAdapter.notifyDataSetChanged();
                    }
                }, 1000);

            }
        });

        loadCampaignStat(0);

    }

    private void loadCampaignStat(final int number) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlCampaignStatistics, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error") && !response.getBoolean("error")) {

                                JSONArray jsonArray = response.getJSONArray("data");

                                lengthArrayStat = jsonArray.length() ;

                                LoadMoreDataStat(jsonArray,number);

                                rvCampaignStat.setHasFixedSize(true);
                                managerStat = new GridLayoutManager(CampaignActivity.this, 2, GridLayoutManager.VERTICAL, false);
                                managerStat.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        if (mCampaignStatList.size() % 2 != 0) {
                                            return (position == mCampaignStatList.size() - 1) ? 2 : 1;
                                        } else {
                                            return 1;
                                        }
                                    }
                                });
                                rvCampaignStat.setLayoutManager(managerStat);
                                mCampaignStatAdapter = new CampaignStatisticsAdapter(CampaignActivity.this, mCampaignStatList);
                                rvCampaignStat.setAdapter(mCampaignStatAdapter);
                                mCampaignStatAdapter.notifyDataSetChanged();

                                if(running.equals("CampaignStat")){
                                    loadNextPageStat(jsonArray);
                                }

                            }
                            else {
                                Toast.makeText(CampaignActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CampaignActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueueCampaignStat.add(request);
    }

    private void LoadMoreDataStat(JSONArray jsonArray, int number) {

        for (int i = number ; i<number+16 ; i++) {

            testLastStat = i ;

            if(i == lengthArrayStat){
                loadCampaign(0);
                running = "Campaign";
                break;
            }

            JSONObject exampleJson = null;
            try {
                exampleJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CampaignStatItem campaignstatModel = new CampaignStatItem();

            Field[] fields = campaignstatModel.getClass().getDeclaredFields();

            for(int j=0;j<fields.length;j++){
                String fieldName = fields[j].getName();
                String fieldValueInJson = null;
                try {
                    fieldValueInJson = exampleJson.getString(fieldName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fields[j].set(campaignstatModel,fieldValueInJson);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            mCampaignStatList.add(campaignstatModel);
        }

    }

    private void loadCampaign(final int number) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlCampaign, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error") && !response.getBoolean("error")) {
                                JSONArray jsonArray = response.getJSONArray("data");

                                lengthArrayCampaign = jsonArray.length() ;

                                LoadMoreDataCampaign(jsonArray,number);

                                managerCampaign = new LinearLayoutManager(CampaignActivity.this,LinearLayoutManager.VERTICAL,false);
                                rvCampaign.setLayoutManager(managerCampaign);
                                mCampaignAdapter = new CampaignAdapter(CampaignActivity.this, mCampaignList,CampaignActivity.this);
                                rvCampaign.setAdapter(mCampaignAdapter);
                                mCampaignAdapter.notifyDataSetChanged();

                                loadNextPageCampaign(jsonArray);
                            }
                            else {
                                Toast.makeText(CampaignActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CampaignActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueueCampaign.add(request);
    }

    private void LoadMoreDataCampaign(JSONArray jsonArray, int number) {

        for (int i = number; i < number+10 && i<lengthArrayCampaign ; i++) {

            testLastCampaign = i ;

            JSONObject exampleJson = null;
            try {
                exampleJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CampaignItem campaignModel = new CampaignItem();

            Field[] fields = campaignModel.getClass().getDeclaredFields();

            for(int j=0;j<fields.length;j++){
                String fieldName = fields[j].getName();
                String fieldValueInJson = null;
                try {
                    fieldValueInJson = exampleJson.getString(fieldName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fields[j].set(campaignModel,fieldValueInJson);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            mCampaignList.add(campaignModel);
        }
    }

    private void loadNextPageStat(JSONArray jsonArray) {
        if (scroller != null ) {
            scroller.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    int j = testLastStat+1;
                    LoadMoreDataStat(jsonArray, j);
                    mCampaignStatAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void loadNextPageCampaign(JSONArray jsonArray) {
        if (scroller != null) {
            scroller.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    int j = testLastCampaign + 1;
                    LoadMoreDataCampaign(jsonArray, j);
                    mCampaignAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void parseJSONChangeStatus(final String scheduleNo,final String status){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlCampaignStatusChange, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error") && !response.getBoolean("error")) {
                                 loadCampaignStat(0);
                            }
                            else {
                                Toast.makeText(CampaignActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CampaignActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                final HashMap<String, String> params = new HashMap<>();
                params.put("scheduleNo",scheduleNo);
                params.put("toStatus", status);
                return params;
            }
        };
        mRequestQueueCampaignChangeStatus.add(request);
    }

    @Override
    public void onClickButton(int position,String scheduleNo) {
        if(position == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to pause this campaign?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            parseJSONChangeStatus(scheduleNo,"0");
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else if(position == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to continue this campaign?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            parseJSONChangeStatus(scheduleNo,"1");
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else if(position == 2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to cancel this campaign?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            parseJSONChangeStatus(scheduleNo,"-1");
                        }
                    });

            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    @Override
    public void duplicateInfo(String schedule_no, String message_title, String message, String catid, String session, String apiid, String schedule_starting_datetime, String schedule_particular_number, String priority, String status, String schedule_entry_datetime,
                              String total_subscribers, String total_sent_subscribers) {
        mCampaignList.add(new CampaignItem(
                schedule_no,message_title, message, catid, session, apiid, schedule_starting_datetime,  schedule_particular_number,  priority,  status,  schedule_entry_datetime,
                total_subscribers,total_sent_subscribers
        ));
        mCampaignAdapter.notifyDataSetChanged();
        /*NewCampaignActivity.getActivityInstance().sendDuplicateInfo(schedule_no,message_title, message, catid, session, apiid, schedule_starting_datetime,  schedule_particular_number,  priority,  status,  schedule_entry_datetime,
                total_subscribers,  total_sent_subscribers);*/
    }
}