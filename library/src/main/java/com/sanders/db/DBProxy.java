package com.sanders.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanders on 15/5/17.
 */
public abstract class DBProxy {

    /**
     * 数据库操作计数，防止异常关闭问题
     */
    private int mOpenCount = 0;

    /**
     * 用于缓存实体类Class和实体类详情
     */
    private final Map<Class, ClassInfo> mClassInfoMap = new HashMap<Class, ClassInfo>();

    /**
     * 获取一个实体类Class的详细信息并缓存
     *
     * @param t
     * @param <T>
     * @return
     */
    protected final <T extends IDColumn> ClassInfo getClassInfo(T t) {
        return this.getClassInfo(t.getClass());
    }

    /**
     * 获取一个实体类Class的详细信息并缓存
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected final <T extends IDColumn> ClassInfo getClassInfo(Class<T> clazz) {
        ClassInfo classInfo = mClassInfoMap.get(clazz);
        if (classInfo == null) {
            classInfo = new ClassInfo(clazz);
        }
        return classInfo;
    }


    /**
     * 插入对应实体到数据库
     *
     * @param t
     * @param <T>
     * @return
     */
    public final <T extends IDColumn> long insert(T t) {
        if (t == null) {
            throw new NullPointerException("插入对象为NULL");
        }
        ClassInfo<T> classInfo = getClassInfo(t);
        String tableName = classInfo.getTableName();
        ContentValues values = classInfo.getContentValues(t);
        if (values.size() > 0) {
            SQLiteDatabase database = getDatabase();
            database.beginTransaction();
            long id = database.insert(tableName, null, values);
            t.setPrimaryKey(id);
            database.setTransactionSuccessful();
            database.endTransaction();
            close(database);
            return id;
        }
        return -1;
    }

