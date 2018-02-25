package com.example.vlad.exam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vlad.exam.activity.client.ClientActivity;
import com.example.vlad.exam.activity.employee.EmployeeActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_main);

        Button buttonClient = findViewById(R.id.buttonClient);
        Button buttonEmployee = findViewById(R.id.buttonEmployee);

        buttonClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        ClientActivity.class
                );
                startActivity(intent);
            }
        });

        buttonEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        MainActivity.this,
                        EmployeeActivity.class
                );
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");
    }
}
