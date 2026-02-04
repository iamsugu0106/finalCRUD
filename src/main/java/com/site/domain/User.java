package com.site.domain;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private String id;
    private String password;
    private String name;
    private String email;
    private Date createAt;

}
