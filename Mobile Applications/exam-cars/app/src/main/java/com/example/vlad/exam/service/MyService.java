package com.example.vlad.exam.service;

import com.example.vlad.exam.model.Car;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by vlad on 29.01.2018.
 */

public interface MyService {

    // ---------- Client APIs ----------

    @GET("cars")
    Observable<List<Car>> getCars();

    @POST("buyCar")
    Observable<Car> buyCar(@Body Car car);

    @POST("returnCar")
    Observable<Car> returnCar(@Body Car car);

    // ---------- Employee APIs ----------

    @GET("all")
    Observable<List<Car>> getAll();

    @POST("addCar")
    Observable<Car> addCar(@Body Car car);

    @POST("removeCar")
    Observable<Car> removeCar(@Body Car car);
}
