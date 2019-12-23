package com.jt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.pojo.Cart;
import com.jt.pojo.Order;
import com.jt.service.DubboCartService;
import com.jt.service.DubboOrderService;
import com.jt.util.UserThreadLocal;
import com.jt.vo.SysResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    @Reference
    private DubboCartService dubboCartService;

    @Reference
    private DubboOrderService dubboOrderService;

    @RequestMapping(value = "/create")
    public String create(Model model) {
        Long userId = UserThreadLocal.get().getId();
        List<Cart> cartList = dubboCartService.findCartListByUserId(userId);
        model.addAttribute("carts", cartList);
        return "order-cart";
    }

    /**
     * 订单提交
     *
     * @param order
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public SysResult saveOrder(Order order) {
        SysResult sysResult = new SysResult();
        //获取用户Id
        Long userId = UserThreadLocal.get().getId();
        order.setUserId(userId);
        String orderId = dubboOrderService.saveOrder(order);
        //删除购物车的内容
        return sysResult.setData(orderId);
    }

    @RequestMapping(value = "/success")
    public String findOrderByID(String id, Model model) {
        Order order = dubboOrderService.findOrderByID(id);
        model.addAttribute("order", order);
        return "success";
    }

}
