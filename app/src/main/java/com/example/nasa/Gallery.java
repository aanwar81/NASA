package com.example.nasa;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class Gallery extends AppCompatActivity {
    public static final String Main_Activity = "prep";
    private ListView myList;
    private MyListAdapter myAdapter;
    protected static final String Nasa_query = "Nasa Query";
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
        setContentView(R.layout.activity_gallery);

        myList = (ListView) findViewById(R.id.galleryList);

        // Set the adapter which will populate the list view
        myAdapter = new MyListAdapter();
        Log.d(Gallery_Activity, "initiate MyListAdapter");
        myList.setAdapter(myAdapter);

        progressBar = (ProgressBar) findViewById(R.id.galleryProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        //myList.setAdapter((ListAdapter) (myAdapter  = new MyListAdapter()));
        Log.d(Gallery_Activity, "my list adapter is called");

        MainActivity main = new MainActivity();
        Log.d(Main_Activity, "the main date is " + main.date);
        setDate(main.date);
        Log.d(Main_Activity, "the gallery date is " + date);

        myList.setOnItemClickListener((parent, view, position, id) -> {
            selectedDate=((TextView) view.findViewById(R.id.discViewGal)).getText().toString();
            startActivity(new Intent(Gallery.this, SelectedImage.class));
            Images img = imageList.get(position);

            String message =  getString(R.string.clicked) + " " + selectedDate;
            Toast.makeText(Gallery.this, message, Toast.LENGTH_LONG).show();
        });

    }

    public void setDate(String date) {
        Log.d(Main_Activity, "end date is" + date);
        this.date = date;
    }

    public  String getDate() {
        Log.d(Main_Activity, "the get date method returns " + this.date);
        return this.date;

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

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        Log.d(Gallery_Activity, "end picture is" + pic.toString());
        this.pic = pic;
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

                endDate = MainActivity.endDate;

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

                    URL urlJson = new URL("https://api.nasa.gov/planetary/apod?api_key=idVLDwPn5wyemaUVEmwZXizbFDUg8fVDmjn7t5hb&start_date="+startDate+"&end_date="+endDate);

                    Log.d(Nasa_query, "the url with date is " + urlJson);

                    HttpURLConnection urlConnection  = (HttpURLConnection) urlJson.openConnection();

                    InputStream response = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        Log.d(Nasa_query, "the Json line is " + line);
                        String result = sb.toString();
                        JSONArray jObject = new JSONArray(result);

                        for (int i=0; i<jObject.length(); i++) {
                            Log.d(Nasa_query, "the Json line is " + i);
                            JSONObject actor = jObject.getJSONObject(i);
                            String type = actor.getString("media_type");
                            Log.d(Nasa_query, "the midea is  " + type);


                                String title = actor.getString("title");
                                String desc = actor.getString("date");
                                imgDate = actor.getString( "url");
                                InputStream is;
                                is = new java.net.URL(imgDate).openStream();
                                picture = BitmapFactory.decodeStream(is);

                                imageList.add(new Images(title,desc,picture));
                                publishProgress(25 + (50/jObject.length()*i));

                        }


                    }
                    urlConnection.disconnect();
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