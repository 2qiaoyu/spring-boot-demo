package com.joham.springbootdemo.user;

import lombok.Data;

/**
 * @author joham
 */
@Data
public class User1 {

    private Long id;

    private DeleteFlag userName = DeleteFlag.NO;

}
