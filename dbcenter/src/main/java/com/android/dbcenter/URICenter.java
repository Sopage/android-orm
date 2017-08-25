package com.android.dbcenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;

/**
 * @author Mr.Huang
 * @date 2017/8/25
 */
public class URICenter {

    private interface PATH {
        String SWITCH = "/switch";
        String INSERT = "/insert";
        String UPDATE = "/update";
        String DELETE = "/delete";
        String QUERY = "/query";
    }

    private Context context;
    private String authority;

    private URICenter(){

    }

    public URICenter(Context context) {
        this.context = context;
        this.authority = String.format("content://%s", getAuthority(context));
    }

    public Uri getSwitchURI(String dbName) {
        return Uri.parse(authority + PATH.SWITCH + "?database=" + dbName);
    }

    public Uri getQueryURI(String table) {
        return Uri.parse(authority + PATH.QUERY + "?table=" + table);
    }

    public Uri getInsertURI(String table) {
        return Uri.parse(authority + PATH.INSERT + "?table=" + table);
    }

    public Uri getUpdateURI(String table) {
        return Uri.parse(authority + PATH.UPDATE + "?table=" + table);
    }

    public Uri getDeleteURI(String table) {
        return Uri.parse(authority + PATH.DELETE + "?table=" + table);
    }

    private boolean checkUri(String path, Uri uri){
        return path.equals(uri.getPath());
    }

    public boolean isSwitchURI(Uri uri) {
        return checkUri(PATH.SWITCH, uri);
    }

    public boolean isQueryURI(Uri uri) {
        return checkUri(PATH.QUERY, uri);
    }

    public boolean isInsertURI(Uri uri) {
        return checkUri(PATH.INSERT, uri);
    }

    public boolean isUpdateURI(Uri uri) {
        return checkUri(PATH.UPDATE, uri);
    }

    public boolean isDeleteURI(Uri uri) {
        return checkUri(PATH.DELETE, uri);
    }

    private static String getAuthority(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return "";
        }
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
            if (info.providers != null) {
                for (ProviderInfo provider : info.providers) {
                    if (provider.name.equals(DataCenterProvider.class.getName())) {
                        return provider.authority;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
