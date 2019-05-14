package com.example.bilbiophile.model.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "dbFavorite";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_FAVORITE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            FvDatabaseContract.TABLE_FV,
            FvDatabaseContract.favoriteColumns._ID,
            FvDatabaseContract.favoriteColumns.TITLE,
            FvDatabaseContract.favoriteColumns.DESCRIPTION,
            FvDatabaseContract.favoriteColumns.LINK,
            FvDatabaseContract.favoriteColumns.GUID,
            FvDatabaseContract.favoriteColumns.PREDATE
    );
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_FAVORITE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FvDatabaseContract.TABLE_FV);
        onCreate(sqLiteDatabase);
    }
}
