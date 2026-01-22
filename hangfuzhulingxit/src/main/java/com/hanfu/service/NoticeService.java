package com.hanfu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.entity.SysNotice;
import java.util.List;

/**
 * 公告服务接口
 */
public interface NoticeService {
    
    /** 分页查询（后台） */
    IPage<SysNotice> getPage(Integer pageNum, Integer pageSize, Integer type);
    
    /** 获取公告列表（前台） */
    List<SysNotice> getNoticeList();
    
    /** 获取轮播图列表（前台） */
    List<SysNotice> getBannerList();
    
    /** 获取详情 */
    SysNotice getById(Integer id);
    
    /** 新增 */
    void add(SysNotice notice);
    
    /** 更新 */
    void update(SysNotice notice);
    
    /** 删除 */
    void delete(Integer id);
}
