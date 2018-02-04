package com.android.dbcenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * @author Mr.Huang
 * @date 2017/8/25
 */
public class DBCenter {

    private SQLiteOpenHelper helper;

    public DBCenter(){

    }

    public final synchronized boolean doSwitch(Context context, String dbName) {
        helper = new SQLiteOpenHelper(context, dbName, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `test`(`_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `name` TEXT);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        helper.getWritableDatabase().rawQuery("select * from test", null);
        return false;
    }

    public final synchronized SQLiteDatabase getSQLiteDatabase() {
        return helper.getWritableDatabase();
    }
}
