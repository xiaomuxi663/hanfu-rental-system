package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanfu.entity.SysConfig;
import com.hanfu.mapper.SysConfigMapper;
import com.hanfu.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 系统配置服务实现
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    
    private final SysConfigMapper configMapper;

    @Override
    public List<SysConfig> getAll() {
        return configMapper.selectList(null);
    }

    @Override
    public String getValue(String key) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getParamKey, key);
        SysConfig config = configMapper.selectOne(wrapper);
        return config != null ? config.getParamValue() : null;
    }

    @Override
    public void update(SysConfig config) {
        configMapper.updateById(config);
    }

    @Override
    public void add(SysConfig config) {
        configMapper.insert(config);
    }

    @Override
    public void delete(Long id) {
        configMapper.deleteById(id);
    }
}
