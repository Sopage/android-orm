package com.sanders.db;

import java.util.List;

/**
 * Created by sanders on 15/3/27.
 */
public interface ProxyOperation {
    public <T extends IDColumn> long insert(T t);
    public <T extends IDColumn> void insert(List<T> list);
    public <T extends IDColumn> int update(T t, String where, String... args);
    public <T extends IDColumn> int update(T t);
    public <T extends IDColumn> int update(T t, long keyId);
    public <T extends IDColumn> void update(List<T> list);
    public <T extends IDColumn> void update(List<T> list, String where, String... args);
    public <T extends IDColumn> void insertOrUpdate(List<T> list);
    public void execSQL(String... sql);
    public int delete(Class<?> clazz, long keyId);
    public int delete(Class<?> clazz, String where, String... args);
    public <T extends IDColumn> T query(Class<T> clazz, long keyId);
    public <T extends IDColumn> T query(Class<T> clazz, String where, String... args);
    public <T extends IDColumn> T querySql(Class<T> clazz, String sql, String... args);
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String... selectionArgs);
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, int pageNumber, int pageSize, String... selectionArgs);
    public <T extends IDColumn> List<T> queryList(Class<T> clazz, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit);
    public <T extends IDColumn> List<T> querySqlList(Class<T> clazz, String sql, String... args);
}
