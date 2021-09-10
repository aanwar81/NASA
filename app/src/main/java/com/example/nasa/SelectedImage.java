package com.example.nasa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class SelectedImage extends AppCompatActivity {
    public static final String Main_Activity = "Selected Image Activity";
    private ListView myList;
    private MyListAdapter myAdapter;

    public static String title;
    public static String desc;
    public static String date = "";
    public static Bitmap pic;
    private ProgressBar progressBar;
    ArrayList<Images> imageList  = new ArrayList<>();



    public static final String Gallery_Activity = "Gallery Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_image);

        myList = (ListView) findViewById(R.id.imageList);

        // Set the adapter which will populate the list view
        myAdapter  = new MyListAdapter();
        Log.d(Gallery_Activity, "initiate MyListAdapter");
        myList.setAdapter(myAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Log.d(Gallery_Activity, "my list adapter is called");

        MainActivity main = new MainActivity();

        Log.d(Main_Activity, "the main date is " + main.date);
        setDate(main.date);
        Log.d(Main_Activity, "the gallery date is " + date);

        myList.setOnItemClickListener((parent, view, position, id) -> {
            String selected =((TextView) view.findViewById(R.id.titleView)).getText().toString();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_title))
                    .setMessage(getString(R.string.alert_message, selected ))
                    .setPositiveButton(getString(R.string.alert_yes), (d, which) -> {
                        Toast.makeText(SelectedImage.this, getString(R.string.toast_alert_message), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.alert_no), null)
                    .create();

            dialog.show();
        });

    }

    public void setDate(String date) {
        Log.d(Main_Activity, "end date is" + date);
        this.date = date;
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

            View view;
            view = inflater.inflate(R.layout.image_layout,parent,false);

            // Populate images data
             ((TextView) view.findViewById(R.id.titleView)).setText(img.title);
            ((TextView) view.findViewById(R.id.discView)).setText(img.desc);
            ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(img.picture);

            return view;
        }

        public class NasaQuery extends AsyncTask<String, Integer, String> {


            private String UV;
            private String minTemp;
            private String maxTemp;
            private String imgDate;
            private Bitmap picture;
            private String theDate = "";


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
                Log.d(Gallery_Activity, "load data pic " + getPic().toString());
                MyListAdapter.this.notifyDataSetChanged();
                //startActivity(new Intent(Gallery.this, MainActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            protected String doInBackground(String ...args) {

                theDate = Gallery.selectedDate;

                Log.d(Main_Activity, "the date in doinbackground is  " + theDate);
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

                    URL urlJson = new URL("https://api.nasa.gov/planetary/apod?api_key=idVLDwPn5wyemaUVEmwZXizbFDUg8fVDmjn7t5hb&date="+theDate);

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
                        String desc       = jObject.getString("explanation");
                        setDesc(desc);
                        imgDate = jObject.getString( "url");
                        InputStream is;
                        is = new URL(imgDate).openStream();
                        picture = BitmapFactory.decodeStream(is);
                        setPic(picture);
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