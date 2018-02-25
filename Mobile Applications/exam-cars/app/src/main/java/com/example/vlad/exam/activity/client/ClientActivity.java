package com.example.vlad.exam.activity.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vlad.exam.R;

import timber.log.Timber;

public class ClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_client);

        Button buttonAvailableCars = findViewById(R.id.buttonAvailableCars);
        Button buttonPurchasedCars = findViewById(R.id.buttonPurchasedCars);

        buttonAvailableCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        ClientActivity.this,
                        ClientAvailableCarsActivity.class
                );
                startActivity(intent);
            }
        });

        buttonPurchasedCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        ClientActivity.this,
                        ClientPurchasedCarsActivity.class
                );
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");
        Timber.d("YEEEEEEEEE");
    }
}
