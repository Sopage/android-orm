package com.sanders.db.simple;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

/**
 * Created by sanders on 15/3/27.
 */
public class SimpleActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.btn_insert_list).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
    }

    int index = 1;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_insert:
                ContentValues values = new ContentValues();
                values.put("test", "1");
                index++;
                break;
            case R.id.btn_insert_list:
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
