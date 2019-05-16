package com.joham.springbootdemo.es.repository;


import com.joham.springbootdemo.es.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * ES 操作类
 *
 * @author joham
 */
public interface CityRepository extends ElasticsearchRepository<City, Long> {

    /**
     * AND 语句查询
     *
     * @param description
     * @param provinceId
     * @return
     */
    List<City> findByDescriptionAndProvinceId(String description, Integer provinceId);

    /**
     * OR 语句查询
     *
     * @param description
     * @param provinceId
     * @return
     */
    List<City> findByDescriptionOrProvinceId(String description, Integer provinceId);

    /**
     * 查询城市描述
     * <p>
     * 等同于下面代码
     *
     * @param description
     * @param page
     * @return
     * @Query("{\"bool\" : {\"must\" : {\"term\" : {\"description\" : \"?0\"}}}}")
     * Page<City> findByDescription(String description, Pageable pageable);
     */
    Page<City> findByDescription(String description, Pageable page);

    /**
     * NOT 语句查询
     *
     * @param description
     * @param page
     * @return
     */
    Page<City> findByDescriptionNot(String description, Pageable page);

    /**
     * LIKE 语句查询
     *
     * @param description
     * @param page
     * @return
     */
    Page<City> findByDescriptionLike(String description, Pageable page);
}
