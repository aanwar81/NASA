package com.example.nasa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
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

public class Search extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Button search;
    private EditText startDt;
    private EditText endDt;
    public static String date = "";
    public static String endDate = "";

    private static final String testToolbar = "Toolbar Activity";
    public static final String Main_Activity = "Main Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_search);

        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.gallery_view);
        Log.d(testToolbar,"User started the toolbar activity");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
                startActivity(new Intent(Search.this, Gallery.class));
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


    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                startActivity(new Intent(Search.this, MainActivity.class));
                Log.d(testToolbar, "home Item selected");
                break;

            case R.id.search:

                Log.d(testToolbar, "search Item selected");
                break;

            case R.id.fav:
                startActivity(new Intent(Search.this, Favorites.class));
                Log.d(testToolbar, "favorites Item selected");
                break;


        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu items for use in the action bar
        Log.d(testToolbar,"User started the toolbar activity");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.the_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.search_help ))
                .setPositiveButton(getString(R.string.help_ok), (d, which) -> {
                })
                .create();

        dialog.show();
        return true;
    }

}