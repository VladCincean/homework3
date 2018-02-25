package com.example.vlad.exam.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ProgressBar;

import com.example.vlad.exam.model.Car;
import com.example.vlad.exam.model.ExceptionBody;
import com.example.vlad.exam.model.PurchasedCar;
import com.example.vlad.exam.room.AppDatabase;
import com.example.vlad.exam.service.MyService;
import com.example.vlad.exam.service.MyServiceProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by vlad on 29.01.2018.
 */

public class Controller {
    private static volatile Controller INSTANCE;
    private MyService service;
    private AppDatabase db;
    private Context context;

    private Controller(Context context) {
        this.context = context;
        db = AppDatabase.getInstance(context);
        service = MyServiceProvider.getService();
    }

    public static Controller getController(Context context) {
        if (null == INSTANCE) {
            synchronized (Controller.class) {
                if (null == INSTANCE) {
                    INSTANCE = new Controller(context);
                }
            }
        }

        return INSTANCE;
    }

    public boolean networkConnectivity() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert (manager != null);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }

    /**
     * a. (1p) View the available cars. Using GET /cars call, the client will receive the list of
     * cars available in the system. If offline the app will display an offline message and a
     * way to retry the connection and the call. For each car the name, quantity and the
     * type are displayed.
     */
    public void getCars(
            final ProgressBar progressBar,
            final MyCallback callback,
            final List<Car> carList
    ) {
        if (!networkConnectivity()) {
            Timber.d("[CarService::getCars] no internet connection.");
            callback.showError("No internet connection.");
            return;
        }

        service.getCars()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Car>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("[CarService - GET /cars] onCompleted.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("[CarService - GET /cars] onError.");
                        callback.showError("Not able to retrieve the data. Connection error.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(final List<Car> cars) {
                        Timber.d("[CarService - GET /cars] onNext.");
                        carList.clear();
                        carList.addAll(cars);
                        callback.onRequestSuccess();
                    }
                });
    }

    /**
     * b. (0.5p) Buy a car. The client will buy a car, if available, using the POST /buyCar call, by
     * specifying the car id and the quantity. Available online only.
     */
    public void buyCar(final Car car, final ProgressBar progressBar, final MyCallback callback) {
        if (!this.networkConnectivity()) {
            callback.showError("No internet connection.");
            return;
        }

        service.buyCar(car)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Car>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("[CarService - POST /buyCar] completed.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ResponseBody body = ((HttpException) e).response().errorBody();

                        if (body != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement json = null;
                            try {
                                json = parser.parse(body.string());
                                Gson gson = new Gson();
                                ExceptionBody exceptionBody = gson.fromJson(json, ExceptionBody.class);

                                String text = exceptionBody.getText();

                                Timber.e("[CarService - POST /buyCar] " + text);
                                callback.showError(text);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Timber.e("[CarService - POST /buyCar] error.");
                            callback.showError("Error. Couldn't buy the car.");
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Car car) {
                        Timber.d("[CarService - POST /buyCar] on next.");
                        final PurchasedCar purchasedCar = new PurchasedCar(
                                car.getId(),
                                car.getName(),
                                car.getType(),
                                new Date()
                        );

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.getPurchasedCarDao().insert(purchasedCar);
                                Timber.d("[db] purchased car inserted into db.");
                            }
                        }).start();

                        callback.onRequestSuccess();
                    }
                });
    }

    /**
     * c. (1p) Once the client purchased a car, the list of his cars will be displayed. The list is
     * persisted on the device, on the local storage, available offline too. The client can
     * return a car, from hist list, by doing a POST /returnCar call using the car id, operation
     * available only when online. Only in the first 30 days after the purchase.
     */
    public void returnCar(final int id, final ProgressBar progressBar, final MyCallback callback) {
        if (!this.networkConnectivity()) {
            callback.showError("No internet connection.");
            return;
        }

        CompletableFuture<PurchasedCar> completableFuture =
                CompletableFuture.supplyAsync(new Supplier<PurchasedCar>() {
            @Override
            public PurchasedCar get() {
                Timber.v("[db] select one - db id %d", id);
                return db.getPurchasedCarDao().selectOne(id);
            }
        });

        completableFuture.thenAccept(new Consumer<PurchasedCar>() {
            @Override
            public void accept(final PurchasedCar purchasedCar) {
                if (null == purchasedCar) {
                    Timber.e("[db] error. Cannot find a car with the given car id.");
                    callback.showError("Cannot find a car with the given car id.");
                    return;
                }

                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1);
                Date oneMonthAgo = calendar.getTime();

                if (oneMonthAgo.after(purchasedCar.getBuyDate())) {
                    callback.showError("Cannot return cars purchased more than 30 days ago.");
                    return;
                }

                final Car dummyCarToReturn = new Car();
                dummyCarToReturn.setId(purchasedCar.getCarId());

                service.returnCar(dummyCarToReturn)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Car>() {
                            @Override
                            public void onCompleted() {
                                Timber.v("[CarService - POST /returnCar] completed.");
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                ResponseBody body = ((HttpException) e).response().errorBody();

                                if (body != null) {
                                    JsonParser parser = new JsonParser();
                                    JsonElement json = null;
                                    try {
                                        json = parser.parse(body.string());
                                        Gson gson = new Gson();
                                        ExceptionBody exceptionBody = gson.fromJson(json, ExceptionBody.class);

                                        String text = exceptionBody.getText();

                                        Timber.e("[CarService - POST /returnCar] " + text);
                                        callback.showError(text);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    Timber.e("[CarService - POST /returnCar] error.");
                                    callback.showError("Error. Couldn't return the car.");
                                }

                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(Car car) {
                                Timber.v("[CarService - POST /returnCar] on next.");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.getPurchasedCarDao().delete(purchasedCar);
                                        Timber.v("[db] purchased car successfully deleted from the db.");
                                    }
                                }).start();
                                callback.onRequestSuccess();
                            }
                        });
            }
        });
    }

    /**
     * e. (0.5p) Remove all the cars from the local persisted list.
     */
    public void removeAllCars(final ProgressBar progressBar, final MyCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getPurchasedCarDao().deleteAll();
                Timber.v("[db] all purchased cars were successfully removed from the db.");
            }
        }).start();
        progressBar.setVisibility(View.GONE);
        callback.onRequestSuccess();
    }

    /**
     * a. (1p) The list of all available cars. The list will be retrieved using the GET /all call,
     * in this list along with the name, quantity and type, the app will display the status also.
     */
    public void getAllCars(
            final ProgressBar progressBar,
            final MyCallback callback,
            final List<Car> carList
    ) {
        if (!networkConnectivity()) {
            Timber.d("[CarService::getAllCars] no internet connection.");
            callback.showError("No internet connection.");
            return;
        }

        service.getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Car>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("[CarService - GET /all] onCompleted.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("[CarService - GET /all] onError.");
                        callback.showError("Not able to retrieve the data. Connection error.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(final List<Car> cars) {
                        Timber.d("[CarService - GET /all] onNext.");
                        carList.clear();
                        carList.addAll(cars);
                        callback.onRequestSuccess();
                    }
                });
    }

    /**
     * b. (1p) Add a car. Using a POST /addCar call, by sending the car object a new car will
     * be added to the store list, on success the server will return the car object with the id
     * field set
     */
    public void addCar(
            final String name,
            final String type,
            final ProgressBar progressBar,
            final MyCallback callback
    ) {
        if (!this.networkConnectivity()) {
            callback.showError("No internet connection.");
            return;
        }

        Car car = new Car();
        car.setName(name);
        car.setType(type);

        service.addCar(car)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Car>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("[CarService - POST /addCar] onCompleted.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("[CarService - POST /addCar] onError.");

                        ResponseBody body = ((HttpException) e).response().errorBody();

                        if (body != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement json = null;
                            try {
                                json = parser.parse(body.string());
                                Gson gson = new Gson();
                                ExceptionBody exceptionBody = gson.fromJson(json, ExceptionBody.class);

                                String text = exceptionBody.getText();

                                Timber.e("[CarService - POST /addCar] " + text);
                                callback.showError(text);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onNext(Car car) {
                        Timber.d("[CarService - POST /addCar] onNext.");
                        callback.onRequestSuccess();
                    }
                });
    }

    /**
     * c. (1p) Delete a car. Using DELETE /removeCar call, by sending a valid car id, the
     * server will remove the car. On success 200 OK status will be returned.
     */
    public void removeCar(final int id, final ProgressBar progressBar, final MyCallback callback) {
        if (!this.networkConnectivity()) {
            callback.showError("No internet connection.");
            return;
        }

        Car car = new Car();
        car.setId(id);

        service.removeCar(car)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Car>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("[CarService - DELETE /removeCar] onCompleted.");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("[CarService - DELETE /removeCar] onError.");

                        ResponseBody body = ((HttpException) e).response().errorBody();

                        if (body != null) {
                            JsonParser parser = new JsonParser();
                            JsonElement json = null;
                            try {
                                json = parser.parse(body.string());
                                Gson gson = new Gson();
                                ExceptionBody exceptionBody = gson.fromJson(json, ExceptionBody.class);

                                String text = exceptionBody.getText();

                                Timber.e("[CarService - DELETE /removeCar] " + text);
                                callback.showError(text);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onNext(Car car) {
                        Timber.d("[CarService - DELETE /removeCar] onError.");
                        callback.onRequestSuccess();
                    }
                });
    }
}
