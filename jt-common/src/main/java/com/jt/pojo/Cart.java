package com.jt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) //表示JSON转化时忽略未知属性
@TableName("tb_cart")
@Accessors(chain = true)
public class Cart extends BasePojo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;    //用户ID号
    private Long itemId;    //商品ID号
    private String itemTitle;
    private String itemImage;
    private Long itemPrice;
    private Integer num;


}
