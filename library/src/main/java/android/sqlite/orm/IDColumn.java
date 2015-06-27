package android.sqlite.orm;

import java.io.Serializable;

/**
 * 所有数据库表对应的实体类都要继承自此类
 * 此类只统一主键为primary_key而不是_id，这样以防ID冲突
 */
public abstract class IDColumn implements Serializable {

    /**
     * 主键ID 自增长类型
     */
    public static final String PRIMARY_ID = "_id";

    /**
     * 主键ID 自增长类型
     */
    private long _id;

    public long getPrimaryId() {
        return _id;
    }

    public void setPrimaryId(long id) {
        this._id = id;
    }
}
