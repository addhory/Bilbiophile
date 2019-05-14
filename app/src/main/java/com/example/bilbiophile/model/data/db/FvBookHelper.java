package com.example.bilbiophile.model.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.bilbiophile.model.data.Book;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.TABLE_FV;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.favoriteColumns.DESCRIPTION;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.favoriteColumns.GUID;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.favoriteColumns.LINK;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.favoriteColumns.PREDATE;
import static com.example.bilbiophile.model.data.db.FvDatabaseContract.favoriteColumns.TITLE;

public class FvBookHelper {
    private static String TABLE_NAME = TABLE_FV;
    private Context context;
    private DatabaseHelper helper;
    private SQLiteDatabase database;

    public FvBookHelper(Context context) {
        this.context = context;
    }
    public FvBookHelper open() throws SQLException {
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }


    public ArrayList<Book> query() {
        ArrayList<Book> arrayList = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                _ID + " DESC",
                null);
        cursor.moveToFirst();
        Book book;
        if (cursor.getCount() > 0) {
            do {
                book = new Book();
                book.title=cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
                book.description=cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
                book.link=cursor.getString(cursor.getColumnIndexOrThrow(LINK));
                book.guid=cursor.getString(cursor.getColumnIndexOrThrow(GUID));
                book.pubDate= cursor.getLong(cursor.getColumnIndexOrThrow(PREDATE));



                arrayList.add(book);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public long insert(Book book){
        ContentValues values= new ContentValues();
        values.put(TITLE, book.title);
        values.put(DESCRIPTION, book.description);
        values.put(LINK, book.link);
        values.put(GUID, book.guid);
        values.put(PREDATE, book.pubDate);
        return database.insert(TABLE_NAME, null, values);
    }

    public int delete(String title){
        return database.delete(TABLE_NAME, TITLE + " = '" + title + "'", null);

    }
}
