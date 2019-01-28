package com.joham.springbootdemo.stock;

import lombok.Data;

import java.io.Serializable;

/**
 * 库存
 *
 * @author joham
 */
@Data
public class Stock implements Serializable {

    private static final long serialVersionUID = -8437012513227627973L;

    private Integer id;

    private String name;

    private Integer count;

    private Integer sale;

    private Integer version;
}