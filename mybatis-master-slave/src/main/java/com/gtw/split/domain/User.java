package com.gtw.split.domain;

import lombok.Data;

@Data
public class User {
    private long id;
    private String userName;
    private String passWord;
    private Sex userSex;
    private String nickName;

    public enum Sex{
        MAN, WOMAN;
    }
}
