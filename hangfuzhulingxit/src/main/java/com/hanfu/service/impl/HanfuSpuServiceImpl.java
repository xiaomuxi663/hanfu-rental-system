package com.hanfu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanfu.entity.HanfuCategory;
import com.hanfu.entity.HanfuSpu;
import com.hanfu.mapper.HanfuCategoryMapper;
import com.hanfu.mapper.HanfuSpuMapper;
import com.hanfu.service.HanfuSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HanfuSpuServiceImpl implements HanfuSpuService {
    private final HanfuSpuMapper spuMapper;
    private final HanfuCategoryMapper categoryMapper;

    @Override
    public IPage<HanfuSpu> getPage(Integer pageNum, Integer pageSize, Integer categoryId, String name) {
        LambdaQueryWrapper<HanfuSpu> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(HanfuSpu::getCategoryId, categoryId);
        }
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(HanfuSpu::getName, name);
        }
        wrapper.orderByDesc(HanfuSpu::getCreateTime);
        IPage<HanfuSpu> page = spuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        // 填充分类名称和库存
        page.getRecords().forEach(this::fillSpuInfo);
        return page;
    }

    @Override
    public IPage<HanfuSpu> getPublishList(Integer pageNum, Integer pageSize, Integer categoryId) {
        LambdaQueryWrapper<HanfuSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HanfuSpu::getIsPublish, 1);
        if (categoryId != null) {
            wrapper.eq(HanfuSpu::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(HanfuSpu::getCreateTime);
        IPage<HanfuSpu> page = spuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(this::fillSpuInfo);
        return page;
    }

    @Override
    public HanfuSpu getById(Long id) {
        HanfuSpu spu = spuMapper.selectById(id);
        if (spu != null) {
            fillSpuInfo(spu);
        }
        return spu;
    }

    @Override
    public void add(HanfuSpu spu) {
        spu.setIsPublish(1);
        spuMapper.insert(spu);
    }

    @Override
    public void update(HanfuSpu spu) {
        spuMapper.updateById(spu);
    }

    @Override
    public void delete(Long id) {
        spuMapper.deleteById(id);
    }

    @Override
    public void updatePublish(Long id, Integer isPublish) {
        HanfuSpu spu = new HanfuSpu();
        spu.setId(id);
        spu.setIsPublish(isPublish);
        spuMapper.updateById(spu);
    }

    private void fillSpuInfo(HanfuSpu spu) {
        HanfuCategory category = categoryMapper.selectById(spu.getCategoryId());
        if (category != null) {
            spu.setCategoryName(category.getCategoryName());
        }
        spu.setStockCount(spuMapper.getAvailableStock(spu.getId()));
    }
}
