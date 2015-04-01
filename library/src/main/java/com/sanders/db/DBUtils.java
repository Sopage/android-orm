package com.sanders.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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

    public static <T extends IDColumn> Map<String, Field> getCacheFields(Map<Class, Map<String, Field>> cacheFieldMaps, Class<T> clazz) throws NoSuchFieldException {
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

    /**
     * Android中此方法不能够使用！！！
     * @param packageName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class> getClassFileNames(String packageName) throws IOException, ClassNotFoundException {
        Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        if (enumeration.hasMoreElements()) {
            URL url = enumeration.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                File classFile = new File(url.getFile());
                String[] classFileNames = classFile.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if (name.contains("$") || !name.contains(".class")) {
                            return false;
                        }
                        return true;
                    }
                });
                if (classFileNames != null && classFileNames.length > 0) {
                    List<Class> list = new ArrayList<Class>();
                    for (String className : classFileNames) {
                        Class clazz = Class.forName(packageName + "." + className.replace(".class",""));
                        list.add(clazz);
                    }
                    return list;
                }
            }
        }
        return java.util.Collections.emptyList();
    }

    /**
     * 返回每个java字段对应的数据库类型
     * @param field
     * @return
     */
    public static String getDBFieldType(Field field){
        String type = "NULL";
        Class<?> classType = field.getType();
        if (classType.equals(Integer.TYPE) || classType.equals(Integer.class)) {
            type = "INTEGER";
        } else if (classType.equals(String.class)) {
            type = "TEXT";
        } else if (classType.equals(Boolean.TYPE) || classType.equals(Boolean.class)) {
            type = "INTEGER";
        } else if (classType.equals(Long.TYPE) || classType.equals(Long.class)) {
            type = "INTEGER";
        } else if (classType.equals(Double.TYPE) || classType.equals(Double.class)) {
            type = "REAL";
        } else if (classType.equals(Float.TYPE) || classType.equals(Float.class)) {
            type = "REAL";
        } else if (classType.equals(byte[].class)) {
            type = "BLOB";
        } else if (classType.equals(Short.TYPE) || classType.equals(Short.class)) {
            type = "INTEGER";
        } else if (classType.equals(Date.class)) {
            type = "INTEGER";
        }
        return type;
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
