package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.entity.HanfuSku;
import com.hanfu.service.HanfuSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class HanfuSkuController {
    private final HanfuSkuService skuService;

    // 分页查询
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long spuId,
            @RequestParam(required = false) Integer status) {
        return Result.success(skuService.getPage(pageNum, pageSize, spuId, status));
    }

    // 根据SPU获取库存列表
    @GetMapping("/list/{spuId}")
    public Result listBySpuId(@PathVariable Long spuId) {
        return Result.success(skuService.getBySpuId(spuId));
    }

    @PostMapping("/add")
    public Result add(@RequestBody HanfuSku sku) {
        skuService.add(sku);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody HanfuSku sku) {
        skuService.update(sku);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        skuService.delete(id);
        return Result.success();
    }

    // 更新状态
    @PutMapping("/status/{id}")
    public Result updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        skuService.updateStatus(id, status);
        return Result.success();
    }
}
