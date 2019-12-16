package com.jt.service;

import com.jt.pojo.Item;
import com.jt.pojo.ItemDesc;
import com.jt.util.HttpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:/properties/url.properties")
public class ItemServiceImpl implements ItemService {

    @Value("${manage.url}")
    private String manageUrl;

    @Autowired
    private HttpClientService httpClientService;

    @Override
    public Item findItemById(Long itemId) {
        String url = manageUrl + "web/item/findItemById?itemId=" + itemId;
        Item item = httpClientService.doGet(url, Item.class);
        return item;
    }

    @Override
    public ItemDesc findItemDescById(Long itemId) {
        String url = manageUrl + "web/item/findItemDescById?itemId=" + itemId;
        ItemDesc itemDesc = httpClientService.doGet(url, ItemDesc.class);
        return itemDesc;
    }
}
