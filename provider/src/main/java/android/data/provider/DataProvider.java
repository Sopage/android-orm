package android.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Mr.Huang
 * @date 2017/8/25
 */
public class DataProvider extends ContentProvider {

    private URICenter uriCenter;
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        uriCenter = new URICenter(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriCenter.isQueryURI(uri)) {
            String table = uri.getQueryParameter("table");
            if ("null".equals(table)) {
                return null;
            }
            String groupBy = uri.getQueryParameter("group");
            String having = uri.getQueryParameter("having");
            String limit = uri.getQueryParameter("limit");
            if (database != null && database.isOpen()) {
                return database.query(false,
                        table,
                        projection,
                        selection,
                        selectionArgs,
                        "null".equals(groupBy) ? null : groupBy,
                        "null".equals(having) ? null : having,
                        sortOrder,
                        "null".equals(limit) ? null : limit);
            }
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "no type";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriCenter.isInsertURI(uri)) {
            String table = uri.getQueryParameter("table");
            if (database != null && database.isOpen()) {
                long i = database.insert(table, null, values);
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return uri;
                }
            }
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriCenter.isDeleteURI(uri)) {
            String table = uri.getQueryParameter("table");
            if ("null".equals(table)) {
                return -1;
            }
            if (database != null && database.isOpen()) {
                int i = database.delete(table, selection, selectionArgs);
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriCenter.isUpdateURI(uri)) {
            String table = uri.getQueryParameter("table");
            if ("null".equals(table)) {
                return -1;
            }
            if (database != null && database.isOpen()) {
                int i = database.update(table, values, selection, selectionArgs);
                if (i > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return i;
            }
        } else if (uriCenter.isSwitchURI(uri)) {
            String name = uri.getQueryParameter("database");
            database = new SQLiteOpenHelper(getContext(), name + ".db").getWritableDatabase();
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        return -1;
    }

    private static final class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

        private SQLiteOpenHelper(Context context, String db) {
            super(context, db, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS test(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, test TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
