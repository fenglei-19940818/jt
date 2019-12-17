package com.jt.web;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.pojo.ItemDesc;
import com.jt.util.JsonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JSONPController {

    @RequestMapping("web/testJSONP")
    public JSONPObject jsonp(String callback) {
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(1001L);
        itemDesc.setItemDesc("详情");
        JSONPObject jsonpObject = new JSONPObject(callback, itemDesc);
        return jsonpObject;
    }

//    @RequestMapping("web/testJSONPOld")
//    public String jsonpOld(String callback) {
//        ItemDesc itemDesc = new ItemDesc();
//        itemDesc.setItemId(1001L);
//        itemDesc.setItemDesc("详情");
//        String json = JsonUtil.getBeanToJson(itemDesc);
//        return callback + "(" + json + ")";
//    }
}
