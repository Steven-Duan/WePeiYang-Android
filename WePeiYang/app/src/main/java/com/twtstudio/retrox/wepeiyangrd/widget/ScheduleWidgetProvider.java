package com.twtstudio.retrox.wepeiyangrd.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.twt.wepeiyang.commons.cache.CacheProvider;
import com.twt.wepeiyang.commons.network.RetrofitProvider;
import com.twt.wepeiyang.commons.network.RxErrorHandler;
import com.twt.wepeiyang.commons.utils.CommonPrefUtil;
import com.twtstudio.retrox.schedule.ScheduleActivity;
import com.twtstudio.retrox.schedule.TimeHelper;
import com.twtstudio.retrox.schedule.model.ClassTable;
import com.twtstudio.retrox.schedule.model.CourseHelper;
import com.twtstudio.retrox.schedule.model.ScheduleApi;
import com.twtstudio.retrox.schedule.model.ScheduleCacheApi;
import com.twtstudio.retrox.wepeiyangrd.R;
import com.twtstudio.retrox.wepeiyangrd.home.HomeActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.rx_cache.DynamicKey;
import io.rx_cache.EvictDynamicKey;
import io.rx_cache.Reply;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by retrox on 24/03/2017.
 */

public class ScheduleWidgetProvider extends AppWidgetProvider {

    private final CourseHelper helper = new CourseHelper();
    public static final SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    private AppWidgetManager manager = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_schedule);
            remoteViews.setOnClickPendingIntent(R.id.widget_framelayout, pendingIntent);
//            remoteViews.setOnClickPendingIntent(R.id.widget_listview,pendingIntent);

            remoteViews.setTextViewText(R.id.widget_today_date, getTodayString());

            getData(context, appWidgetId, remoteViews, false);

            manager = appWidgetManager;
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
    }

    private void setupList(Context context, int appWidgetId, RemoteViews remoteViews, ArrayList list) {
        Intent serviceIntent = new Intent(context, WidgetService.class);

        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        Hawk.put("scheduleCache", list);

        Intent startActivityIntent = new Intent(context, ScheduleActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context,0,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_listview,startActivityPendingIntent);

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);
        remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty_view);
        manager.updateAppWidget(appWidgetId, remoteViews);

    }

    public void getData(Context context, int appWidgetId, RemoteViews remoteViews, boolean update) {

        CacheProvider.getRxCache().using(ScheduleCacheApi.class)
                .getClassTableAuto(RetrofitProvider.getRetrofit().create(ScheduleApi.class)
                        .getClassTable(), new DynamicKey("classTable"), new EvictDynamicKey(update))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(reply -> Logger.d(reply.toString()))
                .map(Reply::getData)
                .subscribe(classTable -> {
                    //存入学期开始时间
                    CommonPrefUtil.setStartUnix(Long.valueOf(classTable.data.term_start));

                    List<ClassTable.Data.Course> courseList = helper.getTomorrowCourses(classTable);
                    for (int i = courseList.size() - 1; i >= 0; i--) {
                        //去除后面结尾的 "无" 空课程
                        if (courseList.get(i).coursename.equals("无")){
                            courseList.remove(i);
                        }else {
                            break;
                        }
                    }
                    setupList(context, appWidgetId, remoteViews, (ArrayList) courseList);

                }, new RxErrorHandler());

    }

    private String getTodayString() {
        StringBuilder stringBuilder = new StringBuilder();
        Observable.just(Calendar.getInstance())
                .map(Calendar::getTime)
                .map(dateFormate::format)
                .subscribe(stringBuilder::append);
        stringBuilder.append("  ");
        String s = "星期" + TimeHelper.getChineseCharacter(CourseHelper.getTodayNumber());
        stringBuilder.append(s);
        return stringBuilder.toString();
    }
}
