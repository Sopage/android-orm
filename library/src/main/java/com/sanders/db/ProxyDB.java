package com.sanders.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sanders on 15/2/8.
 * 请保证此类为单例<br>
 * 约定：1. 表名称必须以小写字母开始，单词之间用“_”下划线分开（当然也可以全部小写不分开）如：table_name、table_xxx、xx_xx
 * 2. 表名称和实体类对应如下：
 * 表：table_name 对应的实体类：TableName;
 * 类：TableName 对应表名：table_name
 * 3. 表字段和实体类字段对应如下：
 * 表字段：field_name 对应实体类字段：fieldName.
 * 实体类字段：fieldName 对应实体类字段：field_name.
 * 4. 主键在IDColumn类里面有说明
 */
public final class ProxyDB implements ProxyOperation {

    /**
     * Class的Field的缓存
     */
    private Map<Class, Map<String, Field>> cacheFieldMaps = new HashMap<Class, Map<String, Field>>();
    private SQLiteOpenHelper helper;
    /**
     * 关闭数据库计数
     */
    private int closeIndex = 0;

    public ProxyDB(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    /**
     * 插入对象
     *
     * @param t
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T extends IDColumn> long insert(T t) {
        if (t == null) {
            return -1;
        }
        Class clazz = t.getClass();
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        ContentValues values = conversionValues(clazz, t);
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

    /**
     * 批量插入
     *
     * @param list
     * @param <T>
     */
    @Override
    public synchronized <T extends IDColumn> void insert(List<T> list) {
        if (DBUtils.isEmpty(list)) {
            return;
        }
        Class clazz = list.get(0).getClass();
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = conversionValues(clazz, t);
            long id = database.insert(tableName, null, values);
            t.setKeyId(id);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    /**
     * 更具条件更新T对象
     *
     * @param t
     * @param where
     * @param args
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T extends IDColumn> int update(T t, String where, String... args) {
        if (t == null) {
            return -1;
        }
        if (where == null) {
            throw new NullPointerException("缺少WHERE条件语句！");
        }
        Class clazz = t.getClass();
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        ContentValues values = conversionValues(clazz, t);
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

    /**
     * 更新对象。对象内的key_id不能小于0
     *
     * @param t
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T extends IDColumn> int update(T t) {
        long keyId;
        if (t == null || (keyId = t.getKeyId()) < 1) {
            return -1;
        }
        return update(t, IDColumn.KEY_ID + "=" + keyId, null);
    }

    /**
     * 更新对象。keyId不能小于0
     *
     * @param t
     * @param <T>
     * @return
     */
    @Override
    public synchronized <T extends IDColumn> int update(T t, long keyId) {
        if (t == null) {
            return -1;
        }
        return update(t, IDColumn.KEY_ID + "=" + keyId, null);
    }

    /**
     * 当集合中T对象的key_id大于0的时候执行更新操作否则此T对象作废
     *
     * @param list
     * @param <T>
     */
    @Override
    public synchronized <T extends IDColumn> void update(List<T> list) {
        if (DBUtils.isEmpty(list)) {
            return;
        }
        Class clazz = list.get(0).getClass();
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            long keyId = t.getKeyId();
            ContentValues values = conversionValues(clazz, t);
            if (values != null && keyId > 0) {
                values.remove(IDColumn.KEY_ID);
                database.update(tableName, values, IDColumn.KEY_ID + "=" + keyId, null);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    /**
     * 集合中的T的key_id大于0的时候执行更新操作反之为插入操作
     *
     * @param list
     * @param <T>
     */
    @Override
    public synchronized <T extends IDColumn> void insertOrUpdate(List<T> list) {
        if (DBUtils.isEmpty(list)) {
            return;
        }
        Class clazz = list.get(0).getClass();
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = conversionValues(clazz, t);
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

    @Override
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

    @Override
    public synchronized int delete(Class<?> clazz, long keyId) {
        return delete(clazz, IDColumn.KEY_ID + "=" + keyId, null);
    }

    @Override
    public synchronized int delete(Class<?> clazz, String where, String... args) {
        String table = DBUtils.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.delete(table, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    @Override
    public <T extends IDColumn> T query(Class<T> clazz, long keyId) {
        return query(clazz, IDColumn.KEY_ID + "=" + keyId, null);
    }

    @Override
    public <T extends IDColumn> T query(Class<T> clazz, String where, String... args) {
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        return querySql(clazz, "SELECT * FROM " + tableName + " WHERE " + where, args);
    }

    @Override
    public <T extends IDColumn> T querySql(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        T t = null;
        if (cursor.moveToNext()) {
            t = setClassValue(clazz, cursor);
        }
        DBUtils.close(cursor);
        close(database);
        return t;
    }

    @Override
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String... selectionArgs) {
        return queryList(clazz, selection, selectionArgs, null, null, null, null);
    }

    @Override
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, int pageNumber, int pageSize, String... selectionArgs) {
        int startPosition = ((pageNumber - 1) * pageSize);
        return queryList(clazz, selection, selectionArgs, null, null, null, startPosition + "," + pageSize);
    }

    @Override
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.query(tableName, null, selection, selectionArgs, groupBy, having, orderBy, limit);
        List<T> list = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T t = setClassValue(clazz, cursor);
            if (t != null) {
                list.add(t);
            }
        }
        DBUtils.close(cursor);
        close(database);
        return list;
    }

    @Override
    public <T extends IDColumn> List<T> querySqlList(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        List<T> list = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T t = setClassValue(clazz, cursor);
            if (t != null) {
                list.add(t);
            }
        }
        DBUtils.close(cursor);
        close(database);
        return list;
    }

    public synchronized SQLiteDatabase getDatabase() {
        closeIndex++;
        return helper.getWritableDatabase();
    }

    public synchronized void close(SQLiteDatabase database) {
        closeIndex--;
        if (closeIndex == 0) {
            DBUtils.close(database);
        }
    }

    private <T extends IDColumn> Map<String, Field> getCacheFields(Class<T> clazz) throws NoSuchFieldException {
        Map<String, Field> fieldMap;
        fieldMap = cacheFieldMaps.get(clazz);
        if (DBUtils.isNotEmpty(fieldMap)) {
            return fieldMap;
        }
        fieldMap = new HashMap<String, Field>();
        Field superField = clazz.getSuperclass().getDeclaredField(IDColumn.KEY_ID);
        superField.setAccessible(true);
        fieldMap.put(IDColumn.KEY_ID, superField);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (modifiers == 25 || modifiers == 26 || modifiers == 28) {
                continue;
            }
            String fieldName = field.getName();
            field.setAccessible(true);
            fieldMap.put(fieldName, field);
        }
        cacheFieldMaps.put(clazz, fieldMap);
        return fieldMap;
    }

    private <T extends IDColumn> T setClassValue(Class<T> clazz, Cursor cursor) {
        try {
            String[] columnNames = cursor.getColumnNames();
            Map<String, Field> fieldMap = getCacheFields(clazz);
            T t = clazz.newInstance();
            for (String columnName : columnNames) {
                int index = cursor.getColumnIndex(columnName);
                Field field;
                if (IDColumn.KEY_ID.equals(columnName)) {
                    field = fieldMap.get(IDColumn.KEY_ID);
                } else {
                    String fieldName = DBUtils.conversionDBFieldNameToJavaFileName(columnName);
                    field = fieldMap.get(fieldName);
                }
                DBUtils.setFieldValue(t, field, cursor, index);
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private <T extends IDColumn> ContentValues conversionValues(Class<T> clazz, T t) {
        ContentValues values = new ContentValues();
        try {
            Map<String, Field> fieldMap = getCacheFields(clazz);
            Collection<Field> collection = fieldMap.values();
            Iterator<Field> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                DBUtils.getFieldValue(field, t, values);
            }
            return values;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }


}
