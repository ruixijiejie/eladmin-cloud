package com.google.system.dto;

import com.google.system.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author iris
 */
@Data
@ApiModel(value = "用户")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -7657663783681647907L;

    /**
     * 系统用户信息
     */
    @ApiModelProperty("系统用户信息")
    private User sysUser;

    /**
     * 系统权限标识组
     */
    @ApiModelProperty("系统权限标识组")
    private List<String> permissions;

    /**
     * 系统角色标识组
     */
    @ApiModelProperty(value = "系统角色标识组")
    private List<String> roleIds;

    /**
     * 登录类型　1：用户名密码登录　2：手机号登录　3：社交登录
     */
    @ApiModelProperty(value = "登录类型")
    private int type;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
}
