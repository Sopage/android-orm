package com.sanders.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanders on 15/4/4.
 */
public class DBProxy {

    public Map<Class, ClassInfo> classInfoMap = new HashMap<Class, ClassInfo>();
    private SQLiteOpenHelper helper;
    private int closeIndex = 0;

    public static class DBBuilder {
        private String dbName;
        private int dbVersion;
        private DefaultSQLiteOpenHelper.OnDBUpgrade upgrade;
        private Set<Class> classes = new HashSet<Class>();

        public DBBuilder builderDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public DBBuilder builderDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
            return this;
        }

        public DBBuilder builderTable(Class clazz) {
            this.classes.add(clazz);
            return this;
        }

        public DBBuilder setOnDBUpgrade(DefaultSQLiteOpenHelper.OnDBUpgrade upgrade){
            this.upgrade = upgrade;
            return this;
        }

        public DBProxy build(Context context) {
            DBProxy proxy = new DBProxy();
            DefaultSQLiteOpenHelper helper = new DefaultSQLiteOpenHelper(context, dbName, dbVersion, classes, upgrade);
            helper.setDBProxy(proxy);
            proxy.setSQLiteOpenHelper(helper);
            return proxy;
        }
    }

    private DBProxy(){

    }

    private void setSQLiteOpenHelper(SQLiteOpenHelper helper){
        this.helper = helper;
    }

    public DBProxy(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    private synchronized <T extends IDColumn> ClassInfo getClassInfo(T t) {
        ClassInfo classInfo = classInfoMap.get(t.getClass());
        if (classInfo == null) {
            classInfo = new ClassInfo(t.getClass());
        }
        return classInfo;
    }

    public synchronized <T extends IDColumn> ClassInfo getClassInfo(Class<T> clazz) {
        ClassInfo classInfo = classInfoMap.get(clazz);
        if (classInfo == null) {
            classInfo = new ClassInfo(clazz);
        }
        return classInfo;
    }

    public synchronized <T extends IDColumn> long insert(T t) {
        if (t == null) {
            return -1;
        }
        ClassInfo<T> classInfo = getClassInfo(t);
        String tableName = classInfo.getTableName();
        ContentValues values = classInfo.getContentValues(t);
        if (values != null) {
            SQLiteDatabase database = getDatabase();
            database.beginTransaction();
            long id = database.insert(tableName, null, values);
            t.setKeyId(id);
            database.setTransactionSuccessful();
            database.endTransaction();
            close(database);
            return id;
        }
        return -1;
    }

    public synchronized <T extends IDColumn> void insert(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        ClassInfo<T> classInfo = getClassInfo(list.get(0));
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = classInfo.getContentValues(t);
            if (values != null) {
                long id = database.insert(classInfo.getTableName(), null, values);
                t.setKeyId(id);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public synchronized <T extends IDColumn> int update(T t, String where, String... args) {
        if (t == null) {
            throw new NullPointerException("T对象不能为NULL！");
        }
        if (where == null) {
            throw new NullPointerException("缺少WHERE条件语句！");
        }
        ClassInfo<T> classInfo = getClassInfo(t);
        String tableName = classInfo.getTableName();
        ContentValues values = classInfo.getContentValues(t);
        if (values == null) {
            return -1;
        }
        values.remove(IDColumn.KEY_ID);
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.update(tableName, values, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    public synchronized <T extends IDColumn> int update(T t) {
        long keyId;
        if (t == null) {
            throw new NullPointerException("T对象不能为NULL！");
        } else if ((keyId = t.getKeyId()) < 1) {
            return -1;
        }
        return update(t, IDColumn.KEY_ID + "=" + keyId, null);
    }

    public synchronized <T extends IDColumn> int update(T t, long keyId) {
        return update(t, IDColumn.KEY_ID + "=" + keyId, null);
    }

    public synchronized <T extends IDColumn> void update(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        Class clazz = list.get(0).getClass();
        ClassInfo<T> classInfo = getClassInfo(list.get(0));
        String tableName = classInfo.getTableName();
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            long keyId = t.getKeyId();
            ContentValues values = classInfo.getContentValues(t);
            if (values != null && keyId > 0) {
                values.remove(IDColumn.KEY_ID);
                database.update(tableName, values, IDColumn.KEY_ID + "=" + keyId, null);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public synchronized <T extends IDColumn> void insertOrUpdate(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        ClassInfo<T> classInfo = getClassInfo(list.get(0));
        String tableName = classInfo.getTableName();
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = classInfo.getContentValues(t);
            if (values != null) {
                long keyId = t.getKeyId();
                if (keyId > 0) {
                    database.update(tableName, values, IDColumn.KEY_ID + "=" + keyId, null);
                } else {
                    database.insert(tableName, null, values);
                }
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public synchronized void execSQL(String... sql) {
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (String s : sql) {
            database.execSQL(s);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public synchronized int delete(Class<?> clazz, String where, String... args) {
        String table = ClassInfo.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.delete(table, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    public synchronized int delete(Class<?> clazz, long keyId) {
        return delete(clazz, IDColumn.KEY_ID + "=" + keyId, null);
    }

    public <T extends IDColumn> long queryCount(Class<T> clazz, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        ClassInfo<T> classInfo = getClassInfo(clazz);
        StringBuilder sql = new StringBuilder("SELECT COUNT(").append(IDColumn.KEY_ID).append(") AS count FROM ");
        sql.append(classInfo.getTableName());
        if (where != null && where.trim().length() > 0) {
            sql.append(" WHERE ").append(where);
        }
        sql.append(";");
        Cursor cursor = database.rawQuery(sql.toString(), args);
        long count = 1;
        if (cursor.moveToNext()) {
            count = cursor.getLong(0);
        }
        close(cursor);
        close(database);
        return count;
    }

    public <T extends IDColumn> long queryKeyId(Class<T> clazz, String where, String... args) {
        if (where == null) {
            throw new NullPointerException("缺少WHERE条件语句！");
        }
        ClassInfo<T> classInfo = getClassInfo(clazz);
        SQLiteDatabase database = getDatabase();
        StringBuilder sql = new StringBuilder("SELECT ").append(IDColumn.KEY_ID).append(" FROM ").append(classInfo.getTableName()).append(" WHERE ").append(where);
        Cursor cursor = database.rawQuery(sql.toString(), args);
        long id = -1;
        if (cursor.moveToNext()) {
            id = cursor.getLong(0);
        }
        close(cursor);
        close(database);
        return id;
    }

    public <T extends IDColumn> T query(Class<T> clazz, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        ClassInfo<T> classInfo = getClassInfo(clazz);
        Cursor cursor = database.query(classInfo.getTableName(), null, where, args, null, null, null);
        T t = classInfo.getInstanceObject(cursor);
        close(cursor);
        close(database);
        return t;
    }

    public <T extends IDColumn> T query(Class<T> clazz, long keyId) {
        return query(clazz, IDColumn.KEY_ID + "=" + keyId, null);
    }

    public <T extends IDColumn> T querySql(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        ClassInfo<T> classInfo = getClassInfo(clazz);
        T t = classInfo.getInstanceObject(cursor);
        close(cursor);
        close(database);
        return t;
    }

    public <T extends IDColumn> List<T> querySqlList(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        ClassInfo<T> classInfo = getClassInfo(clazz);
        List<T> list = classInfo.getInstanceList(cursor);
        close(cursor);
        close(database);
        return list;
    }

    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        ClassInfo<T> classInfo = getClassInfo(clazz);
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.query(classInfo.getTableName(), null, selection, selectionArgs, groupBy, having, orderBy, limit);
        List<T> list = classInfo.getInstanceList(cursor);
        close(cursor);
        close(database);
        return list;
    }

    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, int pageNumber, int pageSize, String... selectionArgs) {
        int startPosition = ((pageNumber - 1) * pageSize);
        return queryList(clazz, selection, selectionArgs, null, null, null, startPosition + "," + pageSize);
    }

    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String... selectionArgs) {
        return queryList(clazz, selection, selectionArgs, null, null, null, null);
    }


    public synchronized SQLiteDatabase getDatabase() {
        closeIndex++;
        return helper.getWritableDatabase();
    }

    public synchronized void close(SQLiteDatabase database) {
        closeIndex--;
        if (closeIndex == 0) {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }
}
