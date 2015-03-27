package com.sanders.db;

import java.io.Serializable;

/**
 * Created by sanders on 15/3/21.
 */
public class IDColumn implements Serializable {

    public static final String KEY_ID = "_key_id";

    protected Long _key_id;

    public long getKeyId() {
        return _key_id;
    }

    public void setKeyId(long id) {
        this._key_id = id;
    }

}
