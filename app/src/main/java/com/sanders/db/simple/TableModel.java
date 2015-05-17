package com.sanders.db.simple;

import com.sanders.db.IDColumn;

/**
 * Created by sanders on 15/3/28.
 */
public class TableModel extends IDColumn {

    private Short fieldShort;
    private Integer fieldInt;
    private Long fieldLong;
    private Double fieldDouble;
    private Float fieldFloat;
    private Boolean fieldBoolean;

    public TableModel() {
    }

    public TableModel(Short fieldShort, Integer fieldInt, Long fieldLong, Double fieldDouble, Float fieldFloat, Boolean fieldBoolean) {
        this.fieldShort = fieldShort;
        this.fieldInt = fieldInt;
        this.fieldLong = fieldLong;
        this.fieldDouble = fieldDouble;
        this.fieldFloat = fieldFloat;
        this.fieldBoolean = fieldBoolean;
    }

    public Short getFieldShort() {
        return fieldShort;
    }

    public void setFieldShort(Short fieldShort) {
        this.fieldShort = fieldShort;
    }

    public Integer getFieldInt() {
        return fieldInt;
    }

    public void setFieldInt(Integer fieldInt) {
        this.fieldInt = fieldInt;
    }

    public Long getFieldLong() {
        return fieldLong;
    }

    public void setFieldLong(Long fieldLong) {
        this.fieldLong = fieldLong;
    }

    public Double getFieldDouble() {
        return fieldDouble;
    }

    public void setFieldDouble(Double fieldDouble) {
        this.fieldDouble = fieldDouble;
    }

    public Float getFieldFloat() {
        return fieldFloat;
    }

    public void setFieldFloat(Float fieldFloat) {
        this.fieldFloat = fieldFloat;
    }

    public Boolean getFieldBoolean() {
        return fieldBoolean;
    }

    public void setFieldBoolean(Boolean fieldBoolean) {
        this.fieldBoolean = fieldBoolean;
    }
}
