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

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public boolean listItem(String itemId, String sellerId, long price) {
        return marketService.listItem(itemId, sellerId, price);
    }

    @RequestMapping(value = "purchase" ,method = RequestMethod.POST)
    public boolean purchaseItem(String buyerId, String itemId, String sellerId) {
        return marketService.purchaseItem(buyerId, itemId, sellerId);
    }
}
