package com.hanfu.controller;

import com.hanfu.common.Result;
import com.hanfu.dto.LoginDto;
import com.hanfu.dto.RegisterDto;
import com.hanfu.service.AuthService;
import com.hanfu.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDto dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDto dto) {
        Map<String, Object> result = authService.login(dto);
        return Result.success("登录成功", result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVo> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVo user = authService.getCurrentUser(userId);
        return Result.success(user);
    }
}
