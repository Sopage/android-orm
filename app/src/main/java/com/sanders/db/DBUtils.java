package com.sanders.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by sanders on 15/2/8.
 */
public class DBUtils {


    /**
     * 将类名转为数据库表名
     *
     * @param className
     * @return
     */
    public static String conversionClassNameToTableName(String className) {
        className = className.substring(className.lastIndexOf(".") + 1, className.length()).toLowerCase();
        return className;
    }

    /**
     * 将Java字段名字转成数据库字段名字
     *
     * @param fieldName
     * @return
     */
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

    /**
     * 将数据库字段名字转成Java字段名字
     *
     * @param dbFieldName
     * @return
     */
    public static String conversionDBFieldNameToJavaFileName(String dbFieldName) {
        char[] chars = dbFieldName.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean b = false;
        for (char c : chars) {
            if (c == '_') {
                b = true;
            } else {
                if (b) {
                    sb.append(Character.toUpperCase(c));
                    b = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取泛型类字段值
     *
     * @param field
     * @param t
     * @param values
     * @param <T>
     * @throws IllegalAccessException
     */
    public static <T extends IDColumn> void getFieldValue(Field field, T t, ContentValues values) throws IllegalAccessException {
        String fieldName = conversionJavaFieldNameToDBFieldName(field.getName());
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

    /**
     * 设置泛型类字段值
     *
     * @param t
     * @param field
     * @param cursor
     * @param index
     * @param <T>
     * @throws IllegalAccessException
     */
    public static <T extends IDColumn> void setFieldValue(T t, Field field, Cursor cursor, int index) throws IllegalAccessException {
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

    public static boolean isEmpty(Map map) {
        if (map == null || map.size() < 1) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() < 1) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
