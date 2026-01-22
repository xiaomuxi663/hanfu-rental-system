package com.hanfu.service;

import com.hanfu.dto.LoginDto;
import com.hanfu.dto.RegisterDto;
import com.hanfu.vo.UserVo;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    void register(RegisterDto dto);

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginDto dto);

    /**
     * 获取当前用户信息
     */
    UserVo getCurrentUser(Long userId);
}
