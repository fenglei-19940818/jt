package com.jt.web;

import com.jt.pojo.Item;
import com.jt.service.ItemService;
import com.jt.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/cart")
@RestController
public class CartController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/queryItemPrice")
    public String queryItemPrice(Long itemId) {
        Item item = itemService.findItemById(itemId);
        return JsonUtil.getBeanToJson(item);
    }
}
