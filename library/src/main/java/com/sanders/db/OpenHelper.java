package com.sanders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;

/**
 * Created by sanders on 15/6/20.
 */
public class OpenHelper extends SQLiteOpenHelper {

    private OnDBUpgrade mUpgrade;
    private Collection<String> mSql;

    public OpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public void setSqlCollection(Collection<String> sql){
        this.mSql = sql;
    }

    public void setOnUpgrade(OnDBUpgrade upgrade){
        this.mUpgrade = upgrade;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String sql : mSql){
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mUpgrade != null) {
            mUpgrade.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
