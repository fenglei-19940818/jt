package com.jt.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jt.mapper.CartMapper;
import com.jt.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class
DubboCartServiceImpl implements DubboCartService {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 查询用户购物车里的商品列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<Cart> findCartListByUserId(Long userId) {
        //创建查询条件
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>(new Cart().setUserId(userId));
        //查询
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        return cartList;
    }

    /**
     * 向购物车中添加商品
     *
     * @param cart
     */
    @Override
    public void addCart(Cart cart) {

        //创建查询条件
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", cart.getUserId());
        queryWrapper.eq("item_id", cart.getItemId());
        //查询该商品是否存在
        Cart cartDB = cartMapper.selectOne(queryWrapper);
        //判断  如果不存在则直接添加商品  若存在修改数据库数量(相加)
        if (cartDB == null) {
            cartMapper.insert(cart);
        } else {
            Integer num = cart.getNum() + cartDB.getNum();
            cartDB.setNum(num);
            cartMapper.updateById(cartDB);
        }

    }

    @Override
    public void updateCartNum(Cart cart) {
        //创建查询条件
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", cart.getUserId());
        queryWrapper.eq("item_id", cart.getItemId());
        cartMapper.update(new Cart().setNum(cart.getNum()), queryWrapper);
    }

    @Override
    public void deleteCart(Cart cart) {
        //创建查询条件
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>(cart);
        cartMapper.delete(queryWrapper);
    }
}
