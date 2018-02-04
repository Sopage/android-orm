package com.sanders.db.simple;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.dbcenter.URICenter;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sanders on 15/3/27.
 */
public class SimpleActivity extends Activity implements View.OnClickListener {

    public static String content = "m=init&lang=en&uuid=87A7D2AD-1CB5-4F70-8DAF-971C05788E46";
    public static byte[] keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        try {
            keys = "mobilejump".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        findViewById(R.id.btn_insert).setOnClickListener(this);
        System.out.println(encrypt(content));
        Log.e("ESA", encrypt(content));
    }

    public static String encrypt(String content) {
        try {

            byte[] key = new byte[16];
            int length = keys.length;
            for(int i=0; i< length; i++){
                if(i > 15){
                    break;
                }
                key[i] = keys[i];
            }
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(content.getBytes("UTF8"));
            String encryptedString = new String(Base64.encodeToString(cipherText, Base64.NO_PADDING));
            return encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    int index = 1;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_insert:
                URICenter uriCenter = new URICenter(getApplicationContext());
                getContentResolver().update(uriCenter.getSwitchURI("test" + index), new ContentValues(), null, null);
                index ++;
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
