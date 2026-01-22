package com.hanfu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.common.Result;
import com.hanfu.entity.SysNotice;
import com.hanfu.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告/轮播图控制器
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 前台接口 - 获取公告列表
     */
    @GetMapping("/list")
    public Result<List<SysNotice>> getNoticeList() {
        return Result.success(noticeService.getNoticeList());
    }

    /**
     * 前台接口 - 获取轮播图
     */
    @GetMapping("/banners")
    public Result<List<SysNotice>> getBanners() {
        return Result.success(noticeService.getBannerList());
    }

    /**
     * 前台接口 - 公告详情
     */
    @GetMapping("/{id}")
    public Result<SysNotice> getDetail(@PathVariable Integer id) {
        return Result.success(noticeService.getById(id));
    }

    /**
     * 后台接口 - 分页查询
     */
    @GetMapping("/admin/page")
    public Result<IPage<SysNotice>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        return Result.success(noticeService.getPage(pageNum, pageSize, type));
    }

    /**
     * 后台接口 - 新增
     */
    @PostMapping("/admin")
    public Result<Void> add(@RequestBody SysNotice notice) {
        noticeService.add(notice);
        return Result.success("添加成功", null);
    }

    /**
     * 后台接口 - 更新
     */
    @PutMapping("/admin")
    public Result<Void> update(@RequestBody SysNotice notice) {
        noticeService.update(notice);
        return Result.success("更新成功", null);
    }

    /**
     * 后台接口 - 删除
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        noticeService.delete(id);
        return Result.success("删除成功", null);
    }
}
