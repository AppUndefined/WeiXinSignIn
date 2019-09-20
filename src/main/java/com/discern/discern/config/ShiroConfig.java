package com.discern.discern.config;

import com.discern.discern.dao.RedisSessionDao;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration // 等价于beans
public class ShiroConfig {

    private static final Logger log = LoggerFactory.getLogger(ShiroFilterFactoryBean.class);
    @Autowired
    private RedisSessionDao sessionDao;

    @Bean(name = "securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") MyAuthorizingRealm authRealm,
                                           @Qualifier("cookieRememberMeManager") CookieRememberMeManager cookieRememberMeManager, DefaultWebSessionManager webSessionManager) {
        log.info("securityManager()");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.
        securityManager.setRealm(authRealm);
        securityManager.setSessionManager(webSessionManager);
        // 设置rememberMe管理器
        securityManager.setRememberMeManager(cookieRememberMeManager);

        return securityManager;
    }

    /**
     * realm
     *
     * @return
     */
    @Bean(name = "authRealm")
    public MyAuthorizingRealm myAuthRealm(
            @Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher,
            @Qualifier("ehCacheManager") EhCacheManager ehCacheManager) {
        log.info("myShiroRealm()");
        MyAuthorizingRealm myAuthorizingRealm = new MyAuthorizingRealm();
        // 设置密码凭证匹配器
        myAuthorizingRealm.setCredentialsMatcher(matcher); // myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        // 设置缓存管理器
        myAuthorizingRealm.setCacheManager(ehCacheManager);

        return myAuthorizingRealm;
    }

    @Bean
    public DefaultWebSessionManager configWebSessionManager() {
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        manager.setSessionDAO(sessionDao);// 设置SessionDao
        manager.setDeleteInvalidSessions(true);// 删除过期的session
        manager.setGlobalSessionTimeout(sessionDao.getExpireTime());// 设置全局session超时时间
        manager.setSessionValidationSchedulerEnabled(true);// 是否定时检查session

        return manager;
    }

    /**
     * 缓存管理器
     *
     * @return
     */
    @Bean(value = "ehCacheManager")
    public EhCacheManager ehCacheManager(@Qualifier(value = "ehCacheManagerFactoryBean2") EhCacheManagerFactoryBean bean) {
        log.info("ehCacheManager()");
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return cacheManager;
    }
    @Bean(value = "ehCacheManagerFactoryBean2")
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
        return new EhCacheManagerFactoryBean();
    }

    /**
     * cookie对象;
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        log.info("rememberMeCookie()");
        // 这个参数是cookie的名称，对应前端的checkbox 的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // <!-- 记住我cookie生效时间30天（259200） ,单位秒;-->
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }

    /**
     * 记住我管理器 cookie管理对象;
     *
     * @return
     */
    @Bean(name = "cookieRememberMeManager")
    public CookieRememberMeManager rememberMeManager() {
        System.out.println("rememberMeManager()");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }

    /**
     * 密码匹配凭证管理器
     *
     * @return
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        log.info("hashedCredentialsMatcher()");
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("MD5");// 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(1024);// 散列的次数，比如散列两次，相当于
        // md5(md5(""));

        return hashedCredentialsMatcher;
    }

    /**
     * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持; Controller才能使用//@RequiresPermissions
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            @Qualifier("securityManager") SecurityManager securityManager) {
        log.info("authorizationAttributeSourceAdvisor()");
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager) {

        log.info("shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 拦截器.
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("/logout", "logout");
        map.put("/css/**", "anon");
        map.put("/js/**", "anon");
        map.put("/font/**", "anon");
        map.put("/ico/**", "anon");
        map.put("/img/**", "anon");
        map.put("/img/gallery/**", "anon");
        map.put("/login/**", "anon");
        map.put("/swagger-ui*", "anon");
        map.put("/rest/**", "anon");
        map.put("/www/**", "anon");
        map.put("/login.html*", "anon");
        map.put("/**", "authc");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login.html");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/widgets.html");
        // 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

}
