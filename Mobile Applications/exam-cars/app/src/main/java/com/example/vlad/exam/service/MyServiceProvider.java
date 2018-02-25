package com.example.vlad.exam.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vlad on 29.01.2018.
 */

public class MyServiceProvider {
    private static volatile MyService SERVICE = null;

    private MyServiceProvider() {
    }

    public static MyService getService() {
        if (null == SERVICE) {
            synchronized (MyServiceProvider.class) {
                if (null == SERVICE) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(MyServiceConstants.SERVICE_ENDPOINT)
                            .build();

                    SERVICE = retrofit.create(MyService.class);
                }
            }
        }

        return SERVICE;
    }
}
