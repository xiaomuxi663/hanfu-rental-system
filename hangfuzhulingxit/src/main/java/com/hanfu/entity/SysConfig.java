package com.hanfu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统配置实体
 */
@Data
@TableName("sys_config")
public class SysConfig {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 参数键 */
    private String paramKey;
    
    /** 参数值 */
    private String paramValue;
    
    /** 参数说明 */
    private String paramDesc;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
