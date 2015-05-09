package com.sanders.db;

import java.io.Serializable;

/**
 * Created by sanders on 15/3/21.
 * 所有数据库表对应的实体类都要继承自此类
 * 此类只统一主键为_primary_key_id而不是_id，这样以防ID冲突
 */
public class IDColumn implements Serializable {

    /**
     * 主键ID 自增长类型
     */
    public static final String PRIMARY_KEY_ID = "_primary_key_id";

    /**
     * 主键ID 自增长类型
     */
    private long _primary_key_id;

    public long getPrimaryKeyId() {
        return _primary_key_id;
    }

    public void setPrimaryKeyId(long _primary_key_id) {
        this._primary_key_id = _primary_key_id;
    }

}
