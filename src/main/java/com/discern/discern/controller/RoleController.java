package com.discern.discern.controller;

import com.alibaba.fastjson.JSONObject;
import com.discern.discern.constant.UserConstants;
import com.discern.discern.entity.Role;
import com.discern.discern.entity.User;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.RoleService;
import com.discern.discern.service.UserService;
import com.discern.discern.utils.CookieUtils;
import com.discern.discern.utils.VerifyCodeUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author:XiaoYao
 * @date:2018/7/5 13:29
 */
@Controller
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RoleService roleService;

    //查询角色列表
    //@RequiresPermissions("read:role")
    @RequiresRoles("管理员")
    @RequestMapping(value = "/findByUserDepartment")
    @ResponseBody
    public Map findByUserDepartment(HttpServletRequest request ) {
        Map<String, Object> map = new HashMap<>();
        String token = CookieUtils.getCookieValue(request,"JSESSIONID");
        //根据当前用户所属的部门查询该部门下的角色111
        String jsonUser = redisService.getData(token);
        User user = JSONObject.parseObject(jsonUser, User.class);
        if(user==null||"".equals(jsonUser)||jsonUser==null){
            map.put("code",500);
            map.put("message","检查请求参数");
           return map;
        }
        List<Role> roleList = roleService.findByDepartment(user.getDepartment());
        map.put("data",roleList);
        map.put("code",200);
        return map;
    }
    //角色设置权限(编辑）
    //@RequiresPermissions("bind:function")
    @RequestMapping(value = "/update")
    @ResponseBody
    public Map bindFunction( Role role, @RequestParam("functionIds") List<Integer> functionIds) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (null != role) {
                if (null != functionIds && functionIds.size() > 0) {
                    roleService.save(role, functionIds);
                    map.put("code", 200);
                    map.put("msg", "更新成功");
                } else {
                    map.put("code", 404);
                    map.put("msg", "未找到需要绑定的数据");
                }
            }
        } catch (Exception e) {
            map.put("code", 500);
            map.put("msg", "更新失败");
            e.printStackTrace();
        }
        return map;
    }

}
