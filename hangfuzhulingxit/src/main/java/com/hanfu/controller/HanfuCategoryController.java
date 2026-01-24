package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.entity.HanfuCategory;
import com.hanfu.service.HanfuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class HanfuCategoryController {
    private final HanfuCategoryService categoryService;

    @GetMapping("/list")
    public Result list() {
        return Result.success(categoryService.getAll());
    }

    @PostMapping("/add")
    public Result add(@RequestBody HanfuCategory category) {
        categoryService.add(category);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody HanfuCategory category) {
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return Result.success();
    }
}
