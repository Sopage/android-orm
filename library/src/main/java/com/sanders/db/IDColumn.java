package com.sanders.db;

import java.io.Serializable;

/**
 * 所有数据库表对应的实体类都要继承自此类
 * 此类只统一主键为primary_key而不是_id，这样以防ID冲突
 */
public abstract class IDColumn implements Serializable {

    /**
     * 主键ID 自增长类型
     */
    public static final String PRIMARY_KEY = "primary_key";

    /**
     * 主键ID 自增长类型
     */
    private long primary_key;

    public long getPrimaryKey() {
        return primary_key;
    }

    public void setPrimaryKey(long primary_key) {
        this.primary_key = primary_key;
    }
}
