package com.joham.springbootdemo.stock;

import org.springframework.stereotype.Repository;

/**
 * 库存mapper
 *
 * @author joham
 */
@Repository
public interface StockMapper {

    /**
     * 查询库存
     *
     * @param id
     * @return
     */
    Stock getStockById(Integer id);

    /**
     * 乐观锁更新库存
     *
     * @param stock
     * @return
     */
    int updateStockByOptimistic(Stock stock);

    /**
     * 普通更新库存
     *
     * @param stock
     * @return
     */
    int updateStock(Stock stock);
}