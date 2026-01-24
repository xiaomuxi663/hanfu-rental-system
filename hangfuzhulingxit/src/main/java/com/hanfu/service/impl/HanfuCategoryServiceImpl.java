package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanfu.entity.HanfuCategory;
import com.hanfu.mapper.HanfuCategoryMapper;
import com.hanfu.service.HanfuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HanfuCategoryServiceImpl implements HanfuCategoryService {
    private final HanfuCategoryMapper categoryMapper;

    @Override
    public List<HanfuCategory> getAll() {
        LambdaQueryWrapper<HanfuCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(HanfuCategory::getSort);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public void add(HanfuCategory category) {
        categoryMapper.insert(category);
    }

    @Override
    public void update(HanfuCategory category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Integer id) {
        categoryMapper.deleteById(id);
    }
}