    /**
     * 插入数据
     *
     * @param tableName
     * @param values
     * @return
     */
    public final long insert(String tableName, ContentValues values) {
        SQLiteDatabase database = getDatabase();
        long id = -1;
        database.beginTransaction();
        if (values.size() > 0) {
            id = database.insert(tableName, null, values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return id;
    }

    /**
     * 批量插入对应实体类到数据库。建议集合不要太大，这是一次性事务
     *
     * @param list
     * @param <T>
     */
    public final <T extends IDColumn> void insert(Collection<T> list) {
        if (isEmpty(list)) {
            return;
        }
        ClassInfo<T> classInfo = getClassInfo(list.iterator().next());
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = classInfo.getContentValues(t);
            if (values.size() > 0) {
                long id = database.insert(classInfo.getTableName(), null, values);
                t.setPrimaryKey(id);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }


    /**
     * 更具条件更新实体到数据库
     *
     * @param t
     * @param where
     * @param args
     * @param <T>
     * @return
     */
    public final <T extends IDColumn> int update(T t, String where, String... args) {
        if (t == null) {
            throw new NullPointerException("更新对象为NULL！");
        }
        if (where == null || where.trim().length() < 1) {
            throw new NullPointerException("缺少WHERE条件语句！");
        }
        ClassInfo<T> classInfo = getClassInfo(t);
        String tableName = classInfo.getTableName();
        ContentValues values = classInfo.getContentValues(t);
        if (values.size() < 1) {
            return -1;
        }
        values.remove(IDColumn.PRIMARY_KEY);
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.update(tableName, values, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    /**
     * 更新表数据
     *
     * @param tableName
     * @param values
     * @param where
     * @param args
     * @return
     */
    public final int update(String tableName, ContentValues values, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.update(tableName, values, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    /**
     * 更具实体中的主键(_key_id)更新实体到数据库
     *
     * @param t
     * @param <T>
     * @return
     */
    public final <T extends IDColumn> int update(T t) {
        long primaryKey;
        if (t == null) {
            throw new NullPointerException("更新对象为NULL！");
        } else if ((primaryKey = t.getPrimaryKey()) <= 0) {
            return -1;
        }
        return update(t, IDColumn.PRIMARY_KEY + "=" + primaryKey);
    }

    /**
     * 更具实体中的主键更新实体到数据库
     *
     * @param t
     * @param keyId
     * @param <T>
     * @return
     */
    public final <T extends IDColumn> int update(T t, long keyId) {
        return update(t, IDColumn.PRIMARY_KEY + "=" + keyId);
    }

    /**
     * 更具集合实体中的主键(_key_id)更新实体到数据库
     *
     * @param list
     * @param <T>
     */
    public final <T extends IDColumn> void update(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        ClassInfo<T> classInfo = getClassInfo(list.get(0));
        String tableName = classInfo.getTableName();
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            long primaryKey = t.getPrimaryKey();
            ContentValues values = classInfo.getContentValues(t);
            if (values.size() > 0 && primaryKey > 0) {
                values.remove(IDColumn.PRIMARY_KEY);
                database.update(tableName, values, IDColumn.PRIMARY_KEY + "=" + primaryKey, null);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public final <T extends IDColumn> long insertOrUpdate(T t) {
        if (t == null) {
            return -1;
        }
        ClassInfo<T> classInfo = getClassInfo(t);
        ContentValues values = classInfo.getContentValues(t);
        long rowId = -1;
        if (values.size() > 0) {
            if (t.getPrimaryKey() > 0) {
                rowId = update(t);
            } else {
                rowId = insert(t);
            }
        }
        return rowId;
    }

    /**
     * 插入或者更新集合
     *
     * @param list
     * @param <T>
     */
    public final <T extends IDColumn> void insertOrUpdate(List<T> list) {
        if (isEmpty(list)) {
            return;
        }
        ClassInfo<T> classInfo = getClassInfo(list.get(0));
        String tableName = classInfo.getTableName();
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (T t : list) {
            ContentValues values = classInfo.getContentValues(t);
            if (values.size() > 0) {
                long primaryKey = t.getPrimaryKey();
                if (primaryKey > 0) {
                    database.update(tableName, values, IDColumn.PRIMARY_KEY + "=" + primaryKey, null);
                } else {
                    database.insert(tableName, null, values);
                }
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    /**
     * 执行原生sql
     *
     * @param sql
     */
    public final void execSQL(String... sql) {
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        for (String s : sql) {
            database.execSQL(s);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    /**
     * 更具条件删除数据库内容
     *
     * @param clazz
     * @param where
     * @param args
     * @return
     */
    public final int delete(Class<?> clazz, String where, String... args) {
        String table = ClassInfo.conversionClassNameToTableName(clazz.getName());
        return delete(table, where, args);
    }

    /**
     * 根据主键删除数据库内容
     *
     * @param clazz
     * @param primaryKey
     * @return
     */
    public final int delete(Class<?> clazz, long primaryKey) {
        return delete(clazz, IDColumn.PRIMARY_KEY + "=" + primaryKey);
    }

    /**
     * 更具条件删除数据库内容
     *
     * @param tableName
     * @param where
     * @param args
     * @return
     */
    public final int delete(String tableName, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        int row = database.delete(tableName, where, args);
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
        return row;
    }

    /**
     * 查询数量
     *
     * @param clazz
     * @param where
     * @param args
     * @param <T>
     * @return
     */
    public <T extends IDColumn> long queryCount(Class<T> clazz, String where, String... args) {
        ClassInfo<T> classInfo = getClassInfo(clazz);
        return queryCount(classInfo.getTableName(), where, args);
    }

    /**
     * 查询数量
     *
     * @param tableName
     * @param where
     * @param args
     * @return
     */
    public <T extends IDColumn> long queryCount(String tableName, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        StringBuilder sql = new StringBuilder("SELECT COUNT(").append(IDColumn.PRIMARY_KEY).append(") AS count FROM ");
        sql.append(tableName);
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

    /**
     * 查询主键
     *
     * @param clazz
     * @param where
     * @param args
     * @param <T>
     * @return
     */
    public <T extends IDColumn> long queryPrimaryKey(Class<T> clazz, String where, String... args) {
        ClassInfo<T> classInfo = getClassInfo(clazz);
        return queryPrimaryKey(classInfo.getTableName(), where, args);
    }

    /**
     * 查询主键
     *
     * @param tableName
     * @param where
     * @param args
     * @return
     */
    public long queryPrimaryKey(String tableName, String where, String... args) {
        if (where == null) {
            throw new NullPointerException("缺少WHERE条件语句！");
        }
        SQLiteDatabase database = getDatabase();
        StringBuilder sql = new StringBuilder("SELECT ").append(IDColumn.PRIMARY_KEY).append(" FROM ").append(tableName).append(" WHERE ").append(where);
        Cursor cursor = database.rawQuery(sql.toString(), args);
        long id = -1;
        if (cursor.moveToNext()) {
            id = cursor.getLong(0);
        }
        close(cursor);
        close(database);
        return id;
    }

    /**
     * 根据条件查询一条记录到实体
     *
     * @param clazz
     * @param where
     * @param args
     * @param <T>
     * @return
     */
    public <T extends IDColumn> T query(Class<T> clazz, String where, String... args) {
        SQLiteDatabase database = getDatabase();
        ClassInfo<T> classInfo = getClassInfo(clazz);
        Cursor cursor = database.query(classInfo.getTableName(), null, where, args, null, null, null);
        T t = classInfo.getInstanceObject(cursor);
        close(cursor);
        close(database);
        return t;
    }

    /**
     * 根据主键查询实体
     *
     * @param clazz
     * @param primaryKey
     * @param <T>
     * @return
     */
    public <T extends IDColumn> T query(Class<T> clazz, long primaryKey) {
        return query(clazz, IDColumn.PRIMARY_KEY + "=" + primaryKey);
    }

    /**
     * 根据sql语句查询实体
     *
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    public <T extends IDColumn> T queryBySql(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        ClassInfo<T> classInfo = getClassInfo(clazz);
        T t = classInfo.getInstanceObject(cursor);
        close(cursor);
        close(database);
        return t;
    }

    /**
     * 根据sql语句查询实体集合
     *
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    public <T extends IDColumn> List<T> queryListBySql(Class<T> clazz, String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        ClassInfo<T> classInfo = getClassInfo(clazz);
        List<T> list = classInfo.getInstanceList(cursor);
        close(cursor);
        close(database);
        return list;
    }

    /**
     * 更具条件插叙实体集合
     *
     * @param clazz
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @param <T>
     * @return
     */
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        ClassInfo<T> classInfo = getClassInfo(clazz);
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.query(classInfo.getTableName(), null, selection, selectionArgs, groupBy, having, orderBy, limit);
        List<T> list = classInfo.getInstanceList(cursor);
        close(cursor);
        close(database);
        return list;
    }

    /**
     * 分页查询实体集合
     *
     * @param clazz
     * @param selection
     * @param pageNumber
     * @param pageSize
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, int pageNumber, int pageSize, String... selectionArgs) {
        int startPosition = ((pageNumber - 1) * pageSize);
        return queryList(clazz, selection, selectionArgs, null, null, null, startPosition + "," + pageSize);
    }

    /**
     * 根据条件查询实体集合
     *
     * @param clazz
     * @param selection
     * @param selectionArgs
     * @param <T>
     * @return
     */
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String... selectionArgs) {
        return queryList(clazz, selection, selectionArgs, null, null, null, null);
    }

    /**
     * <b>此方法适用于Build.VERSION_CODES.HONEYCOMB以上版本</b><br>
     * 查询一条记录到Map
     *
     * @param sql
     * @param args
     * @return
     */
    public Map<String, Object> query(String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        String[] names = cursor.getColumnNames();
        Map<String, Object> map = null;
        if (cursor.moveToNext()) {
            map = new HashMap<String, Object>();
            for (String columnName : names) {
                this.putMapKeyValue(cursor, columnName, map);
            }
        }
        close(cursor);
        close(database);
        return map;
    }

    /**
     * <b>此方法适用于Build.VERSION_CODES.HONEYCOMB以上版本</b><br>
     * 根据sql语句查询map到list
     *
     * @param sql
     * @param args
     * @return
     */
    public List<Map<String, Object>> queryListBySql(String sql, String... args) {
        SQLiteDatabase database = getDatabase();
        Cursor cursor = database.rawQuery(sql, args);
        String[] names = cursor.getColumnNames();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (String columnName : names) {
                putMapKeyValue(cursor, columnName, map);
            }
            list.add(map);
        }
        close(cursor);
        close(database);
        return list;
    }

    /**
     * 封装内容到Map对象
     * @param cursor
     * @param columnName
     * @param map
     */
    private void putMapKeyValue(Cursor cursor, String columnName, Map<String, Object> map) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            map.put(columnName, cursor.getString(columnIndex));
        } else {
            int type = cursor.getType(columnIndex);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    map.put(columnName, cursor.getLong(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    map.put(columnName, cursor.getString(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    map.put(columnName, cursor.getFloat(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    map.put(columnName, cursor.getBlob(columnIndex));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    map.put(columnName, cursor.getString(columnIndex));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 拿到数据库操作类SQLiteDatabase并打开数据库计数加1
     * @return
     */
    private SQLiteDatabase getDatabase() {
        mOpenCount++;
        return getCreateDatabase();
    }

    /**
     * 获取数据可SQLiteDatabase，需要实现此方法
     * @return
     */
    public abstract SQLiteDatabase getCreateDatabase();

    /**
     * 关闭数据库SQLiteDatabase，打开数据库计数减1
     * @param database
     */
    private void close(SQLiteDatabase database) {
        mOpenCount--;
        if (mOpenCount == 0 && database != null && database.isOpen()) {
            database.close();
        }
    }

    /**
     * 关闭Cursor
     * @param cursor
     */
    private void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * 集合NULL判断
     * @param collection
     * @return
     */
    private boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 返回当前数据库打开关闭计数
     * @return
     */
    public int getOpenCount() {
        return mOpenCount;
    }

    /**
     * 转换Boolean值用于数据库查询
     *
     * @param b
     * @return
     */
    public final String getBooleanValue(boolean b) {
        if (b) {
            return "1";
        }
        return "0";
    }
}
