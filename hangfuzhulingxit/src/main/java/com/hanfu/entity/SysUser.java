package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String realName;

    private String idCard;

    private String phone;

    private String defaultReceiverName;

    private String defaultReceiverPhone;

    private String defaultAddress;

    private String avatar;

    private Integer creditScore;

    private BigDecimal balance;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 角色标识(非数据库字段)
     */
    @TableField(exist = false)
    private String roleKey;
}
