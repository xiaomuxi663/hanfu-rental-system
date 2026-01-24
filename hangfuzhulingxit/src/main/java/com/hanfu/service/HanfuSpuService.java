package com.hanfu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.entity.HanfuSpu;

public interface HanfuSpuService {
    // 后台分页
    IPage<HanfuSpu> getPage(Integer pageNum, Integer pageSize, Integer categoryId, String name);
    // 前台列表
    IPage<HanfuSpu> getPublishList(Integer pageNum, Integer pageSize, Integer categoryId);
    // 详情
    HanfuSpu getById(Long id);
    void add(HanfuSpu spu);
    void update(HanfuSpu spu);
    void delete(Long id);
    // 上下架
    void updatePublish(Long id, Integer isPublish);
}
