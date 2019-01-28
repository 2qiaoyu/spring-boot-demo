package com.joham.springbootdemo.stock;

import org.springframework.stereotype.Repository;

/**
 * 库存订单mapper
 *
 * @author joham
 */
@Repository
public interface StockOrderMapper {

    /**
     * 生成订单
     *
     * @param record
     * @return
     */
    int insertSelective(StockOrder record);
}