package com.sanders.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库升级操作类
 * Created by sanders on 15/5/9.
 */
public interface OnDBUpgrade {

    /**
     * 数据库升级调用此方法
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     * @return true是手动升级，false是自动升级
     */
    boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
