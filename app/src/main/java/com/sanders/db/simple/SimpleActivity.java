package com.sanders.db.simple;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.sanders.db.DBContext;
import com.sanders.db.DBFile;
import com.sanders.db.DBProxy;
import com.sanders.db.OnDBUpgrade;

import java.io.File;

/**
 * Created by sanders on 15/3/27.
 */
public class SimpleActivity extends Activity implements View.OnClickListener {

    public DBProxy db;
    public DBProxy db2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            linearLayout.getChildAt(i).setOnClickListener(this);
        }

        DBContext dbContext = new DBContext("database", 3, new OnDBUpgrade() {
            @Override
            public boolean onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                return false;
            }
        });
        dbContext.addTableBean(TableModel.class).addTableBean(TableBean.class);
        db = dbContext.buildDBProxy(this);
        dbContext = new DBContext("database", 3, null);
        dbContext.addTableBean(TableModel.class).addTableBean(TableBean.class);
        db2 = dbContext.buildDBProxy(this);

        DBFile dbFile = new DBFile("/mnt/sdcard/database.db");
        db = dbFile.buildDBProxy();
        dbFile = new DBFile(new File("/mnt/sdcard/database.db"));
        db2 = dbFile.buildDBProxy();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_insert:
                for (int i = 0; i < 10; i++) {
                    new Thread() {
                        @Override
                        public void run() {
                            int j = 100;
                            do {
                                long count = db.queryCount(TableModel.class, null);
                                Log.e("ESA", "query count --------------" + count);
                                j--;
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (j > 0);
                        }
                    }.start();
                    new Thread() {
                        @Override
                        public void run() {
                            int j = 100;
                            do {
                                long id = db.insert(new TableModel(Short.parseShort("1"), 1, 1l, 1d, 1f, true));
                                Log.e("ESA", "insert id++++++++++++" + id);
                                j--;
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } while (j > 0);
                        }
                    }.start();
                }
                break;
            case R.id.btn_insert_list:
                Log.e("ESA", "db count=" + db.getOpenCount() + "    db count=" + db.getOpenCount());
                break;
            case R.id.btn_update:
                break;
            case R.id.btn_update_list:
                break;
            case R.id.btn_delete:
                break;
            case R.id.btn_query:
                break;
            case R.id.btn_query_list:
                break;
        }
    }
}
