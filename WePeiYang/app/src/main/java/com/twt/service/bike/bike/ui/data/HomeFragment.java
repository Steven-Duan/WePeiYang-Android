package com.twt.service.bike.bike.ui.data;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.twt.service.R;
import com.twt.service.bike.common.ui.PFragment;
import com.twt.service.bike.model.BikeUserInfo;
import com.twt.service.bike.utils.BikeStationUtils;
import com.twt.service.bike.utils.TimeStampUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by jcy on 2016/8/9.
 */

public class HomeFragment extends PFragment<HomePresenter> implements HomeViewController {
    @InjectView(R.id.srl_home)
    SwipeRefreshLayout mSrlHome;
    //自行车卡片
    @InjectView(R.id.leave_station)
    TextView mLeaveStation;
    @InjectView(R.id.leave_time)
    TextView mLeaveTime;
    @InjectView(R.id.arr_station)
    TextView mArrStation;
    @InjectView(R.id.arr_time)
    TextView mArrTime;
    @InjectView(R.id.bike_fee)
    TextView mBikeFeeText;
    @InjectView(R.id.bike_data_chart)
    LineChart mLineChart;

    ArrayList<String> xVals = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<Entry> yVals = new ArrayList<>();
    private LineDataSet mSet;

    private List<List<Integer>> mRecentData;

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter(getContext(), this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bike_data;
    }

    @Override
    protected void preInitView() {
        super.preInitView();
        mSrlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getBikeUserInfo();
            }
        });
        mSrlHome.post(new Runnable() {
            @Override
            public void run() {
                mPresenter.getBikeUserInfo();
            }
        });
    }

    @Override
    protected void initView() {
        mLineChart.setLogEnabled(true);
        mLineChart.setNoDataText("最近没有骑行数据");
        mLineChart.setTouchEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.animateX(100);
        mLineChart.setDrawBorders(false);
        mLineChart.setDescription("最近骑行记录(s) ");
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setAutoScaleMinMaxEnabled(true);
        mLineChart.setGridBackgroundColor(Color.BLACK);
        mLineChart.setBorderWidth(3);
        int[] colors = mLineChart.getLegend().getColors();
        int[] color = {android.R.color.holo_blue_dark};
        String[] names = {"最近骑行记录(s)"};
        mLineChart.getLegend().setCustom(color,names);
        mLineChart.animateY(2000, Easing.EasingOption.EaseInExpo);


    }

    @Override
    public void setBikeUserInfo(BikeUserInfo bikeUserInfo) {
        mSrlHome.setRefreshing(false);
        String dep = BikeStationUtils.getInstance().queryId(bikeUserInfo.record.dep).name;
        mLeaveStation.setText(dep + "-" + bikeUserInfo.record.dep_dev + "号桩");
        mLeaveTime.setText(TimeStampUtils.getDateString(bikeUserInfo.record.dep_time));
        String arr = BikeStationUtils.getInstance().queryId(bikeUserInfo.record.arr).name;
        mArrStation.setText(arr + "-" + bikeUserInfo.record.arr_dev + "号桩");
        mArrTime.setText(TimeStampUtils.getDateString(bikeUserInfo.record.arr_time));
        mBikeFeeText.setText("本次消费:" + bikeUserInfo.record.fee + "账户余额:" + bikeUserInfo.balance);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary_color));
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(7);
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMaxValue(4f);
        yAxis.setStartAtZero(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);

        mRecentData = bikeUserInfo.recent;
        float max = 0;
        float min = 0;
        for (int i = 0; i < mRecentData.size(); i++) {
            List<Integer> item = mRecentData.get(i);
            Integer val = item.get(1);
            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }
            xVals.add(String.valueOf(item.get(0))+"时");
            yVals.add(new Entry(item.get(1),i));
            colors.add(ContextCompat.getColor(getContext(), R.color.gpa_primary_color));
        }
        yAxis.setAxisMaxValue(max);
        yAxis.setAxisMinValue(min);
        mSet = new LineDataSet(yVals,null);
        mSet.setValueTextSize(10f);
        mSet.setLineWidth(3f);
        LineData data = new LineData(xVals,mSet);
        mLineChart.setData(data);
        mLineChart.setExtraLeftOffset(15);
        mLineChart.setExtraRightOffset(20);
        mLineChart.setExtraTopOffset(15);
        mLineChart.setExtraBottomOffset(15);
    }
}
