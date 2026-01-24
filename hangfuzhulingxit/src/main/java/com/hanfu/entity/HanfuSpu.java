package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hanfu_spu")
public class HanfuSpu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer categoryId;
    private String name;
    private BigDecimal dailyRent;
    private BigDecimal deposit;
    private String mainImage;
    private String subImages;
    private String detailContent;
    private Integer isPublish;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private Integer stockCount;
}
