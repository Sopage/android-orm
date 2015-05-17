package com.sanders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by sanders on 15/5/17.
 */
public class DBContext {

    private String name;
    private int version;
    private OnDBUpgrade upgrade;
    private Collection<Class> tables = new LinkedHashSet<Class>();

    public DBContext(String name, int version, OnDBUpgrade upgrade) {
        this.name = name;
        this.version = version;
        this.upgrade = upgrade;
    }

    public DBContext addTableBean(Class clazz) {
        this.tables.add(clazz);
        return this;
    }

    public void setTables(Collection<Class> tables) {
        this.tables.addAll(tables);
    }

    public DBProxy buildDBProxy(Context context) {
        final SQLiteOpenHelperProxy helper = new SQLiteOpenHelperProxy(context, name, version);
        helper.addTableBeans(tables);
        helper.setOnUpgrade(upgrade);
        DBProxy proxy = new DBProxy() {
            @Override
            public SQLiteDatabase getCreateDatabase() {
                return helper.getWritableDatabase();
            }
        };
        helper.setDBProxy(proxy);
        return proxy;
    }

}
