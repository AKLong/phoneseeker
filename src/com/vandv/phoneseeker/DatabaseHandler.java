package com.vandv.phoneseeker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import static com.vandv.phoneseeker.CommonUtilities.positionData;
import static com.vandv.phoneseeker.CommonUtilities.dateFormat;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "phoneSeeker.db";
 
    // PositionDatas table name
    private static final String TABLE_POSITION = "positionData";
 
    // PositionDatas Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SERVER_ID = "serverid";
    private static final String KEY_GPS_LAT = "gpslat";
    private static final String KEY_GPS_LON = "gpslon";
    private static final String KEY_NET_LAT = "netlat";
    private static final String KEY_NET_LON = "netlon";
    private static final String KEY_IP = "ip";
    private static final String KEY_STATUS = "status";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_GPS_ACCURACY = "gpsaccuracy";
    private static final String KEY_NET_ACCURACY = "netaccuracy";
    private static final String KEY_DATE = "date";
    
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PositionDataS_TABLE = "CREATE TABLE " + TABLE_POSITION + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
           		+ KEY_SERVER_ID + " TEXT,"
           		+ KEY_DATE + " DATE,"
           		+ KEY_GPS_LAT + " FLOAT,"
                + KEY_GPS_LON + " FLOAT," 
           		+ KEY_NET_LAT + " FLOAT,"
                + KEY_NET_LON + " FLOAT," 
                + KEY_IP + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_SOURCE + " TEXT,"
                + KEY_GPS_ACCURACY + " FLOAT,"
                + KEY_NET_ACCURACY + " FLOAT"+ ")";
        db.execSQL(CREATE_PositionDataS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITION);
 
        // Create tables again
        onCreate(db);
    }
    
    
    
    
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new PositionData
    long addPositionData() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_ID, positionData.getServerID()); // PositionData Name
        values.put(KEY_GPS_LAT, positionData.getGPSLat()); // PositionData Name
        values.put(KEY_GPS_LON, positionData.getGPSLon()); // PositionData Phone
        values.put(KEY_NET_LAT, positionData.getNetworkLat()); // PositionData Name
        values.put(KEY_NET_LON, positionData.getNetworkLon()); // PositionData Phone
        values.put(KEY_IP, positionData.getIP()); // PositionData Phone
        values.put(KEY_STATUS, positionData.getStatus()); // PositionData Phone
        values.put(KEY_SOURCE, positionData.getSource()); // PositionData Phone
        values.put(KEY_GPS_ACCURACY, positionData.getGPSAccuracy()); // PositionData Phone
        values.put(KEY_GPS_ACCURACY, positionData.getNetworkAccuracy()); // PositionData Phone
        values.put(KEY_DATE, positionData.getDate()); // PositionData Phone
 
        // Inserting Row
        Long id = db.insert(TABLE_POSITION, null, values);
        db.close(); // Closing database connection
        
        return id;
    }
 
  
    // Getting single PositionData
    void getPositionData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_POSITION, new String[] { KEY_ID, KEY_SERVER_ID,
        		KEY_GPS_LAT, KEY_GPS_LON, KEY_NET_LAT, KEY_NET_LON, KEY_IP, KEY_STATUS, KEY_SOURCE, KEY_GPS_ACCURACY , KEY_NET_ACCURACY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        positionData = new PositionData();
        positionData.setID(cursor.getLong(0));
        positionData.setServerID(cursor.getLong(1));
        positionData.setGPSLat(cursor.getDouble(2));
        positionData.setGPSLon(cursor.getDouble(3));
        positionData.setNetworkLat(cursor.getDouble(4));
        positionData.setNetworkLon(cursor.getDouble(5));
        positionData.setIP(cursor.getString(6));
        positionData.setStatus(cursor.getString(7));
        positionData.setSource(cursor.getString(8));
        positionData.setGPSAccuracy(cursor.getFloat(9));
        positionData.setNetworkAccuracy(cursor.getFloat(10));
        // return PositionData
    }
 
    // Getting All PositionDatas
    public List<PositionData> getAllPositionData() {
        List<PositionData> PositionDataList = new ArrayList<PositionData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_POSITION;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PositionData PositionData = new PositionData();
                PositionData.setID(Long.parseLong(cursor.getString(0)));
                PositionData.setServerID(Long.parseLong(cursor.getString(1)));
                PositionData.setGPSLat(cursor.getDouble(2));
                PositionData.setGPSLon(cursor.getDouble(3));
                PositionData.setNetworkLat(cursor.getDouble(4));
                PositionData.setNetworkLon(cursor.getDouble(5));
                PositionData.setIP(cursor.getString(6));
                PositionData.setStatus(cursor.getString(7));
                PositionData.setSource(cursor.getString(8));
                PositionData.setGPSAccuracy(cursor.getFloat(9));
                PositionData.setNetworkAccuracy(cursor.getFloat(10));
                         // Adding PositionData to list
                PositionDataList.add(PositionData);
            } while (cursor.moveToNext());
        }
 
        // return PositionData list
        return PositionDataList;
    }
 
    // Updating single PositionData
    public int updatePositionData() {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_ID, positionData.getServerID());
        values.put(KEY_GPS_LAT, positionData.getGPSLat());
        values.put(KEY_GPS_LON, positionData.getGPSLon());
        values.put(KEY_NET_LAT, positionData.getNetworkLat());
        values.put(KEY_NET_LON, positionData.getNetworkLon());
        values.put(KEY_IP, positionData.getIP());
        values.put(KEY_STATUS, positionData.getStatus());
        values.put(KEY_SOURCE, positionData.getSource());
        values.put(KEY_GPS_ACCURACY, positionData.getGPSAccuracy());
        values.put(KEY_NET_ACCURACY, positionData.getNetworkAccuracy());
 
        // updating row
        return db.update(TABLE_POSITION, values, KEY_ID + " = ?",
                new String[] { String.valueOf(positionData.getID()) });
    }
 
    // Deleting single PositionData
    public void deletePositionData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSITION, KEY_ID + " = ?",
                new String[] { id});
        db.close();
    }
 
    // Getting PositionDatas Count
    public int getPositionDataCount() {
        String countQuery = "SELECT  * FROM " + TABLE_POSITION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

    
    public void getLastPosition(){
        String query = "SELECT  * FROM " + TABLE_POSITION + " ORDER BY "+ KEY_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
               
        positionData = new PositionData();

        if(cursor.moveToFirst())
        {
        	Date d;
			try {
				d = dateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
	            positionData.setDate(d);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            positionData.setID(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
            positionData.setServerID(cursor.getLong(cursor.getColumnIndex(KEY_SERVER_ID)));
            positionData.setGPSLat(cursor.getDouble(cursor.getColumnIndex(KEY_GPS_LAT)));
            positionData.setGPSLon(cursor.getDouble(cursor.getColumnIndex(KEY_GPS_LON)));
            positionData.setNetworkLat(cursor.getDouble(cursor.getColumnIndex(KEY_NET_LAT)));
            positionData.setNetworkLon(cursor.getDouble(cursor.getColumnIndex(KEY_NET_LON)));
          positionData.setIP(cursor.getString(cursor.getColumnIndex(KEY_IP)));
          positionData.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
          positionData.setSource(cursor.getString(cursor.getColumnIndex(KEY_SOURCE)));
          positionData.setGPSAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_GPS_ACCURACY)));
          positionData.setNetworkAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_NET_ACCURACY)));
                 }
        cursor.close();
               
        //return positionData;
    }
}