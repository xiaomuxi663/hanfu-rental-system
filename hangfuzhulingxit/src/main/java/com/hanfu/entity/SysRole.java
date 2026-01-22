package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String roleName;

    private String roleKey;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
