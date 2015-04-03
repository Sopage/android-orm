package com.sanders.db;

import java.util.Date;

/**
 * Created by sanders on 15/3/30.
 */
public class Table1 extends IDColumn{

    private int f1;
    private String f2;
    private long f3;
    private Date f4;
    private byte[] f5;

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public long getF3() {
        return f3;
    }

    public void setF3(long f3) {
        this.f3 = f3;
    }

    public Date getF4() {
        return f4;
    }

    public void setF4(Date f4) {
        this.f4 = f4;
    }

    public byte[] getF5() {
        return f5;
    }

    public void setF5(byte[] f5) {
        this.f5 = f5;
    }
}
