package com.hanfu.controller;

import com.hanfu.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("汉服租赁系统后端启动成功！");
    }
}
