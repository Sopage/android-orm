package com.sanders.db.simple;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.android.dbcenter.DataCenter;
import com.android.dbcenter.URICenter;

/**
 * Created by sanders on 15/3/27.
 */
public class SimpleActivity extends Activity implements View.OnClickListener {

    DataCenter dataCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.btn_insert_list).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        dataCenter = new DataCenter(getApplicationContext());
    }

    int index = 1;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_insert:
                ContentValues values = new ContentValues();
                values.put("test", "1");
                dataCenter.getDataInterface().insert("test", values);
                index++;
                break;
            case R.id.btn_insert_list:
                dataCenter.getDataInterface().switchs("db" + index);
                break;
            case R.id.btn_update:
                Cursor cursor = dataCenter.getDataInterface().query("test", null, null, null, null, null, null, null);
                while (cursor.moveToNext()){
                    String s = cursor.getString(cursor.getColumnIndex("test"));
                    System.out.println(s);
                }
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
