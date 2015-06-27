package android.sqlite.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanders on 15/3/30.
 */
public class OpenHelperProxy extends SQLiteOpenHelper {

    private Collection<Class> mClasses;
    private OnDBUpgrade mUpgrade;
    private DBProxy mProxy;

    public OpenHelperProxy(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public void addTableBeans(Collection<Class> classes) {
        this.mClasses = classes;
    }

    public void setOnUpgrade(OnDBUpgrade upgrade) {
        this.mUpgrade = upgrade;
    }

    public void setDBProxy(DBProxy proxy) {
        this.mProxy = proxy;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class clazz : mClasses) {
            ClassInfo classInfo = mProxy.getClassInfo(clazz);
            String sql = classInfo.getCreateTableSql();
            db.execSQL(sql);
        }
    }

    private void upgrade(SQLiteDatabase db, int oldVersion) {
        List<String> sqlList = new ArrayList<String>();
        for (Class clazz : mClasses) {
            sqlList.clear();
            ClassInfo classInfo = mProxy.getClassInfo(clazz);
            String tableName = classInfo.getTableName();
            Cursor cursor = db.rawQuery("PRAGMA table_info(`" + tableName + "`)", null);//查询表结构
            if (cursor.getCount() < 1) {
                continue;
            }
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
                    sqlList.add("ALTER TABLE `" + tableName + "` ADD COLUMN `" + entry.getKey() + "` " + ClassInfo.getDBFieldType(entry.getValue()) + ";");
                } else if (!dbFieldMap.get(entry.getKey()).equals(ClassInfo.getDBFieldType(entry.getValue()))) {
                    sqlList.clear();
                    sqlList.add("ALTER TABLE `" + tableName + "` RENAME TO `" + tableName + "_" + oldVersion + "`;");
                    break;
                }
            }
            for (String sql : sqlList) {
                db.execSQL(sql);
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (mUpgrade == null || mUpgrade.onUpgrade(db, oldVersion, newVersion)) {
            this.upgrade(db, oldVersion);
            this.onCreate(db);
        }
    }
}
