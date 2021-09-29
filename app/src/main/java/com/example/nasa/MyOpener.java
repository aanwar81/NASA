package com.example.nasa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper {
    protected final static String Database_name = "Nasa_db";
    protected final static int Version_num = 1;
    public final static String Table_name = "favorits";
    public final static String col_id = "_id";
    public final static String col_url = "pic_url";



    public MyOpener(Context ctx){super(ctx, Database_name, null, Version_num); }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("Create table " + Table_name + " (_id Integer Primary key autoincrement,"
        + col_url + " text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("Drop table if exists " + Table_name);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer){
        db.execSQL("DROP TABLE IF EXISTS " + Table_name);
        onCreate(db);
    }

}
