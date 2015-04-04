package com.sanders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by sanders on 15/3/30.
 */
public class DefaultSQLiteOpenHelper extends SQLiteOpenHelper {

    private Set<Class> classes;
    private OnDBUpgrade upgrade;
    private DBProxy proxy;

    public DefaultSQLiteOpenHelper(Context context, String dbName, int dbVersion, Set<Class> classes, OnDBUpgrade upgrade) {
        super(context, dbName, null, dbVersion);
        this.classes = classes;
        this.upgrade = upgrade;
    }

    public void setDBProxy(DBProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            try {
                ClassInfo classInfo = proxy.getClassInfo(iterator.next());
                String sql = classInfo.getCreateTableSql();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (upgrade != null) {
            upgrade.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public interface OnDBUpgrade {
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }
}
