package com.ftl.tourisma.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ftl.tourisma.utils.Utils;

import java.util.ArrayList;

public class DBAdapter {

    private static final String TAG = DBAdapter.class.getSimpleName();
    // TOURISMA DATABASE
    private static final String DATABASE_NAME = "TOURISMA.db";
    private static final int DATABASE_VERSION = 1;
    // SEARCH TABLE
    private static final String TABLE_SEARCH = "SEARCH";// course
    private static final String KEY_SEARCH_ID = "_id";
    // SEARCH FIELD
    private static final String KEY_SEARCH_STR = "SEARCH_STR";
    // CREATE TABLE LANGUAGE
    private static final String TABLE_CREATE_SEARCH = "Create table IF NOT EXISTS " + TABLE_SEARCH + "( " + KEY_SEARCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SEARCH_STR + " TEXT );";
    // LANGUAGE TABLE
    private static final String TABLE_LANGUAGES = "LANGUAGES";// course
    private static final String KEY_LAN_ID = "_id";
    // LANGUAGE FIELD
    private static final String KEY_Lan_ID = "Lan_ID";
    private static final String KEY_Lan_name = "Lan_name";
    private static final String KEY_Lan_Contents = "Lan_Contents";
    private static final String KEY_Lan_Status = "Lan_Status";
    private static final String KEY_Msg_ID = "Msg_ID";
    private static final String KEY_Msg_Constant = "Msg_Constant";
    private static final String KEY_Msg_Statement = "Msg_Statement";
    private static final String KEY_Msg_Status = "Msg_Status";
    // CREATE TABLE LANGUAGE
    private static final String TABLE_CREATE_LANGUAGE = "Create table IF NOT EXISTS " + TABLE_LANGUAGES + "( " + KEY_LAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_Lan_ID + " TEXT, "
            + KEY_Lan_name + " TEXT, "
            + KEY_Lan_Contents + " TEXT, "
            + KEY_Lan_Status + " TEXT, "
            + KEY_Msg_ID + " TEXT, "
            + KEY_Msg_Constant + " TEXT, "
            + KEY_Msg_Statement + " TEXT, "
            + KEY_Msg_Status + " TEXT );";
    // NEARBY TABLE
    private static final String TABLE_NEARBY = "NEARBY";// course
    private static final String KEY_NB_ID = "_id";
    // NEARBY FIELD
    private static final String KEY_Place_Id = "Place_Id";
    private static final String KEY_Category_Name = "Category_Name";
    private static final String KEY_Place_Name = "Place_Name";
    private static final String KEY_Place_ShortInfo = "Place_ShortInfo";
    private static final String KEY_Place_MainImage = "Place_MainImage";
    private static final String KEY_Place_Description = "Place_Description";
    private static final String KEY_Place_Address = "Place_Address";
    private static final String KEY_Place_Latitude = "Place_Latitude";
    private static final String KEY_Place_Longi = "Place_Longi";
    private static final String KEY_Place_Recommended = "Place_Recommended";
    private static final String KEY_otherimages = "otherimages";
    private static final String KEY_dist = "dist";
    private static final String KEY_Fav_Id = "Fav_Id";
    // CREATE TABLE NEARBY
    private static final String TABLE_CREATE_NEARBY = "Create table IF NOT EXISTS " + TABLE_NEARBY + "( " + KEY_NB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_Place_Id + " TEXT, "
            + KEY_Category_Name + " TEXT, "
            + KEY_Place_Name + " TEXT, "
            + KEY_Place_ShortInfo + " TEXT, "
            + KEY_Place_MainImage + " TEXT, "
            + KEY_Place_Description + " TEXT, "
            + KEY_Place_Address + " TEXT, "
            + KEY_Place_Latitude + " TEXT, "
            + KEY_Place_Longi + " TEXT, "
            + KEY_Place_Recommended + " TEXT, "
            + KEY_otherimages + " TEXT, "
            + KEY_dist + " TEXT, "
            + KEY_Fav_Id + " TEXT );";
    // HoursOfOperation TABLE
    private static final String TABLE_HoursOfOperation = "HoursOfOperation";// course
    private static final String KEY_HO_ID = "_id";
    // HoursOfOperation FIELD
    private static final String KEY_POH_Id = "POH_Id";
    //    private static final String KEY_Place_Id = "Place_Id";
    private static final String KEY_POH_Start_Day = "POH_Start_Day";
    private static final String KEY_POH_End_Day = "POH_End_Day";
    private static final String KEY_POH_Start_Time = "POH_Start_Time";
    private static final String KEY_POH_End_Time = "POH_End_Time";
    private static final String KEY_POH_Charges = "POH_Charges";
    // CREATE TABLE HoursOfOperation
    private static final String TABLE_CREATE_HoursOfOperation = "Create table IF NOT EXISTS " + TABLE_HoursOfOperation + "( " + KEY_HO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_POH_Id + " TEXT, "
            + KEY_Place_Id + " TEXT, "
            + KEY_POH_Start_Day + " TEXT, "
            + KEY_POH_End_Day + " TEXT, "
            + KEY_POH_Start_Time + " TEXT, "
            + KEY_POH_End_Time + " TEXT, "
            + KEY_POH_Charges + " TEXT );";
    private final Context context;
    public Cursor cursor;
    private SQLiteDatabase sqlDB;
    private DatabaseHelper DBHelper;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    // To open SQLiteDatabase object; either in read or write mode.
    public DBAdapter open() throws SQLException {
        sqlDB = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public boolean deleteTable(String tablename) {
        Utils.Log(TAG, "In del all..." + tablename);
        return sqlDB.delete(tablename, null, null) > 0;
    }

    public long insertSearchString(String searchStr) {
        long returnVal = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SEARCH_STR, searchStr);

            returnVal = sqlDB.insert(TABLE_SEARCH, null, contentValues);
            Utils.Log(TAG, "inserted search string " + searchStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    public ArrayList<String> getSearchStr() {
        ArrayList<String> search = new ArrayList<String>();
        String data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_SEARCH + " ORDER BY _id DESC limit 10", null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = cr.getString(1);
            search.add(data);
            cr.moveToNext();
        }
        return search;
    }

    public long insertLanguage(String Lan_ID, String Lan_name, String Lan_Contents, String Lan_Status, String Msg_ID, String Msg_Constant, String Msg_Statement, String Msg_Status) {
        long returnVal = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_Lan_ID, Lan_ID);
            cv.put(KEY_Lan_name, Lan_name);
            cv.put(KEY_Lan_Contents, Lan_Contents);
            cv.put(KEY_Lan_Status, Lan_Status);
            cv.put(KEY_Msg_ID, Msg_ID);
            cv.put(KEY_Msg_Constant, Msg_Constant);
            cv.put(KEY_Msg_Statement, Msg_Statement);
            cv.put(KEY_Msg_Status, Msg_Status);

            returnVal = sqlDB.insert(TABLE_LANGUAGES, null, cv);
//            Utils.Log(TAG, " insertLanguage Msg_Statement " + Msg_Statement);
        } catch (Exception e) {
            Utils.Log(TAG, e.getMessage());
        }
        return returnVal;
    }

    public String getLanguageMsg(String Lan_ID, String Msg_Constant) {
        String msg = new String();
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_LANGUAGES + " where Lan_ID = \"" + Lan_ID + "\" and " + "Msg_Constant = \"" + Msg_Constant + "\"", null);
        if (cr != null && cr.moveToFirst()) {
//            cr.moveToFirst();
            msg = cr.getString(cr.getColumnIndex(KEY_Msg_Statement));
            //Log.d("System out", "Msg_Constant Inside if" + msg);
        } else {
            msg = "";
            // Log.d("System out", "Msg_Constant Inside else" + msg);
        }
        // Log.d("System out", "Msg_Constant Outside" + msg);
        return msg.trim();
    }

    public ArrayList<Language> getLanguage() {
        ArrayList<Language> languages = new ArrayList<Language>();
        Language data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_LANGUAGES, null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = new Language();
            data.setLan_ID(cr.getString(cr.getColumnIndex(KEY_Lan_ID)));
            data.setLan_name(cr.getString(cr.getColumnIndex(KEY_Lan_name)));
            data.setLan_Contents(cr.getString(cr.getColumnIndex(KEY_Lan_Contents)));
            data.setLan_Status(cr.getString(cr.getColumnIndex(KEY_Lan_Status)));
            data.setMsg_ID(cr.getString(cr.getColumnIndex(KEY_Msg_ID)));
            data.setMsg_Constant(cr.getString(cr.getColumnIndex(KEY_Msg_Constant)));
            data.setMsg_Statement(cr.getString(cr.getColumnIndex(KEY_Msg_Statement)));
            data.setMsg_Status(cr.getString(cr.getColumnIndex(KEY_Msg_Status)));
            languages.add(data);
            cr.moveToNext();
        }
        return languages;
    }

    public long insertNearby(String Place_Id, String Category_Name, String Place_Name, String Place_ShortInfo, String Place_MainImage, String Place_Description, String Place_Address, String Place_Latitude, String Place_Longi, String Place_Recommended, String otherimages, String dist, String Fav_Id) {
        long returnVal = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_Place_Id, Place_Id);
            cv.put(KEY_Category_Name, Category_Name);
            cv.put(KEY_Place_Name, Place_Name);
            cv.put(KEY_Place_ShortInfo, Place_ShortInfo);
            cv.put(KEY_Place_MainImage, Place_MainImage);
            cv.put(KEY_Place_Description, Place_Description);
            cv.put(KEY_Place_Address, Place_Address);
            cv.put(KEY_Place_Latitude, Place_Latitude);
            cv.put(KEY_Place_Longi, Place_Longi);
            cv.put(KEY_Place_Recommended, Place_Recommended);
            cv.put(KEY_otherimages, otherimages);
            cv.put(KEY_dist, dist);
            cv.put(KEY_Fav_Id, Fav_Id);

            returnVal = sqlDB.insert(TABLE_NEARBY, null, cv);
            Utils.Log(TAG, "inserted place id " + Place_Id);
        } catch (Exception e) {
            Utils.Log(TAG, e.getMessage());
        }
        return returnVal;
    }

    public boolean updateNearBy(String Place_Id, String Fav_Id) {
        Cursor cr = sqlDB.rawQuery("update " + TABLE_NEARBY + " set Fav_Id = " + "\"" + Fav_Id + "\"" + " where Place_Id = " + "\"" + Place_Id + "\"", null);
        Utils.Log(TAG, "total updated records " + cr.getCount());
        return true;
    }

    public ArrayList<Nearby> getNearby() {
        ArrayList<Nearby> nearbies = new ArrayList<Nearby>();
        Nearby data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_NEARBY, null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = new Nearby();
            data.setPlace_Id(cr.getString(1));
            data.setCategory_Name(cr.getString(2));
            data.setPlace_Name(cr.getString(3));
            data.setPlace_ShortInfo(cr.getString(4));
            data.setPlace_MainImage(cr.getString(5));
            data.setPlace_Description(cr.getString(6));
            data.setPlace_Address(cr.getString(7));
            data.setPlace_Latitude(cr.getString(8));
            data.setPlace_Longi(cr.getString(9));
            data.setPlace_Recommended(cr.getString(10));
            data.setOtherimages(cr.getString(11));
            data.setDist(cr.getString(12));
            data.setFav_Id(cr.getString(13));
            nearbies.add(data);
            cr.moveToNext();
        }
        return nearbies;
    }

    public ArrayList<Nearby> getNearbyFavorite() {
        ArrayList<Nearby> nearbies = new ArrayList<Nearby>();
        Nearby data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_NEARBY + " where Fav_Id <> " + "\"0\"", null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = new Nearby();
            data.setPlace_Id(cr.getString(1));
            data.setCategory_Name(cr.getString(2));
            data.setPlace_Name(cr.getString(3));
            data.setPlace_ShortInfo(cr.getString(4));
            data.setPlace_MainImage(cr.getString(5));
            data.setPlace_Description(cr.getString(6));
            data.setPlace_Address(cr.getString(7));
            data.setPlace_Latitude(cr.getString(8));
            data.setPlace_Longi(cr.getString(9));
            data.setPlace_Recommended(cr.getString(10));
            data.setOtherimages(cr.getString(11));
            data.setDist(cr.getString(12));
            data.setFav_Id(cr.getString(13));
            nearbies.add(data);
            cr.moveToNext();
        }
        return nearbies;
    }

    public long insertHoursOfOperation(String POH_Id, String Place_Id, String POH_Start_Day, String POH_End_Day, String POH_Start_Time, String POH_End_Time, String POH_Charges) {
        long returnVal = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_POH_Id, POH_Id);
            cv.put(KEY_Place_Id, Place_Id);
            cv.put(KEY_POH_Start_Day, POH_Start_Day);
            cv.put(KEY_POH_End_Day, POH_End_Day);
            cv.put(KEY_POH_Start_Time, POH_Start_Time);
            cv.put(KEY_POH_End_Time, POH_End_Time);
            cv.put(KEY_POH_Charges, POH_Charges);

            returnVal = sqlDB.insert(TABLE_HoursOfOperation, null, cv);
            Utils.Log(TAG, "inserted poh id " + POH_Id);
        } catch (Exception e) {
            Utils.Log(TAG, e.getMessage());
        }
        return returnVal;
    }

    public ArrayList<HoursOfOperation> getHoursOfOperation() {
        ArrayList<HoursOfOperation> hoursOfOperationArrayList = new ArrayList<HoursOfOperation>();
        HoursOfOperation data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_HoursOfOperation, null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = new HoursOfOperation();
            data.setPOH_Id(cr.getString(1));
            data.setPlace_Id(cr.getString(2));
            data.setPOH_Start_Day(cr.getString(3));
            data.setPOH_End_Day(cr.getString(4));
            data.setPOH_Start_Time(cr.getString(5));
            data.setPOH_End_Time(cr.getString(6));
            data.setPOH_Charges(cr.getString(7));

            hoursOfOperationArrayList.add(data);
            cr.moveToNext();
        }
        return hoursOfOperationArrayList;
    }

    public ArrayList<HoursOfOperation> getHoursOfOperationFavorite() {
        ArrayList<HoursOfOperation> hoursOfOperationArrayList = new ArrayList<HoursOfOperation>();
        HoursOfOperation data;
        Cursor cr = sqlDB.rawQuery("select * from " + TABLE_HoursOfOperation + " where Fav_Id = " + "\"1\"", null);
        cr.moveToFirst();
        for (int i = 0; i < cr.getCount(); i++) {
            data = new HoursOfOperation();
            data.setPOH_Id(cr.getString(1));
            data.setPlace_Id(cr.getString(2));
            data.setPOH_Start_Day(cr.getString(3));
            data.setPOH_End_Day(cr.getString(4));
            data.setPOH_Start_Time(cr.getString(5));
            data.setPOH_End_Time(cr.getString(6));
            data.setPOH_Charges(cr.getString(7));

            hoursOfOperationArrayList.add(data);
            cr.moveToNext();
        }
        return hoursOfOperationArrayList;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE_LANGUAGE);
            Utils.Log(TAG, "TABLE_CREATE_LANGUAGE");
            db.execSQL(TABLE_CREATE_SEARCH);
            Utils.Log(TAG, "TABLE_CREATE_SEARCH");
//            db.execSQL(TABLE_CREATE_NEARBY);
//            Log.d("System out", "TABLE_CREATE_NEARBY");
//            db.execSQL(TABLE_CREATE_HoursOfOperation);
//            Log.d("System out", "TABLE_CREATE_HoursOfOperation");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}