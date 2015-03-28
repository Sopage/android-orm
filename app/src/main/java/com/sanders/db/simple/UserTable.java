package com.sanders.db.simple;

import com.sanders.db.IDColumn;

/**
 * Created by sanders on 15/3/28.
 */
public class UserTable extends IDColumn{

    private String userName;
    private String passWord;
    private int age;

    public UserTable() {
    }

    public UserTable(String userName, String passWord, int age) {
        this.userName = userName;
        this.passWord = passWord;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
