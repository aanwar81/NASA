package com.example.nasa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button search;
    private EditText startDt;
    private EditText endDt;
    public static String date = "";
    public static String endDate = "";

    private static final String[] paths = {"1", "2", "3", "4", "5", "6", "7"};

    public static final String Main_Activity = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startDt = findViewById(R.id.start_date);
        startDt.setText("2021-09-01");
        endDt = findViewById(R.id.end_date);
        endDt.setText("2021-09-09");

        search= (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Main_Activity, "Search button was clicked");
                setStartDate(startDt.getText().toString());
                setEndDate(endDt.getText().toString());
                startActivity(new Intent(MainActivity.this, Gallery.class));
            }
        });

    }

    public void setStartDate(String date) {
        Log.d(Main_Activity, "start date is" + date);
        this.date = date;
    }

    public void setEndDate(String date) {
        Log.d(Main_Activity, "end date is" + date);
        this.endDate = date;
    }






}