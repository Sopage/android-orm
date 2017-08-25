package com.android.dbcenter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Mr.Huang
 * @date 2017/8/25
 */
public class DataCenterProvider extends ContentProvider {

    private URICenter uriCenter;
    private DBCenter dbCenter;

    @Override
    public boolean onCreate() {
        uriCenter = new URICenter(getContext());
        dbCenter = new DBCenter();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriCenter.isQueryURI(uri)) {
            String table = uri.getQueryParameter("table");
            SQLiteDatabase database = dbCenter.getSQLiteDatabase();
            if (database != null && database.isOpen()) {
                return database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
            }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "no type";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriCenter.isInsertURI(uri)) {
            String table = uri.getQueryParameter("table");
            SQLiteDatabase database = dbCenter.getSQLiteDatabase();
            if (database != null && database.isOpen()) {
                long i = database.insert(table, null, values);
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return uri;
                }
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriCenter.isDeleteURI(uri)) {
            String table = uri.getQueryParameter("table");
            SQLiteDatabase database = dbCenter.getSQLiteDatabase();
            if (database != null && database.isOpen()) {
                int i = database.delete(table, selection, selectionArgs);
                if(i > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriCenter.isUpdateURI(uri)) {
            String table = uri.getQueryParameter("table");
            SQLiteDatabase database = dbCenter.getSQLiteDatabase();
            if (database != null && database.isOpen()) {
                int i = database.update(table, values, selection, selectionArgs);
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return i;
            }
        } else if (uriCenter.isSwitchURI(uri)) {
            String name = uri.getQueryParameter("database");
            if (dbCenter.doSwitch(getContext(), name + ".db")) {
                getContext().getContentResolver().notifyChange(uri, null);
                return 1;
            }
        }
        return -1;
    }
}
