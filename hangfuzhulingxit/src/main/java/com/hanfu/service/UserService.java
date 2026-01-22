package com.hanfu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanfu.entity.SysUser;
import com.hanfu.vo.UserVo;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表
     */
    IPage<UserVo> getUserList(Integer pageNum, Integer pageSize, String username, Integer status);

    /**
     * 修改用户状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 更新用户信息
     */
    void updateProfile(Long userId, SysUser user);
}
