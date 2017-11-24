package com.wind.shiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.PathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 对shiro的安全配置，是对cas的登录策略进行配置
 */
@Configuration
public class ShiroConfig extends AbstractShiroWebFilterConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Value("#{ @environment['cas.prefixUrl'] ?: null }")
    private String prefixUrl;
    @Value("#{ @environment['cas.loginUrl'] ?: null }")
    private String casLoginUrl;
    @Value("#{ @environment['cas.callbackUrl'] ?: null }")
    private String callbackUrl;
    @Value("#{ @environment['cas.serviceUrl'] ?: null }")
    private String serviceUrl;

    // 必须配置此操作才可以使用thymeleaf-extras-shiro开发包
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    protected Config casConfig() {
        CasConfiguration casConfiguration = new CasConfiguration();
        casConfiguration.setProtocol(CasProtocol.CAS30);
        casConfiguration.setPrefixUrl(prefixUrl);
        casConfiguration.setLoginUrl(casLoginUrl);

        //用于认证的客户端名称（用逗号分隔）
        //在任何情况下，这个过滤器都要求用户进行认证。因此，如果clients空白或未定义，用户必须先前已被认证
        //如果client_name提供请求参数，则仅选择该客户端（如果存在于该客户端中clients）。
        CasClient casClient = new CasClient();
        casClient.setConfiguration(casConfiguration);
        casClient.setCallbackUrl(callbackUrl);
        casClient.setName("cas");

        Clients clients = new Clients();
        clients.setClients(casClient);

        //不走cas的过滤器的path
        PathMatcher pathMatcher = new PathMatcher();
        HashSet<String> excludedPath = Sets.newHashSet();
        excludedPath.add("/casLogin");
        pathMatcher.setExcludedPaths(excludedPath);

        // 安全配置
        Config config = new Config();
        config.addMatcher("excludedPath", pathMatcher);
        config.setClients(clients);
        return config;
    }

    /**
     * cas核心过滤器，把支持的client写上，filter过滤时才会处理，clients必须在casConfig.clients已经注册
     */
    @Bean
    public Filter casSecurityFilter() {
        SecurityFilter filter = new SecurityFilter();
        filter.setClients("cas");
        filter.setConfig(this.casConfig());
        filter.setMatchers("excludedPath");//matchers:请求必须满足以检查认证/授权的匹配器名称（以逗号分隔）的列表
        return filter;
    }

    /**
     * 对过滤器进行调整
     */
    @Bean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        //把subject对象设为subjectFactory
        //由于cas代理了用户，所以必须通过cas进行创建对象
        ((DefaultSecurityManager) securityManager).setSubjectFactory(new Pac4jSubjectFactory());
        ShiroFilterFactoryBean filterFactoryBean = super.shiroFilterFactoryBean();
        filterFactoryBean.setFilters(shiroFilters());
        filterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> chains = Maps.newHashMap();
        chains.put("/callback", "callbackFilter");
        chains.put("/logout", "logoutFilter");
        chains.put("/**", "casSecurityFilter");
        filterFactoryBean.setFilterChainDefinitionMap(chains);
        return filterFactoryBean;
    }


    /**
     * 对shiro的过滤策略进行明确
     */
    @Bean
    protected Map<String, Filter> shiroFilters() {
        CallbackFilter callbackFilter = new CallbackFilter();
        callbackFilter.setConfig(this.casConfig());

        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setConfig(this.casConfig());
        logoutFilter.setCentralLogout(true);
        logoutFilter.setDefaultUrl(serviceUrl);

        //过滤器设置
        Map<String, Filter> filters = new HashMap<>();
        filters.put("casSecurityFilter", casSecurityFilter());
        filters.put("callbackFilter", callbackFilter);
        filters.put("logoutFilter", logoutFilter);
        return filters;
    }
}
