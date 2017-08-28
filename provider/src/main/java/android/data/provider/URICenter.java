package android.data.provider;

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
        String SWITCH = "switch";
        String INSERT = "insert";
        String UPDATE = "update";
        String DELETE = "delete";
        String QUERY = "query";
    }

    private String authority;

    public URICenter(Context context) {
        this.authority = getAuthority(context);
    }

    public Uri getSwitchURI(String dbName) {
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .path(PATH.SWITCH)
                .appendQueryParameter("database", dbName)
                .build();
    }

    public Uri getQueryURI(String table, String group, String having, String limit) {
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .path(PATH.QUERY)
                .appendQueryParameter("table", table)
                .appendQueryParameter("group", group)
                .appendQueryParameter("having", having)
                .appendQueryParameter("limit", limit)
                .build();
    }

    public Uri getInsertURI(String table) {
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .path(PATH.INSERT)
                .appendQueryParameter("table", table)
                .build();
    }

    public Uri getUpdateURI(String table) {
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .path(PATH.UPDATE)
                .appendQueryParameter("table", table)
                .build();
    }

    public Uri getDeleteURI(String table) {
        return new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .path(PATH.DELETE)
                .appendQueryParameter("table", table)
                .build();
    }

    private boolean checkUri(String path, Uri uri) {
        return uri.getPath().startsWith("/" + path);
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
                    if (provider.name.equals(DataProvider.class.getName())) {
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
