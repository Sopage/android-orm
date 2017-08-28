package com.android.dbcenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * @author Mr.Huang
 * @date 2017/8/28
 */
public class DataInterface {

    private Context context;
    private URICenter uri;
    private String db;

    public DataInterface(Context context) {
        this.context = context;
        this.uri = new URICenter(context);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return context.getContentResolver().query(uri.getQueryURI(table, groupBy, having, limit), columns, selection, selectionArgs, orderBy);
    }

    public boolean insert(String table, ContentValues values) {
        return context.getContentResolver().insert(uri.getInsertURI(table), values) != null;
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return context.getContentResolver().update(uri.getUpdateURI(table), values, whereClause, whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return context.getContentResolver().delete(uri.getDeleteURI(table), whereClause, whereArgs);
    }

    public boolean switchDB(String db) {
        this.db = db;
        return context.getContentResolver().update(uri.getSwitchURI(db), new ContentValues(), null, null) > 0;
    }

    public String getDatabaseName(){
        return db;
    }
}
