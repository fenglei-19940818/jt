package com.jt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jt.pojo.Cart;
import com.jt.pojo.Item;
import com.jt.service.DubboCartService;
import com.jt.util.HttpClientService;
import com.jt.vo.SysResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/cart")
@PropertySource("classpath:/properties/url.properties")
public class CartController {

    @Reference(check = false)
    private DubboCartService cartService;

    @Autowired
    private HttpClientService httpClientService;

    @Value("${manage.url}")
    private String manageUrl;

    private Long userId = 7L;

    /**
     * 跳转到购物车展现页面
     *
     * @return
     */
    @RequestMapping(value = "/show")
    public String show(Model model) {
        List<Cart> cartList = cartService.findCartListByUserId(userId);
        model.addAttribute("cartList", cartList);
        return "cart";

    }

    /**
     * 购物车添加商品
     *
     * @param cart
     * @param model
     * @return
     */
    @RequestMapping(value = "/add/{itemId}")
    public String add(Cart cart, Model model) {
        //查询商品价格
        Map<String, String> map = new HashMap<>();
        map.put("itemId", cart.getItemId().toString());
        Item item = httpClientService.doGet(manageUrl + "cart/queryItemPrice", map, Item.class, "utf-8");
        //添加购物车并修改价格(以防万一有人恶意修改)
        cartService.addCart(cart.setUserId(userId).setItemPrice(item.getPrice()));
        return "redirect:/cart/show.html";
    }

    /**
     * 修改购物车商品数量
     *
     * @param cart
     * @return
     */
    @RequestMapping(value = "/update/num/{itemId}/{num}")
    @ResponseBody
    public SysResult updateCartNum(Cart cart) {
        //修改数量
        cartService.updateCartNum(cart.setUserId(userId));
        return SysResult.success();
    }

    @RequestMapping(value = "/delete/{itemId}")
    public String deleteCart(Cart cart) {
        //删除商品
        cartService.deleteCart(cart.setUserId(userId));
        return "redirect:/cart/show.html";
    }
}
