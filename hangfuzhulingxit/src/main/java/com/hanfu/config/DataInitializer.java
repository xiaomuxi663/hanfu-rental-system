package com.hanfu.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanfu.entity.SysUser;
import com.hanfu.entity.SysUserRole;
import com.hanfu.mapper.SysUserMapper;
import com.hanfu.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 - 确保管理员账号存在且密码正确
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public void run(String... args) {
        initAdminUser();
    }

    private void initAdminUser() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, "admin");
        SysUser admin = userMapper.selectOne(wrapper);

        if (admin == null) {
            // 创建管理员
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123"));
            admin.setNickname("超级管理员");
            admin.setCreditScore(100);
            admin.setStatus(1);
            userMapper.insert(admin);

            // 分配管理员角色
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(admin.getId());
            userRole.setRoleId(1);
            userRoleMapper.insert(userRole);

            log.info("已创建管理员账号: admin/admin123");
        } else {
            // 更新密码为正确的BCrypt哈希
            admin.setPassword(BCrypt.hashpw("admin123"));
            userMapper.updateById(admin);
            log.info("已更新管理员密码");
        }
    }
}
