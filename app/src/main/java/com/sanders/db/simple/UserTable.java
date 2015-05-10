package com.sanders.db.simple;

import com.sanders.db.IDColumn;

/**
 * Created by sanders on 15/3/28.
 */
public class UserTable extends IDColumn {

    private String userName;
    private String passWord;
    private String email;
    private String idCard;
    private int age;

    public UserTable(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }
}
