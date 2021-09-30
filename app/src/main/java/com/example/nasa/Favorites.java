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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Favorites extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    public static final String Main_Activity = "prep";
    private static final String testToolbar = "Toolbar Activity";
    private ListView myList;
    private MyListAdapter myAdapter;
    protected static final String Nasa_query = "Favorites";
    public static String title;
    public static String desc;
    public static String date = "";
    public static String selectedDate;
    public static Bitmap pic;
    private ProgressBar progressBar;
    ArrayList<Images> imageList  = new ArrayList<>();



    public static final String Gallery_Activity = "Gallery Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_favorites);

        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.fav);
        Log.d(testToolbar,"User started the toolbar activity");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        myList = (ListView) findViewById(R.id.favList);

        // Set the adapter which will populate the list view
        myAdapter = new MyListAdapter();
        Log.d(Gallery_Activity, "initiate MyListAdapter");
        myList.setAdapter(myAdapter);

        progressBar = (ProgressBar) findViewById(R.id.favProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        //myList.setAdapter((ListAdapter) (myAdapter  = new MyListAdapter()));
        Log.d(Gallery_Activity, "my list adapter is called");

        MainActivity main = new MainActivity();
        Log.d(Main_Activity, "the main date is " + main.date);

        Log.d(Main_Activity, "the gallery date is " + date);

        myList.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.list_delete))
                    .setMessage(getString(R.string.delete_message))
                    .setPositiveButton(getString(R.string.alert_yes), (d, which) -> {

                        // remove from database
                        deleteDataFromDatabase(position,id);
                         /*
                        if(frameLayout!=null){
                            frameLayout.removeAllViewsInLayout();
                        }

                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
                        DetailsFragment messageFragment = new DetailsFragment();
                        getFragmentManager().beginTransaction().remove().commit();

                         */

                    })
                    .setNegativeButton(getString(R.string.alert_no), null)
                    .create();

            dialog.show();
            return true;
        });

        myList.setOnItemClickListener((parent, view, position, id) -> {
            selectedDate=((TextView) view.findViewById(R.id.discViewGal)).getText().toString();
            Log.d(Main_Activity, "the favorite date is " + selectedDate);

            SelectedImage selectedImage = new SelectedImage();
            selectedImage.setDate(selectedDate);
            startActivity(new Intent(Favorites.this, SelectedImage.class));
            Images img = imageList.get(position);

            String message =  getString(R.string.clicked) + " " + selectedDate;
            Toast.makeText(Favorites.this, message, Toast.LENGTH_LONG).show();
        });

    }

    public void deleteDataFromDatabase(int position, long id){
        MyOpener dbOpener = new MyOpener(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        db.delete(MyOpener.Table_name, MyOpener.col_id + " = ?", new String[] {Long.toString(id)});

    }

    public void setDate(String date) {
        Log.d(Main_Activity, "end date is" + date);
        this.date = date;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                startActivity(new Intent(Favorites.this, MainActivity.class));
                Log.d(testToolbar, "home Item selected");
                break;

            case R.id.search:
                startActivity(new Intent(Favorites.this, Search.class));
                Log.d(testToolbar, "search Item selected");
                break;

            case R.id.fav:
                //startActivity(new Intent(Gallery.this, Favorites.class));
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
                .setMessage(getString(R.string.fav_help ))
                .setPositiveButton(getString(R.string.help_ok), (d, which) -> {
                })
                .create();

        dialog.show();
        return true;
    }


    public  class MyListAdapter extends BaseAdapter {


        public MyListAdapter() {
            Log.d(Gallery_Activity, "calling MyListAdapter constructor");
            NasaQuery nasaQuery = new NasaQuery();
            nasaQuery.execute();

        }




        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return (long) position;
        }



        @Override
        public View getView(int position, View old, ViewGroup parent) {
            Log.d(Gallery_Activity, "my list adapter is inflated");
            LayoutInflater inflater = getLayoutInflater();
            Images img = imageList.get(position);

            // Depending if the message is sent or received, load the correct template
            //View view = inflater.inflate((msg.isSent) ? R.layout.row_send : R.layout.row_recieve, parent, false);
            View view;
            view = inflater.inflate(R.layout.gallery_layout,parent,false);

            // Populate images data
            ((TextView) view.findViewById(R.id.titleViewGal)).setText(img.title);
            ((TextView) view.findViewById(R.id.discViewGal)).setText(img.desc);
            //((ImageView) view.findViewById(R.id.imageView)).setImageResource(img.imgId);
            ((ImageView) view.findViewById(R.id.imageViewGal)).setImageBitmap(img.picture);

            return view;
        }

        public class NasaQuery extends AsyncTask<String, Integer, String> {


            private String UV;
            private String minTemp;
            private String maxTemp;
            private String imgDate;
            private Bitmap picture;
            private String startDate = "";
            private String endDate = "";


            @Override
            protected void onProgressUpdate(Integer ... value){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(value[0]);

            }



            @Override
            protected void onPostExecute(String result) {
                //add the tours from internet to the array
                //startActivity(new Intent(prep.this, Gallery.class));


                Log.d(Nasa_query, "we are on post Execution");
                Log.d(Nasa_query, "post execution title is " + title);


                MyListAdapter.this.notifyDataSetChanged();
                //startActivity(new Intent(Gallery.this, MainActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            protected String doInBackground(String ...args) {

                startDate = MainActivity.date;

                if (TextUtils.isEmpty(startDate)) {
                    startDate = "2021-09-01";
                }

                endDate = Search.endDate;

                if (TextUtils.isEmpty(endDate)) {
                    endDate = "2021-09-03";
                }


                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    Log.i(Nasa_query, "Device is connecting to the network");
                } else {
                    Log.i(Nasa_query, "Device is not connecting to the network");
                }
                publishProgress(20);
                try {

                    MyOpener dbOpener = new MyOpener(Favorites.this);
                    SQLiteDatabase db = dbOpener.getReadableDatabase();
                    //db.execSQL("delete from "+ MyOpener.Table_name);

                    Cursor count = db.rawQuery("select count(*) from " + MyOpener.Table_name, null);
                    count.moveToFirst();

                    Log.d(Nasa_query, "the cursor count is " + count.getInt(0) );

                    Cursor c = db.rawQuery("select * from " + MyOpener.Table_name, null);
                    int urlIndex = c.getColumnIndex(MyOpener.col_url);
                    int idIndex = c.getColumnIndex(MyOpener.col_id);
                    c.moveToFirst();
                    String url ="";
                    Log.d(Nasa_query, "the db count is " + c.getColumnCount() );

                    for(int ii =0;ii<count.getInt(0);ii++) {
                        Log.d(Nasa_query, "the urlIndex is " + urlIndex + ii);
                        url  = c.getString(urlIndex);

                        URL urlJson = new URL(url);

                        Log.d(Nasa_query, "the url with date is " + urlJson + ii);

                        HttpURLConnection urlConnection = (HttpURLConnection) urlJson.openConnection();

                        InputStream response = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                            Log.d(Nasa_query, "the Json line is " + line);
                            String result = sb.toString();
                            JSONArray jObject = new JSONArray(result);

                            for (int i = 0; i < jObject.length(); i++) {
                                Log.d(Nasa_query, "the Json line is " + i);
                                JSONObject actor = jObject.getJSONObject(i);
                                String type = actor.getString("media_type");
                                Log.d(Nasa_query, "the midea is  " + type);


                                String title = actor.getString("title");
                                String desc = actor.getString("date");
                                imgDate = actor.getString("url");
                                InputStream is;
                                is = new java.net.URL(imgDate).openStream();
                                picture = BitmapFactory.decodeStream(is);

                                imageList.add(new Images(title, desc, picture));
                                publishProgress(25 + (50 / jObject.length() * i));

                            }


                        }
                        urlConnection.disconnect();
                        c.moveToNext();
                    }
                    publishProgress(75);
                }
                catch (FileNotFoundException fne) {
                    Log.e(Nasa_query, fne.getMessage());
                }

                catch (IOException e) {
                    Log.e(Nasa_query, e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }
    }

    public static class Images  {
        private final String title;
        private final String desc;
        private Bitmap picture;

        private Images( String title, String desc, Bitmap picture) {
            this.desc = desc;
            this.title = title;
            this.picture = picture;
        }

        public String getTitle(){
            return this.title;
        }

    }



}