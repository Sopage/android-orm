package android.sqlite.orm;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by sanders on 15/5/17.
 */
public class DBFile {

    private File dbFile;

    public DBFile(File dbFile) {
        this.dbFile = dbFile;
    }

    public DBFile(String dbFilePath) {
        this.dbFile = new File(dbFilePath);
    }

    public DBProxy buildDBProxy() {
        DBProxy proxy = new DBProxy() {
            private SQLiteDatabase database;

            @Override
            public SQLiteDatabase getCreateDatabase() {
                if (database == null || !database.isOpen()) {
                    database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
                }
                return database;
            }
        };
        return proxy;
    }

}
