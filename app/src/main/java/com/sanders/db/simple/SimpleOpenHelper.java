package com.sanders.db.simple;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sanders on 15/3/28.
 */
public class SimpleOpenHelper extends SQLiteOpenHelper {

    private String sql = "CREATE TABLE `user_table` (`_key_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `user_name` TEXT, `pass_word` TEXT, `age` INTEGER);";

    public SimpleOpenHelper(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
