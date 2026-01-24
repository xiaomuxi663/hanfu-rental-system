package com.hanfu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息VO
 */
@Data
public class UserVo {

    private Long id;

    private String username;

    private String nickname;

    private String realName;

    private String phone;

    private String avatar;

    private Integer creditScore;

    private BigDecimal balance;

    private Integer status;

    private String roleKey;

    private String roleName;

    private String defaultReceiverName;

    private String defaultReceiverPhone;

    private String defaultAddress;

    private LocalDateTime createTime;
}
