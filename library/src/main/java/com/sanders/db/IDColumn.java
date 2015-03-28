package com.sanders.db;

import java.io.Serializable;

/**
 * Created by sanders on 15/3/21.
 * 所有数据库表对应的实体类都要继承自此类
 * 此类只统一主键为_key_id而不是_id，这样以防ID冲突
 */
public class IDColumn implements Serializable {

    /**
     * 主键ID 自增长类型
     */
    public static final String KEY_ID = "_key_id";

    /**
     * 主键ID 自增长类型
     */
    private long _key_id;

    public long getKeyId() {
        return _key_id;
    }

    public void setKeyId(long id) {
        this._key_id = id;
    }

}
