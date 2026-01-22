package com.hanfu.service;

import com.hanfu.entity.SysConfig;
import java.util.List;

/**
 * 系统配置服务接口
 */
public interface ConfigService {
    
    /** 获取所有配置 */
    List<SysConfig> getAll();
    
    /** 根据key获取值 */
    String getValue(String key);
    
    /** 添加配置 */
    void add(SysConfig config);
    
    /** 更新配置 */
    void update(SysConfig config);
    
    /** 删除配置 */
    void delete(Long id);
}
