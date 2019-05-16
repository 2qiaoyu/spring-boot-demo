
package com.joham.springbootdemo.es.service;

import com.joham.springbootdemo.es.domain.City;
import com.joham.springbootdemo.es.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 城市 ES 业务接口类
 *
 * @author joham
 */
@Service
@Slf4j
public class CityService {

    Pageable pageable = new PageRequest(0, 10);

    /**
     * 每页数量
     */
    private static final Integer PAGE_SIZE = 12;

    /**
     * 默认当前页码
     */
    private static final Integer DEFAULT_PAGE_NUMBER = 0;

    /**
     * 权重分求和模式
     */
    private static final String SCORE_MODE_SUM = "sum";

    /**
     * 由于无相关性的分值默认为 1 ，设置权重分最小值为 10
     */
    private static final Float MIN_SCORE = 10.0F;

    @Autowired
    CityRepository cityRepository;

    public Long saveCity(City city) {
        City cityResult = cityRepository.save(city);
        return cityResult.getId();
    }

    public List<City> searchCity(Integer pageNumber, Integer pageSize, String searchContent) {

        // 校验分页参数
        if (pageSize == null || pageSize <= 0) {
            pageSize = PAGE_SIZE;
        }

        if (pageNumber == null || pageNumber < DEFAULT_PAGE_NUMBER) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        log.info("\n searchCity: searchContent [" + searchContent + "] \n ");

        // 构建搜索查询
        SearchQuery searchQuery = getCitySearchQuery(pageNumber, pageSize, searchContent);

        log.info("\n searchCity: searchContent [" + searchContent + "] \n DSL  = \n " + searchQuery.getQuery().toString());

        Page<City> cityPage = cityRepository.search(searchQuery);
        return cityPage.getContent();
    }

    /**
     * 根据搜索词构造搜索查询语句
     * <p>
     * 代码流程：
     * - 权重分查询
     * - 短语匹配
     * - 设置权重分最小值
     * - 设置分页参数
     *
     * @param pageNumber    当前页码
     * @param pageSize      每页大小
     * @param searchContent 搜索内容
     * @return
     */
    private SearchQuery getCitySearchQuery(Integer pageNumber, Integer pageSize, String searchContent) {
        // 短语匹配到的搜索词，求和模式累加权重分
        // 权重分查询 https://www.elastic.co/guide/cn/elasticsearch/guide/current/function-score-query.html
        //   - 短语匹配 https://www.elastic.co/guide/cn/elasticsearch/guide/current/phrase-matching.html
        //   - 字段对应权重分设置，可以优化成 enum
        //   - 由于无相关性的分值默认为 1 ，设置权重分最小值为 10
//        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
//                .add(QueryBuilders.matchPhraseQuery("cityName", searchContent),
//                        ScoreFunctionBuilders.weightFactorFunction(1000))
//                .add(QueryBuilders.matchPhraseQuery("description", searchContent),
//                        ScoreFunctionBuilders.weightFactorFunction(500))
//                .scoreMode(SCORE_MODE_SUM).setMinScore(MIN_SCORE);

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("cityname", searchContent)),
                        ScoreFunctionBuilders.weightFactorFunction(1000))
                .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("description", searchContent)),
                        ScoreFunctionBuilders.weightFactorFunction(100));


        // 分页参数
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        return new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
    }

    public List<City> findByDescriptionAndProvinceId(String description, Integer provinceId) {
        return cityRepository.findByDescriptionAndProvinceId(description, provinceId);
    }

    public List<City> findByDescriptionOrProvinceId(String description, Integer provinceId) {
        return cityRepository.findByDescriptionOrProvinceId(description, provinceId);
    }

    public List<City> findByDescription(String description) {
        return cityRepository.findByDescription(description, pageable).getContent();
    }

    public List<City> findByDescriptionNot(String description) {
        return cityRepository.findByDescriptionNot(description, pageable).getContent();
    }

    public List<City> findByDescriptionLike(String description) {
        return cityRepository.findByDescriptionLike(description, pageable).getContent();
    }
}