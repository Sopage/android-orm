package com.sanders.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanders on 15/4/4.
 */
public class ClassInfo<T extends IDColumn> {

    private Class<T> mClass;
    private String mTableName;
    private Map<String, Field> mFieldMap = new LinkedHashMap<String, Field>();

    public ClassInfo(Class<T> clazz) {
        this.mClass = clazz;
        this.mTableName = conversionClassNameToTableName(clazz.getName());
        try {
            Field superField = clazz.getSuperclass().getDeclaredField(IDColumn.PRIMARY_KEY);
            superField.setAccessible(true);
            mFieldMap.put(IDColumn.PRIMARY_KEY, superField);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (modifiers == 25 || modifiers == 26 || modifiers == 28) {
                    continue;
                }
                field.setAccessible(true);
                mFieldMap.put(conversionJavaFieldNameToDBFieldName(field.getName()), field);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String getTableName() {
        return mTableName;
    }

    public Map<String, Field> getFieldMap() {
        return mFieldMap;
    }

    public ContentValues getContentValues(T t) {
        ContentValues values = new ContentValues();
        for (Map.Entry<String, Field> entry : mFieldMap.entrySet()) {
            try {
                String key = entry.getKey();
                if (IDColumn.PRIMARY_KEY.equals(key)) {
                    continue;
                }
                putFieldValue(key, entry.getValue(), t, values);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public T getInstanceObject(Cursor cursor) {
        try {
            String[] columnNames = cursor.getColumnNames();
            if (cursor.moveToNext()) {
                T t = mClass.newInstance();
                for (String columnName : columnNames) {
                    int index = cursor.getColumnIndex(columnName);
                    Field field;
                    if (IDColumn.PRIMARY_KEY.equals(columnName)) {
                        field = mFieldMap.get(IDColumn.PRIMARY_KEY);
                    } else {
                        field = mFieldMap.get(columnName);
                    }
                    if (field != null) {
                        setFieldValue(t, field, cursor, index);
                    }
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> getInstanceList(Cursor cursor) {
        List<T> list = new ArrayList<T>();
        String[] columnNames = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            try {
                T t = mClass.newInstance();
                for (String columnName : columnNames) {
                    int index = cursor.getColumnIndex(columnName);
                    Field field;
                    if (IDColumn.PRIMARY_KEY.equals(columnName)) {
                        field = mFieldMap.get(IDColumn.PRIMARY_KEY);
                    } else {
                        field = mFieldMap.get(columnName);
                    }
                    if (field != null) {
                        setFieldValue(t, field, cursor, index);
                    }
                }
                list.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String getCreateTableSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(this.mTableName).append("` (`").append(IDColumn.PRIMARY_KEY).append("` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");
        for (Map.Entry<String, Field> entry : this.mFieldMap.entrySet()) {
            String javaField = entry.getKey();
            if (IDColumn.PRIMARY_KEY.equals(javaField)) {
                continue;
            }
            String tableField = conversionJavaFieldNameToDBFieldName(javaField);
            sql.append(", `").append(tableField).append("` ").append(getDBFieldType(entry.getValue()));
        }
        sql.append(");");
        return sql.toString();
    }

    public static String getDBFieldType(Field field) {
        String type = "NULL";
        Class<?> classType = field.getType();
        if (classType.equals(String.class) || classType.equals(CharSequence.class)) {
            type = "TEXT";
        } else if (classType.equals(Integer.TYPE) || classType.equals(Integer.class) || classType.equals(Long.TYPE) || classType.equals(Long.class) || classType.equals(Short.TYPE) || classType.equals(Short.class) || classType.equals(Date.class) || classType.equals(Boolean.TYPE) || classType.equals(Boolean.class)) {
            type = "INTEGER";
        } else if (classType.equals(Double.TYPE) || classType.equals(Double.class) || classType.equals(Float.TYPE) || classType.equals(Float.class)) {
            type = "REAL";
        } else if (classType.equals(byte[].class)) {
            type = "BLOB";
        }
        return type;
    }

    private static <T extends IDColumn> void putFieldValue(String fieldName, Field field, T t, ContentValues values) throws IllegalAccessException {
        Class<?> classType = field.getType();
        if (classType.equals(Integer.TYPE)) {
            values.put(fieldName, field.getInt(t));
        } else if (classType.equals(Integer.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Integer) value);
            }
        } else if (classType.equals(String.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (String) value);
            }
        } else if (classType.equals(Boolean.TYPE)) {
            values.put(fieldName, field.getBoolean(t));
        } else if (classType.equals(Boolean.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Boolean) value);
            }
        } else if (classType.equals(Long.TYPE)) {
            values.put(fieldName, field.getLong(t));
        } else if (classType.equals(Long.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Long) value);
            }
        } else if (classType.equals(Double.TYPE)) {
            values.put(fieldName, field.getDouble(t));
        } else if (classType.equals(Double.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Double) value);
            }
        } else if (classType.equals(Float.TYPE)) {
            values.put(fieldName, field.getFloat(t));
        } else if (classType.equals(Float.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Float) value);
            }
        } else if (classType.equals(byte[].class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (byte[]) value);
            }
        } else if (classType.equals(Short.TYPE)) {
            values.put(fieldName, field.getShort(t));
        } else if (classType.equals(Short.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, (Short) value);
            }
        } else if (classType.equals(Date.class)) {
            Object value = field.get(t);
            if (value != null) {
                values.put(fieldName, ((Date) value).getTime());
            }
        }
    }

    private static final <T extends IDColumn> void setFieldValue(T t, Field field, Cursor cursor, int index) throws IllegalAccessException {
        Class<?> classType = field.getType();
        if (classType.equals(Integer.TYPE) || classType.equals(Integer.class)) {
            field.set(t, cursor.getInt(index));
        } else if (classType.equals(String.class)) {
            field.set(t, cursor.getString(index));
        } else if (classType.equals(Boolean.TYPE) || classType.equals(Boolean.class)) {
            field.set(t, cursor.getInt(index) == 1 ? true : false);
        } else if (classType.equals(Long.TYPE) || classType.equals(Long.class)) {
            field.set(t, cursor.getLong(index));
        } else if (classType.equals(Double.TYPE) || classType.equals(Double.class)) {
            field.set(t, cursor.getDouble(index));
        } else if (classType.equals(Float.TYPE) || classType.equals(Float.class)) {
            field.set(t, cursor.getFloat(index));
        } else if (classType.equals(byte[].class)) {
            field.set(t, cursor.getBlob(index));
        } else if (classType.equals(Short.TYPE) || classType.equals(Short.class)) {
            field.set(t, cursor.getShort(index));
        } else if (classType.equals(Date.class)) {
            field.set(t, new Date(cursor.getLong(index)));
        }
    }

    public static String conversionClassNameToTableName(String className) {
        className = className.substring(className.lastIndexOf(".") + 1, className.length());
        char[] chars = className.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        sb.delete(0, 1);
        return sb.toString();
    }

    public static String conversionJavaFieldNameToDBFieldName(String fieldName) {
        char[] chars = fieldName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}