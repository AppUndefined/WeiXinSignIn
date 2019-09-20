package com.discern.discern.config;

import com.alibaba.fastjson.JSONObject;
import com.discern.discern.constant.UserConstants;
import com.discern.discern.entity.Function;
import com.discern.discern.entity.Role;
import com.discern.discern.entity.User;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


/**
 *
 */
public class MyAuthorizingRealm extends AuthorizingRealm {

    private final static Logger logger=LoggerFactory.getLogger(MyAuthorizingRealm.class);
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    //shiro的权限配置方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        logger.info("权限配置-->doGetAuthorizationInfo");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        logger.info("----------------------------->"+principals.getPrimaryPrincipal());
        User user =(User) principals.getPrimaryPrincipal();
                  for(Role role: user.getRoleList()){
                      for (Function function : role.getFunctionList()) {
                          authorizationInfo.addStringPermission(function.getPermission());
                      }
                      authorizationInfo.addRole(role.getName());
                 }
        Set<String> roles = authorizationInfo.getRoles();
                  StringBuilder sb = new StringBuilder();
        for (String role : roles) {
            sb.append(role+"、");
        }
        logger.info("用户"+ user.getUsername()+"具有的角色："+sb);
                  logger.info("用户"+ user.getUsername()+"具有的权限："+authorizationInfo.getStringPermissions());
        return authorizationInfo;
    }

    //shiro的身份验证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        logger.info("正在验证身份...");
        SimpleAuthenticationInfo info=null;

        //将token转换成UsernamePasswordToken
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        //从转换后的token中获取用户名
        String username= upToken.getUsername();
        logger.info("----->"+username);

        //查询数据库，得到用户
        User user =userService.findByUsername(username);
        if(user ==null){
            return null;
        }
        //得到加密密码的盐值
        ByteSource salt = ByteSource.Util.bytes(user.getSalt());
        logger.info("加密密码的盐："+salt);
        Object md = new SimpleHash("MD5",upToken.getPassword(),salt,1024);
        logger.info("盐值加密后的密码："+md);
        info = new SimpleAuthenticationInfo(
                user, //用户名
                user.getPassword(), //密码
                salt, //加密的盐值
                getName()  //realm name
        );
        //登录成功后将用户信息放入Redis（key：token，value：user）
        redisService.setData(SecurityUtils.getSubject().getSession().getId().toString(),JSONObject.toJSONString(user),UserConstants.SESSION_EXPIRE_TIME/1000);
        return info;
    }

}