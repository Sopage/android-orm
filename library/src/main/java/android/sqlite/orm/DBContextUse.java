package android.sqlite.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Created by sanders on 15/6/20.
 */
public class DBContextUse {
    private String name;
    private int version;
    private OnDBUpgrade upgrade;
    private Collection<String> mSqlList = new LinkedHashSet<String>();

    public DBContextUse(String name, int version, OnDBUpgrade upgrade) {
        this.name = name;
        this.version = version;
        this.upgrade = upgrade;
    }

    public DBContextUse addCreateTableSql(String sql) {
        this.mSqlList.add(sql);
        return this;
    }

    public DBProxy buildDBProxy(Context context) {
        final OpenHelper helper = new OpenHelper(context, name, version);
        helper.addCreateTableSqlList(mSqlList);
        helper.setOnUpgrade(upgrade);
        DBProxy proxy = new DBProxy() {
            @Override
            public SQLiteDatabase getCreateDatabase() {
                return helper.getWritableDatabase();
            }
        };
        return proxy;
    }
}
