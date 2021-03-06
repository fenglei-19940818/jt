package com.jt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) //表示JSON转化时忽略未知属性
@TableName("tb_user")
@Accessors(chain = true)
public class User extends BasePojo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    /**
     * md5实现加密
     */
    private String password;
    private String phone;
    private String email;
}
