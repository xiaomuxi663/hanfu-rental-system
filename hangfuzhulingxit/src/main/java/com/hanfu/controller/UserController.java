package com.hanfu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.common.Result;
import com.hanfu.entity.SysUser;
import com.hanfu.service.UserService;
import com.hanfu.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表(管理员)
     */
    @GetMapping("/list")
    public Result<IPage<UserVo>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        IPage<UserVo> page = userService.getUserList(pageNum, pageSize, username, status);
        return Result.success(page);
    }

    /**
     * 修改用户状态(管理员)
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody SysUser user) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, user);
        return Result.success();
    }
}
