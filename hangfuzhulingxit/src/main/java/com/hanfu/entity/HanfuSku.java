package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hanfu_sku")
public class HanfuSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long spuId;
    private String skuCode;
    private String size;
    private Integer status;  // 0在库, 1已租, 2清洗中, 3维修中, 4报废
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String spuName;
}
