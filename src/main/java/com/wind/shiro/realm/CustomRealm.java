package com.wind.shiro.realm;

import com.wind.shiro.pojo.ActiveUser;
import com.wind.shiro.service.LoginService;
import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lilandong
 * @date 2017/11/21
 */
@Configuration
public class CustomRealm extends Pac4jRealm {
    private final static Logger logger = LoggerFactory.getLogger(CustomRealm.class);
    @Autowired
    private LoginService loginService;

    @Bean
    public Realm pac4jRealm() {
        return new Pac4jRealm();
    }
    @Bean
    public Authorizer authorizer() {
        CustomRealm realm = new CustomRealm();
        return realm;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final Pac4jToken token = (Pac4jToken) authenticationToken;
        final LinkedHashMap<String, CommonProfile> profiles = token.getProfiles();
        final Pac4jPrincipal principal = new Pac4jPrincipal(profiles);
        String loginName = principal.getProfile().getId();
        ActiveUser activeUser = new ActiveUser();
        activeUser.setName(loginName);
        /* 将用户信息存放到session中
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("userSessionId", loginName );*/
        //根据身份信息获取权限信息，从数据库获取到权限数据--->避免每次授权都进行数据库读取
        Map<String, List<String>> permissions = loginService.permissions(loginName);
        activeUser.setPermissions(permissions.get("permissions"));
        activeUser.setRoles(permissions.get("roles"));
        return new SimpleAuthenticationInfo(activeUser, profiles.hashCode(), getName());
    }

    // 用于授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ActiveUser activeUser = (ActiveUser)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        //将上边查询到授权信息填充到simpleAuthorizationInfo对象中
        auth.addStringPermissions(activeUser.getPermissions());
        auth.addRoles(activeUser.getRoles()); // 保存所有的角色
        return auth;
    }
}
