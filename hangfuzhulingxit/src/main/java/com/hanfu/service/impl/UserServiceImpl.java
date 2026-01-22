package com.hanfu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanfu.common.BusinessException;
import com.hanfu.entity.SysUser;
import com.hanfu.mapper.SysUserMapper;
import com.hanfu.service.UserService;
import com.hanfu.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;

    @Override
    public IPage<UserVo> getUserList(Integer pageNum, Integer pageSize, String username, Integer status) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysUser::getStatus, status);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        IPage<SysUser> userPage = userMapper.selectPage(page, wrapper);
        
        // 转换为VO
        return userPage.convert(user -> {
            UserVo vo = new UserVo();
            BeanUtil.copyProperties(user, vo);
            vo.setRoleKey(userMapper.selectRoleKeyByUserId(user.getId()));
            return vo;
        });
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(Long userId, SysUser updateUser) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 只允许更新部分字段
        if (StrUtil.isNotBlank(updateUser.getNickname())) {
            user.setNickname(updateUser.getNickname());
        }
        if (StrUtil.isNotBlank(updateUser.getAvatar())) {
            user.setAvatar(updateUser.getAvatar());
        }
        if (StrUtil.isNotBlank(updateUser.getPhone())) {
            user.setPhone(updateUser.getPhone());
        }
        if (StrUtil.isNotBlank(updateUser.getDefaultReceiverName())) {
            user.setDefaultReceiverName(updateUser.getDefaultReceiverName());
        }
        if (StrUtil.isNotBlank(updateUser.getDefaultReceiverPhone())) {
            user.setDefaultReceiverPhone(updateUser.getDefaultReceiverPhone());
        }
        if (StrUtil.isNotBlank(updateUser.getDefaultAddress())) {
            user.setDefaultAddress(updateUser.getDefaultAddress());
        }

        userMapper.updateById(user);
    }
}
