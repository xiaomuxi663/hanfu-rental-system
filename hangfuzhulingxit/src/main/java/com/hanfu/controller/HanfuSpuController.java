package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.entity.HanfuSpu;
import com.hanfu.service.HanfuSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hanfu")
@RequiredArgsConstructor
public class HanfuSpuController {
    private final HanfuSpuService spuService;

    // 后台分页查询
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String name) {
        return Result.success(spuService.getPage(pageNum, pageSize, categoryId, name));
    }

    // 前台已上架列表
    @GetMapping("/list")
    public Result list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) Integer categoryId) {
        return Result.success(spuService.getPublishList(pageNum, pageSize, categoryId));
    }

    // 详情
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(spuService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody HanfuSpu spu) {
        spuService.add(spu);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody HanfuSpu spu) {
        spuService.update(spu);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        spuService.delete(id);
        return Result.success();
    }

    // 上下架
    @PutMapping("/publish/{id}")
    public Result updatePublish(@PathVariable Long id, @RequestParam Integer isPublish) {
        spuService.updatePublish(id, isPublish);
        return Result.success();
    }
}
