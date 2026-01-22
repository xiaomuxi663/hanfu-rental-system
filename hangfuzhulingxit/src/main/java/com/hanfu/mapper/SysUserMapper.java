package com.hanfu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanfu.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询角色标识
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} LIMIT 1")
    String selectRoleKeyByUserId(@Param("userId") Long userId);
}
