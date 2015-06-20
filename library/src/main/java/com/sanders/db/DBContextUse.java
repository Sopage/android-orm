package com.sanders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by sanders on 15/6/20.
 */
public class DBContextUse {
    private String name;
    private int version;
    private OnDBUpgrade upgrade;
    private Collection<String> mSql = new LinkedHashSet<String>();

    public DBContextUse(String name, int version, OnDBUpgrade upgrade) {
        this.name = name;
        this.version = version;
        this.upgrade = upgrade;
    }

    public DBContextUse addSql(String sql) {
        this.mSql.add(sql);
        return this;
    }

    public void addSqls(Collection<String> sql) {
        this.mSql.addAll(sql);
    }

    public DBProxy buildDBProxy(Context context) {
        final OpenHelper helper = new OpenHelper(context, name, version);
        helper.setSqlCollection(mSql);
        helper.setOnUpgrade(upgrade);
        DBProxy proxy = new DBProxy() {
            @Override
            public SQLiteDatabase getCreateDatabase() {
                return helper.getWritableDatabase();
            }
        };
        return proxy;
    }
}
