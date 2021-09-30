package com.example.nasa;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    public static final String Main_Activity = "Selected Image Activity";
    private ListView myList;
    private MyListAdapter myAdapter;

    public static String title;
    public static String desc;
    public static String date = "";
    public static String startDate = "";
    public static Bitmap pic;
    private ProgressBar progressBar;
    public static final String shared_prefs = "sharedPrefs";
    private String email_address;
    public static final String text = "Picture of the day";
    ArrayList<Images> imageList  = new ArrayList<>();


    private static final String testToolbar = "Toolbar Activity";
    public static final String Gallery_Activity = "Gallery Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);


        //loadList();

        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.today);
        Log.d(testToolbar,"User started the toolbar activity");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        myList = (ListView) findViewById(R.id.imageList);

        // Set the adapter which will populate the list view
        myAdapter  = new MyListAdapter();
        Log.d(Gallery_Activity, "initiate MyListAdapter");
        myList.setAdapter(myAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Log.d(Gallery_Activity, "my list adapter is called");

        myList.setOnItemClickListener((parent, view, position, id) -> {
            String selected =((TextView) view.findViewById(R.id.titleView)).getText().toString();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_title))
                    .setMessage(getString(R.string.alert_message, selected ))
                    .setPositiveButton(getString(R.string.alert_yes), (d, which) -> {
                        addDataToDatabase("https://api.nasa.gov/planetary/apod?api_key=idVLDwPn5wyemaUVEmwZXizbFDUg8fVDmjn7t5hb&start_date=" + getDate()+ "&end_date="+ getDate());
                        Toast.makeText(MainActivity.this, getString(R.string.toast_alert_message) , Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.alert_no), null)
                    .create();

            dialog.show();
        });





    }

    private void addDataToDatabase(String url){
        MyOpener dbOpener = new MyOpener(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        ContentValues cValues = new ContentValues(2);
        cValues.put(MyOpener.col_url,url);
        db.insert(MyOpener.Table_name, null, cValues);


    }

    //setting up shared preferences
    public void saveList(){
        SharedPreferences sharedEmail = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedEmail.edit();
        editor.putString("title", this.title);
        //editor.putString("picture", this.pic.toString());
        editor.putString("description", this.desc);
        editor.apply();
        Log.d(Gallery_Activity, "list saved " + this.title);
    }

    public void loadList(){
        SharedPreferences sharedEmail = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        //setting the title
        setTitle(sharedEmail.getString("title", ""));
        setDesc(sharedEmail.getString("description", ""));
        Log.d(Gallery_Activity, "list loaded " + this.title);
    }

    public String getImgTitle() {
        return title;
    }

    public void setDesc(String desc) {
        Log.d(Gallery_Activity, "end desc is" + title);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setTitle(String title) {
        Log.d(Gallery_Activity, "end title is" + title);
        this.title = title;
    }

    public void setDate(String date) {
        Log.d(Gallery_Activity, "date is" + date);
        this.startDate = date;
    }

    public String getDate() {
        return startDate;
    }



    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        Log.d(Gallery_Activity, "end picture is" + pic.toString());
        this.pic = pic;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                Log.d(testToolbar, "home Item selected");
                break;

            case R.id.search:
                startActivity(new Intent(MainActivity.this, Search.class));
                Log.d(testToolbar, "search Item selected");
                break;

            case R.id.fav:
                startActivity(new Intent(MainActivity.this, Favorites.class));
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
                .setMessage(getString(R.string.main_help ))
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

            View view;
            view = inflater.inflate(R.layout.image_layout,parent,false);

            // Populate images data
            ((TextView) view.findViewById(R.id.titleView)).setText(img.title);
            ((TextView) view.findViewById(R.id.discView)).setText(img.desc);
            ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(img.picture);

            return view;
        }

        public class NasaQuery extends AsyncTask<String, Integer, String> {

            private String imgDate;
            private Bitmap picture;


            @Override
            protected void onProgressUpdate(Integer ... value){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(value[0]);

            }



            @Override
            protected void onPostExecute(String result) {
                //add the tours from internet to the array
                //startActivity(new Intent(prep.this, Gallery.class));


                Log.d(Main_Activity, "we are on post Execution");
                Log.d(Main_Activity, "post execution title is " + title);
                imageList.add(new Images(getImgTitle(),getDesc(),getPic()));
                Log.d(Gallery_Activity, "load data title " + getImgTitle());
                Log.d(Gallery_Activity, "load data desc " + getDesc());
                //Log.d(Gallery_Activity, "load data pic " + getPic().toString());
                MyListAdapter.this.notifyDataSetChanged();
                //startActivity(new Intent(Gallery.this, MainActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            protected String doInBackground(String ...args) {

                publishProgress(25);


                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()) {
                    Log.i(Main_Activity, "Device is connecting to the network");
                } else {
                    Log.i(Main_Activity, "Device is not connecting to the network");
                }
                publishProgress(50);
                try {

                    URL urlJson = new URL("https://api.nasa.gov/planetary/apod?api_key=idVLDwPn5wyemaUVEmwZXizbFDUg8fVDmjn7t5hb&date=" );

                    Log.d(Main_Activity, "the url with date is " + urlJson);

                    HttpURLConnection urlConnection  = (HttpURLConnection) urlJson.openConnection();

                    InputStream response = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d(Main_Activity, "the Json line is " + line);
                        String result = sb.toString();
                        JSONObject jObject = new JSONObject(result);
                        String title       = jObject.getString("title");
                        setTitle(title);
                        String date       = jObject.getString("date");
                        setDate(date);
                        String desc       = jObject.getString("explanation");

                        imgDate = jObject.getString( "url");
                        String mediaType       = jObject.getString("media_type");
                        switch(mediaType){
                            case "video":
                                imgDate = "https://cdn.mos.cms.futurecdn.net/8gzcr6RpGStvZFA2qRt4v6.jpg"; break;
                            default: imgDate = jObject.getString( "url");break;

                        }

                        setDesc(desc);

                        Log.d(Main_Activity, "the picture is " + imgDate.substring(imgDate.lastIndexOf(".")+1));

                        InputStream is;
                        is = new URL(imgDate).openStream();
                        picture = BitmapFactory.decodeStream(is);
                        Log.d(Main_Activity, "the picture is " + imgDate.toString());
                        setPic(picture);
                        saveList();
                    }
                    urlConnection.disconnect();
                    publishProgress(75);
                }
                catch (FileNotFoundException fne) {
                    Log.e(Main_Activity, fne.getMessage());
                }

                catch (IOException e) {
                    Log.e(Main_Activity, e.getMessage());
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

    }



}