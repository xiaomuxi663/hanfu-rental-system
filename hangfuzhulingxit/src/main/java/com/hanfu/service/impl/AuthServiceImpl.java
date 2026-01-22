package com.hanfu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanfu.common.BusinessException;
import com.hanfu.dto.LoginDto;
import com.hanfu.dto.RegisterDto;
import com.hanfu.entity.SysUser;
import com.hanfu.entity.SysUserRole;
import com.hanfu.mapper.SysUserMapper;
import com.hanfu.mapper.SysUserRoleMapper;
import com.hanfu.service.AuthService;
import com.hanfu.utils.JwtUtils;
import com.hanfu.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void register(RegisterDto dto) {
        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, dto.getPhone());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setCreditScore(100);
        user.setStatus(1);
        userMapper.insert(user);

        // 分配租客角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3); // 租客角色ID
        userRoleMapper.insert(userRole);
    }

    @Override
    public Map<String, Object> login(LoginDto dto) {
        // 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 查询角色
        String roleKey = userMapper.selectRoleKeyByUserId(user.getId());

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), roleKey);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        userVo.setRoleKey(roleKey);
        result.put("user", userVo);

        return result;
    }

    @Override
    public UserVo getCurrentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        userVo.setRoleKey(userMapper.selectRoleKeyByUserId(userId));

        return userVo;
    }
}
