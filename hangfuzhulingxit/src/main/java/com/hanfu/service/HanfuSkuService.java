package com.hanfu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.entity.HanfuSku;
import java.util.List;

public interface HanfuSkuService {
    IPage<HanfuSku> getPage(Integer pageNum, Integer pageSize, Long spuId, Integer status);
    List<HanfuSku> getBySpuId(Long spuId);
    void add(HanfuSku sku);
    void update(HanfuSku sku);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}
