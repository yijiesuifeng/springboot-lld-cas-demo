package com.wind.cas.controller;

import com.wind.cas.pojo.SysUser;
import com.wind.utils.MD5Util;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * @author Lilandong
 * @date 2017/11/24
 */
@RestController
public class AuthUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserController.class);

    /**
     * 1. cas 服务端会通过post请求，并且把用户信息以"用户名:密码"进行Base64编码放在authorization请求头中
     * 2. 返回200状态码并且格式为{"@class":"org.apereo.cas.authentication.principal.SimplePrincipal","id":"casuser","attributes":{}} 是成功的
     * 2. 返回状态码403用户不可用；404账号不存在；423账户被锁定；428过期；其他登录失败
     *
     * @param httpHeaders
     * @return
     */
    @PostMapping("/casLogin")
    public Object casLogin(@RequestHeader HttpHeaders httpHeaders) {
        LOGGER.info("Rest api login.");
        LOGGER.debug("request headers: {}", httpHeaders);
        SysUser user = null;
        try {
            UserTemp userTemp = obtainUserFormHeader(httpHeaders);
            //模拟：尝试查找用户库是否存在
            user = this.getUser(userTemp.username);

            LOGGER.info("------------------------------------------------"+user.toString());
            LOGGER.info("================================================"+userTemp.toString());
            if (user != null) {
                if (!user.getPassword().equals(userTemp.password)) {
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);//密码不匹配
                }
                if (user.isDisable()) {
                    return new ResponseEntity(HttpStatus.FORBIDDEN);//禁用 403
                }
                if (user.isLocked()) {
                    return new ResponseEntity(HttpStatus.LOCKED);//锁定 423
                }
                if (user.isExpired()) {
                    return new ResponseEntity(HttpStatus.PRECONDITION_REQUIRED);//过期 428
                }
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND); //不存在 404
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
            new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("[{}] login is ok", user.getUsername());
        return user;
    }

    /**
     * 根据请求头获取用户名及密码
     *
     * @param httpHeaders
     * @return
     * @throws UnsupportedEncodingException
     */
    private UserTemp obtainUserFormHeader(HttpHeaders httpHeaders) throws UnsupportedEncodingException {
        /**
         * This allows the CAS server to reach to a remote REST endpoint via a POST for verification of credentials.
         * Credentials are passed via an Authorization header whose value is Basic XYZ where XYZ is a Base64 encoded version of the credentials.
         */
        //根据官方文档，当请求过来时，会通过把用户信息放在请求头authorization中，并且通过Basic认证方式加密
        String authorization = httpHeaders.getFirst("authorization");//将得到 Basic Base64(用户名:密码)
        String baseCredentials = authorization.split(" ")[1];
        String usernamePassword = new String(Base64Utils.decodeFromString(baseCredentials), "UTF-8");//用户名:密码
        LOGGER.debug("login user: {}", usernamePassword);
        String credentials[] = usernamePassword.split(":");
        return new UserTemp(credentials[0], credentials[1]);
    }

    /**
     * 解析请求过来的用户
     */
    @ToString
    private class UserTemp {
        private String username;
        private String password;

        public UserTemp(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    /**
     * 模拟数据库中获取用户信息
     *
     * @param userName
     * @return
     */
    private SysUser getUser(String userName) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(userName);
        sysUser.setPassword("202cb962ac59075b964b07152d234b70");//明文是123
        if (userName.equals("lld")) {
            return sysUser;
        } else if (userName.equals("lld1")) {
            sysUser.setDisable(true);
            return sysUser;
        } else if (userName.equals("lld2")) {
            sysUser.setExpired(true);
            return sysUser;
        } else if (userName.equals("lld3")) {
            sysUser.setLocked(true);
            return sysUser;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String s = MD5Util.MD5("123");
        System.out.println(s);
    }
}
