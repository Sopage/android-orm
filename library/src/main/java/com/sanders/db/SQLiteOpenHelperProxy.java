package com.sanders.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sanders on 15/3/30.
 */
public class SQLiteOpenHelperProxy extends SQLiteOpenHelper {

    private Collection<Class> classes;
    private OnDBUpgrade upgrade;
    private DBProxy proxy;

    public SQLiteOpenHelperProxy(Context context, String dbName, int dbVersion, Collection<Class> classes, OnDBUpgrade upgrade) {
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
                db.execSQL("DROP TABLE IF EXISTS `" + classInfo.getTableName() + "`;");
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

    private void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            ClassInfo classInfo = proxy.getClassInfo(iterator.next());
            String tableName = classInfo.getTableName();
            Cursor cursor = db.rawQuery("PRAGMA table_info(`" + tableName + "`", null);//查询表结构
            Map<String, String> dbFieldMap = new HashMap<String, String>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                dbFieldMap.put(name, type);
            }
            cursor.close();
            Map<String, Field> fieldMap = classInfo.getFieldMap();
            //更新数据库字段及字段类型
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                if (!dbFieldMap.containsKey(entry.getKey())) {
                    //TODO add db field
                    db.execSQL("ALTER TABLE `" + tableName + "` ADD COLUMN `" + entry.getKey() + "` " + ClassInfo.getDBFieldType(entry.getValue()) + ";");
                } else if (!dbFieldMap.get(entry.getKey()).equals(ClassInfo.getDBFieldType(entry.getValue()))) {
                    //TODO update db field type
                    db.execSQL("");
                }
            }
            //删除数据库没用的字段
            for (Map.Entry<String, String> entry : dbFieldMap.entrySet()) {
                if (!fieldMap.containsKey(entry.getKey())) {
                    //TODO delete db field
                }
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (upgrade != null && upgrade.beginUpgrade(db, oldVersion, newVersion)) ;

        if (upgrade == null || upgrade.onUpgrade(db, oldVersion, newVersion)) {
            this.onCreate(db);
        }
        if (upgrade != null && upgrade.endUpgrade(db, oldVersion, newVersion)) ;
    }
}
