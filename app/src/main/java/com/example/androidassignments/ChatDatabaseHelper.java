package com.example.androidassignments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ChatDatabaseHelper extends SQLiteOpenHelper {

    // Static variables for database name and version
    private static final String DATABASE_NAME = "Messages.db";
    private static final int VERSION_NUM = 3; // Incremented version number for upgrade

    // Tag for logging
    private static final String TAG = "ChatDatabaseHelper";

    // Table name
    public static final String TABLE_NAME = "Messages";

    // Column names defined as static final variables
    public static final String KEY_ID = "id";
    public static final String KEY_MESSAGE = "message";

    // Constructor
    public ChatDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Log onCreate
        Log.i(TAG, "onCreate called in ChatDatabaseHelper");

        // SQL statement to create the Messages table
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_MESSAGE + " TEXT NOT NULL" +
                ");";
        try {
            db.execSQL(CREATE_MESSAGES_TABLE);
            Log.d(TAG, "Database created with table: " + TABLE_NAME);
        } catch (Exception e) {
            Log.e(TAG, "Error creating table " + TABLE_NAME + ": " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Log that onUpgrade is being called with version details
        Log.i(TAG, "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);

        // SQL statement to drop the existing table if it exists
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        try {
            db.execSQL(DROP_TABLE);
            Log.d(TAG, "Dropped table: " + TABLE_NAME);
            // Recreate the database
            onCreate(db);
            Log.d(TAG, "Recreated table: " + TABLE_NAME);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database from version " + oldVersion + " to " + newVersion + ": " + e.getMessage());
        }
    }

}