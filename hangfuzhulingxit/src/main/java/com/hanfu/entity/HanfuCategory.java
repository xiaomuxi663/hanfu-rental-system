package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hanfu_category")
public class HanfuCategory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String categoryName;
    private Integer sort;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
