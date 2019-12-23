package com.jt.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.mapper.OrderItemMapper;
import com.jt.mapper.OrderMapper;
import com.jt.mapper.OrderShippingMapper;
import com.jt.pojo.Order;
import com.jt.pojo.OrderItem;
import com.jt.pojo.OrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DubboOrderServiceImpl implements DubboOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderShippingMapper orderShippingMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public String saveOrder(Order order) {
        //生成订单id
        String orderId = "" + order.getUserId() + System.currentTimeMillis();
        order.setOrderId(orderId).setStatus(1);
        //订单入库
        orderMapper.insert(order);
        System.out.println("订单录入成功");
        OrderShipping shipping = order.getOrderShipping();
        shipping.setOrderId(orderId);
        //订单物流入库成功
        orderShippingMapper.insert(shipping);
        System.out.println("订单物流入库成功");
        List<OrderItem> orderItems = order.getOrderItems();
        //订单商品入库成功
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(orderId);
            orderItemMapper.insert(orderItem);
        }
        System.out.println("订单商品入库成功");
        return orderId;
    }

    @Override
    public Order findOrderByID(String orderId) {
        //查询order
        Order order = orderMapper.selectById(orderId);
        //查询orderShipping
        OrderShipping shipping = orderShippingMapper.selectById(orderId);
        //查询orderItem
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id", orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        if (order != null && shipping != null && orderItems != null) {
            order.setOrderShipping(shipping);
            order.setOrderItems(orderItems);
            return order;
        }
        return null;
    }
}
