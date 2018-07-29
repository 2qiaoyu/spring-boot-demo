package com.joham.springbootdemo.mongod;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员实体
 *
 * @author joham
 */
@Data
public class UserEntity implements Serializable {

    private static final long serialVersionUID = -3258839839160856613L;

    private Long id;

    private String userName;

    private String passWord;
}
