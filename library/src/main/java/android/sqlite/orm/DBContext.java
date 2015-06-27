package android.sqlite.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by sanders on 15/5/17.
 */
public class DBContext {

    private String name;
    private int version;
    private OnDBUpgrade upgrade;
    private Collection<Class> tableBeans = new LinkedHashSet<Class>();

    public DBContext(String name, int version, OnDBUpgrade upgrade) {
        this.name = name;
        this.version = version;
        this.upgrade = upgrade;
    }

    public DBContext addTableBean(Class clazz) {
        this.tableBeans.add(clazz);
        return this;
    }

    public DBProxy buildDBProxy(Context context) {
        final OpenHelperProxy helper = new OpenHelperProxy(context, name, version);
        helper.addTableBeans(tableBeans);
        helper.setOnUpgrade(upgrade);
        DBProxy proxy = new DBProxy() {
            @Override
            public SQLiteDatabase getCreateDatabase() {
                return helper.getWritableDatabase();
            }
        };
        helper.setDBProxy(proxy);
        return proxy;
    }

}
