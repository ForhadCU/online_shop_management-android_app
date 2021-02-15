package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.CartStatusModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderReportTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mCtx;
    private List<CartStatusModel> mItemList;
    CartStatusModel mOrderReport ;

    public OrderReportTimelineAdapter(Context mCtx, List<CartStatusModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.order_summary_timeline_list, null);
        return new StockReportViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mOrderReport = mItemList.get(position);

        ((StockReportViewHolder) holder).bind(mOrderReport, position);



    }



    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class StockReportViewHolder extends RecyclerView.ViewHolder {

       ImageButton mImgBtnTimeline ;
       View mView ;
       TextView mTextTimelineName ;
        RecyclerView mTimelineRecycler ;
        private Handler handler = new Handler();
        private Runnable runnable;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        public StockReportViewHolder(View itemView) {
            super(itemView);

            mImgBtnTimeline = itemView.findViewById(R.id.image_btn_timeline);
            mView = itemView.findViewById(R.id.view_timeline);
            mTextTimelineName = itemView.findViewById(R.id.text_timeline);

            mTimelineRecycler = itemView.findViewById(R.id.recycler_timeline_order);




        }

        public void bind(CartStatusModel mOrderReport, int position) {

//            Log.e("TAG", "cart in Timeline adapter::: "+position+" --  "+ mOrderReport);

            if(mItemList.size() > position){
                if(mOrderReport.getPassed() == "true"){
                    mImgBtnTimeline.setImageResource(R.drawable.ic_baseline_check_circle);
                    mView.setBackgroundColor(Color.BLACK);
                }

                mTextTimelineName.setText(position+". "+mOrderReport.getStatustitle());
            }
        }

    }


}