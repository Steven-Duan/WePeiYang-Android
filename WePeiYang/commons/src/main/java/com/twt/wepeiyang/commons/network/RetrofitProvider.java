package com.twt.wepeiyang.commons.network;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.twt.wepeiyang.commons.utils.App;
import com.twt.wepeiyang.commons.utils.CommonPrefUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by retrox on 2017/1/25.
 */

public class RetrofitProvider {

    private Retrofit mRetrofit;

    private RetrofitProvider() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            if (message.startsWith("{")) {
                if (message.length() > 400) {
                    Logger.d(message); // 要不太他了 没法看了fuck
                } else {
                    Logger.json(message);
                }
            } else {
                Platform.get().log(INFO, message, null);
            }
        });

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        SignInterceptor signInterceptor = new SignInterceptor();

        OkHttpClient.Builder clientbuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(signInterceptor)
//                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new UaInterceptor())
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getApplicationContext());
        boolean isUseProxy = sharedPreferences.getBoolean("pref_use_proxy", false);
        if (isUseProxy) {
            try {
                clientbuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CommonPrefUtil.getProxyAddress(), CommonPrefUtil.getProxyPort())));
            } catch (Exception e) {
                Log.e("NetworkProxy", CommonPrefUtil.getProxyAddress() + ":" + CommonPrefUtil.getProxyPort());
                Toast.makeText(App.getApplicationContext(), "老哥你代理配烂了... " + CommonPrefUtil.getProxyAddress() + ":" + CommonPrefUtil.getProxyPort(), Toast.LENGTH_SHORT).show();
            }
        }

        OkHttpClient client = clientbuilder.build();

        boolean isUseHttps = sharedPreferences.getBoolean("pref_use_https", true);
        String baseurl;
        if (isUseHttps) {
            baseurl = "https://open.twtstudio.com/api/v1/";
        } else {
            baseurl = "http://open.twtstudio.com/api/v1/";
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseurl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        mRetrofit = builder.build();
    }


    private static class SingletonHolder {
        private static final RetrofitProvider INSTANCE = new RetrofitProvider();
    }

    public static Retrofit getRetrofit() {
        return SingletonHolder.INSTANCE.mRetrofit;
    }
}
