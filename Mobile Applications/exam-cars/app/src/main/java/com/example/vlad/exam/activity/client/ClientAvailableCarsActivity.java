package com.example.vlad.exam.activity.client;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vlad.exam.R;
import com.example.vlad.exam.adapter.CarClientAdapter;
import com.example.vlad.exam.controller.Controller;
import com.example.vlad.exam.controller.MyCallback;
import com.example.vlad.exam.model.Car;
import com.example.vlad.exam.service.MyServiceConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class ClientAvailableCarsActivity extends AppCompatActivity implements MyCallback {

    private List<Car> carList = new ArrayList<>();
//    private RecyclerView recyclerView;
    private ListView listView;
    private CarClientAdapter mAdapter;

    private ProgressBar progressBar;

    private Controller controller;
    private WebSocket webSocket;

    @Override
    public void showError(String message) {
        progressBar.setVisibility(View.GONE);

        Snackbar.make(listView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reloadData();
                    }
                }).show();
    }

    @Override
    public void clear() {
        mAdapter.clear();
    }

    @Override
    public void onRequestSuccess() {
        mAdapter.setData(carList);
        Toast.makeText(
                ClientAvailableCarsActivity.this,
                "The list is now up to date.",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_client_available_cars);

        controller = Controller.getController(getApplicationContext());

        boolean wsInitiated = initWebSocket();
        if (!wsInitiated) {
            Toast.makeText(
                    this,
                    "Failed to ini web socket. Working without.",
                    Toast.LENGTH_SHORT
            ).show();
        }

        progressBar = findViewById(R.id.progressBar);
//        recyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.list_view_available_cars);

        mAdapter = new CarClientAdapter(this, carList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Car car = carList.get(i);

                new AlertDialog.Builder(ClientAvailableCarsActivity.this)
                        .setTitle("Wanna buy?")
                        .setMessage(car.getName())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressBar.setVisibility(View.VISIBLE);
                                controller.buyCar(
                                        car,
                                        progressBar,
                                        new MyCallback() {
                                            @Override
                                            public void showError(String message) {
                                                Snackbar.make(listView, message, Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("Dismiss", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // do nothing ...
                                                            }
                                                        })
                                                        .show();
                                            }

                                            @Override
                                            public void clear() {
                                                // ...
                                            }

                                            @Override
                                            public void onRequestSuccess() {
                                                Intent intent = new Intent(
                                                        ClientAvailableCarsActivity.this,
                                                        ClientPurchasedCarsActivity.class
                                                );
                                                startActivity(intent);
                                            }
                                        }
                                );
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(
                                        ClientAvailableCarsActivity.this,
                                        "Buy car canceled.",
                                        Toast.LENGTH_SHORT
                                ).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private boolean initWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(MyServiceConstants.WS_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                ClientAvailableCarsActivity.this,
                                "Successfully opened web socket connection.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                ClientAvailableCarsActivity.this,
                                "New data received via web socket.",
                                Toast.LENGTH_SHORT
                        ).show();
                        reloadData();
                    }
                });
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, final String reason) {
                super.onClosing(webSocket, code, reason);

                final int _code = code;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Timber.v("WebSocket closing. Code: " + Integer.toString(_code) + ", reason: " + reason);
                        Toast.makeText(
                                ClientAvailableCarsActivity.this,
                                "WebSocket closing. Code: " + Integer.toString(_code) + ", reason: " + reason,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                final int _code = code;
                final String _reason = reason;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Timber.v("WebSocket closed. Code: " + Integer.toString(_code) + ", reason: " + _reason);
                        Toast.makeText(
                                ClientAvailableCarsActivity.this,
                                "WebSocket closed. Code: " + Integer.toString(_code) + ", reason: " + _reason,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, final Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Timber.e("[ws::onFailure] " + t.getMessage());
                        Toast.makeText(
                                ClientAvailableCarsActivity.this,
                                "[webSocket::onFailure] " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }
        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");

        reloadData();
    }

    private void reloadData() {
        progressBar.setVisibility(View.VISIBLE);
        controller.getCars(progressBar, this, carList);
//        mAdapter.setData(carList);
    }
}
