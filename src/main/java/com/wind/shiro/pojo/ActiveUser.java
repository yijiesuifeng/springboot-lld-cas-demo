package com.wind.shiro.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author lilandong
 * @desc 用户身份信息，存入session 由于tomcat将session会序列化在本地硬盘上，所以使用Serializable接口
 */
@Data
public class ActiveUser implements java.io.Serializable {
    private static final long serialVersionUID = -54524251564564L;
    private String name;
    private List<String> roles;// 角色
    private List<String> permissions;// 权限
}
