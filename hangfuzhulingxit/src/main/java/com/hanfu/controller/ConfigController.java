package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.entity.SysConfig;
import com.hanfu.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    /**
     * 获取所有配置
     */
    @GetMapping("/list")
    public Result<List<SysConfig>> getAll() {
        return Result.success(configService.getAll());
    }

    /**
     * 添加配置
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysConfig config) {
        configService.add(config);
        return Result.success("添加成功", null);
    }

    /**
     * 更新配置
     */
    @PutMapping
    public Result<Void> update(@RequestBody SysConfig config) {
        configService.update(config);
        return Result.success("更新成功", null);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.success("删除成功", null);
    }
}
