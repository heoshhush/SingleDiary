package com.example.singlediary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDatabase {
    private static final String TAG = "NoteDatabase";

    private static NoteDatabase database;
    public static String TABLE_NOTE = "NOTE";
    public static int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    private NoteDatabase(Context context){
        this.context = context;
    }

    public static NoteDatabase getInstance(Context context){
        if(database == null){
            database = new NoteDatabase(context);
        }

        return database;
    }

    public boolean open(){
        println("opening database [" + AppConstants.DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    };

    public void close(){
        db.close();

        database = null;
    }

    public Cursor rawQuery(String SQL){
        Cursor cursor = null;
        cursor = db.rawQuery(SQL, null);
        return cursor;
    }

    public boolean execSQL(String SQL){
        db.execSQL(SQL);
        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context){
            super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);

        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("drop table if exists " + TABLE_NOTE);

            String CREATE_SQL = "create table " + TABLE_NOTE + "("
                    + " _id integer not null PRIMARY KEY AUTOINCREMENT, "
                    + " weather text default '', "
                    + " address text default '', "
                    + " location_x text default '', "
                    + " location_y text default '', "
                    + " contents text default '', "
                    + " mood text, "
                    + " picture text default '', "
                    + " create_date timestamp default current_timestamp, "
                    + " modify_date timestamp default current_timestamp "
                    + ")";

            db.execSQL(CREATE_SQL);

            String CREATE_INDEX_SQL = "create index " + TABLE_NOTE
                    + "_idx on " + TABLE_NOTE + "("
                    + "create_date"
                    + ")";

            db.execSQL(CREATE_INDEX_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


    public void println(String msg){
        Log.d(TAG, msg);
    }
}
