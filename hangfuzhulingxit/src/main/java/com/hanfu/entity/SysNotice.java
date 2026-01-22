package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统公告/轮播图实体
 */
@Data
@TableName("sys_notice")
public class SysNotice {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 标题 */
    private String title;
    
    /** 内容 */
    private String content;
    
    /** 类型: 1公告, 2轮播图 */
    private Integer type;
    
    /** 图片地址(仅轮播图用) */
    private String imgUrl;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
