package com.sanders.db.simple;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sanders.db.DBProxy;
import com.sanders.db.Table1;
import com.sanders.db.TableBean;
import com.sanders.db.Table_2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanders on 15/3/27.
 */
public class SimpleActivity extends Activity implements View.OnClickListener {

    public DBProxy db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
//        db = new ProxyDB(new SimpleOpenHelper(this));
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
//        for (int i = 0; i < linearLayout.getChildCount(); i++) {
//            linearLayout.getChildAt(i).setOnClickListener(this);
//        }

        db = new DBProxy.DBBuilder()
                .builderDbName("test")
                .builderDbVersion(1)
                .builderTable(Table1.class).builderTable(Table_2.class).builderTable(TableBean.class)
                .build(this);
        List<Table1> list = db.queryList(Table1.class, null, null);
        for(Table1 t : list){
            Log.e("ESA", t.getKeyId()+"");
        }
//        db.queryList(Table1.class, null, null);
//
//        try {
//            String path = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).sourceDir;
//            DexFile dexfile = new DexFile(path);
//            Enumeration<String> entries = dexfile.entries();
//            while (entries.hasMoreElements()) {
//                String name = entries.nextElement();
////                if(name.startsWith("com.sanders.db") && !name.contains("$"))
//                    Log.e("ESA", name);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    long id = 1;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_insert:
                long startTime = System.currentTimeMillis();
                UserTable table = new UserTable("username", "password", 100);
                db.insert(table);
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                Log.e("ESA", "KEY_ID="+table.getKeyId());
                break;
            case R.id.btn_insert_list:
                List<UserTable> list = new ArrayList<UserTable>();
                for (int i = 1; i <= 10; i++) {
                    list.add(new UserTable("username" + i, "password" + i, i));
                }
                startTime = System.currentTimeMillis();
                db.insert(list);
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                for(int i=0; i<10; i++){
                    Log.e("ESA", "KEY_ID=" + list.get(i).getKeyId());
                }
                break;
            case R.id.btn_update:
                table = new UserTable("update", "update", 100);
                table.setKeyId(id);
                startTime = System.currentTimeMillis();
                db.update(table);
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                id++;
                break;
            case R.id.btn_update_list:
                list = new ArrayList<UserTable>();
                for(int i=11; i<=20; i++){
                    table = new UserTable("UPDATE", "UPDATE", 0);
                    table.setKeyId(i);
                    list.add(table);
                }
                startTime = System.currentTimeMillis();
                db.update(list);
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                break;
            case R.id.btn_delete:
                startTime = System.currentTimeMillis();
                db.delete(UserTable.class, "age=?", String.valueOf(12));
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                break;
            case R.id.btn_query:
                db.query(UserTable.class, 1);
                break;
            case R.id.btn_query_list:
                startTime = System.currentTimeMillis();
                list = db.queryList(UserTable.class, "age=?", String.valueOf(100));
                Log.e("ESA", String.valueOf(System.currentTimeMillis() - startTime));
                for(UserTable t : list){
                    Log.e("ESA", "KEY_ID=" + t.getKeyId());
                }
                break;
        }
    }
}
