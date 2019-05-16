package com.joham.springbootdemo.es.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * 城市实体类
 *
 * @author joham
 */
@Document(indexName = "province", type = "city")
@Data
public class City implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 城市编号
     */
    private Long id;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;

    /**
     * 省
     */
    private Long provinceId;
}
