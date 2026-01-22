package com.hanfu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanfu.entity.SysNotice;
import com.hanfu.mapper.SysNoticeMapper;
import com.hanfu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 公告服务实现
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    
    private final SysNoticeMapper noticeMapper;

    @Override
    public IPage<SysNotice> getPage(Integer pageNum, Integer pageSize, Integer type) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(SysNotice::getType, type);
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        return noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<SysNotice> getNoticeList() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getType, 1)
               .orderByDesc(SysNotice::getCreateTime)
               .last("LIMIT 10");
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public List<SysNotice> getBannerList() {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getType, 2)
               .orderByDesc(SysNotice::getCreateTime);
        return noticeMapper.selectList(wrapper);
    }

    @Override
    public SysNotice getById(Integer id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public void add(SysNotice notice) {
        noticeMapper.insert(notice);
    }

    @Override
    public void update(SysNotice notice) {
        noticeMapper.updateById(notice);
    }

    @Override
    public void delete(Integer id) {
        noticeMapper.deleteById(id);
    }
}
