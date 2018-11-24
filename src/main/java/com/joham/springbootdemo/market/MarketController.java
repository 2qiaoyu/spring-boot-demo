package com.joham.springbootdemo.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author joham
 */
@RestController
@RequestMapping("market")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @Autowired
    private LockService lockService;

    /**
     * 查询
     *
     * @param itemId
     * @param sellerId
     * @param price
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public boolean listItem(String itemId, String sellerId, long price) {
        return marketService.listItem(itemId, sellerId, price);
    }

    /**
     * 普通购买
     *
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @return
     */
    @RequestMapping(value = "purchase", method = RequestMethod.POST)
    public boolean purchaseItem(String buyerId, String itemId, String sellerId) {
        return marketService.purchaseItem(buyerId, itemId, sellerId);
    }

    /**
     * 锁购买
     *
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @return
     */
    @RequestMapping(value = "purchase1", method = RequestMethod.POST)
    public boolean purchaseItemWithLock(String buyerId, String itemId, String sellerId) {
        return lockService.purchaseItemWithLock(buyerId, itemId, sellerId);
    }

    /**
     * 锁购买
     *
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @return
     */
    @RequestMapping(value = "purchase2", method = RequestMethod.POST)
    public boolean timeoutLockService(String buyerId, String itemId, String sellerId) {
        return lockService.purchaseItemWithTimeLock(buyerId, itemId, sellerId);
    }
}
