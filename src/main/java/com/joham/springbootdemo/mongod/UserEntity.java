package com.joham.springbootdemo.mongod;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 会员实体
 *
 * @author joham
 */

@QueryEntity
@Data
public class UserEntity{

    @NotNull
    private Long id;

    @NotNull
    private Integer age;

    @NotNull
    private String userName;

    @NotNull
    private String passWord;
}
