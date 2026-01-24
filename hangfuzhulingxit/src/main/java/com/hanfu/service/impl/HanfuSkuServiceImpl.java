package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanfu.entity.HanfuSku;
import com.hanfu.entity.HanfuSpu;
import com.hanfu.mapper.HanfuSkuMapper;
import com.hanfu.mapper.HanfuSpuMapper;
import com.hanfu.service.HanfuSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HanfuSkuServiceImpl implements HanfuSkuService {
    private final HanfuSkuMapper skuMapper;
    private final HanfuSpuMapper spuMapper;

    @Override
    public IPage<HanfuSku> getPage(Integer pageNum, Integer pageSize, Long spuId, Integer status) {
        LambdaQueryWrapper<HanfuSku> wrapper = new LambdaQueryWrapper<>();
        if (spuId != null) {
            wrapper.eq(HanfuSku::getSpuId, spuId);
        }
        if (status != null) {
            wrapper.eq(HanfuSku::getStatus, status);
        }
        wrapper.orderByDesc(HanfuSku::getCreateTime);
        IPage<HanfuSku> page = skuMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        page.getRecords().forEach(sku -> {
            HanfuSpu spu = spuMapper.selectById(sku.getSpuId());
            if (spu != null) {
                sku.setSpuName(spu.getName());
            }
        });
        return page;
    }

    @Override
    public List<HanfuSku> getBySpuId(Long spuId) {
        LambdaQueryWrapper<HanfuSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HanfuSku::getSpuId, spuId);
        return skuMapper.selectList(wrapper);
    }

    @Override
    public void add(HanfuSku sku) {
        sku.setStatus(0);
        skuMapper.insert(sku);
    }

    @Override
    public void update(HanfuSku sku) {
        skuMapper.updateById(sku);
    }

    @Override
    public void delete(Long id) {
        skuMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        HanfuSku sku = new HanfuSku();
        sku.setId(id);
        sku.setStatus(status);
        skuMapper.updateById(sku);
    }
}
