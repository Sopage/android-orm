package com.sanders.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanders on 15/3/30.
 */
public class DefaultSQLiteOpenHelper extends SQLiteOpenHelper {

    private Set<Class> classes;
    private Map<Class, Map<String, Field>> cacheFieldMaps;
    private OnDBUpgrade upgrade;

    public DefaultSQLiteOpenHelper(Context context, String dbName, int dbVersion, Set<Class> classes, Map<Class, Map<String, Field>> cacheFieldMaps, OnDBUpgrade upgrade) {
        super(context, dbName, null, dbVersion);
        this.classes = classes;
        this.cacheFieldMaps = cacheFieldMaps;
        this.upgrade = upgrade;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            try {
                String sql = getCreateTableSql(iterator.next());
                if (DBUtils.isEmpty(sql)) {
                    continue;
                }
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

    private <T extends IDColumn> String getCreateTableSql(Class<T> clazz) throws NoSuchFieldException {
        String tableName = DBUtils.conversionClassNameToTableName(clazz.getName());
//        StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS ").append(tableName).append(";");
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE `").append(tableName).append("` (`").append(IDColumn.KEY_ID).append("` INTEGER NOT NULL PRIMARY KEY");
        Map<String, Field> fieldMap = DBUtils.getCacheFields(cacheFieldMaps, clazz);
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String javaField = entry.getKey();
            if (IDColumn.KEY_ID.equals(javaField)) {
                continue;
            }
            String tableField = DBUtils.conversionJavaFieldNameToDBFieldName(javaField);
            sql.append(", `").append(tableField).append("` ").append(DBUtils.getDBFieldType(entry.getValue()));
        }
        sql.append(");");
        return sql.toString();
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
