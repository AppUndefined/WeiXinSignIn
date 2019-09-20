package com.discern.discern.controller;

import com.alibaba.fastjson.JSONObject;
import com.discern.discern.dao.UserJpaDao;
import com.discern.discern.entity.Role;
import com.discern.discern.entity.User;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.RoleService;
import com.discern.discern.service.UserService;
import com.discern.discern.utils.CookieUtils;
import com.discern.discern.utils.FileUtil;
import com.discern.discern.utils.RandomString;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "/rest")
public class restController {
    @Autowired
    private UserJpaDao userJpaDao;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RoleService roleService;
    private String  sign = "已签";
    @ApiOperation(value = "新增用户方法", httpMethod = "POST", response = String.class, notes = "传入roleId,和user对象")
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public Map addUser(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //验证用户名是否存在
        User user1 = userService.findByUsername(user.getUsername());
        if(user1!=null){
            map.put("msg","用户名重复");
            map.put("code",500);
            return map;
        }
        Set<Role> roles = new HashSet<>();
        try {
            String saltString = RandomString.generateUpperLowerString(6);
            ByteSource salt = ByteSource.Util.bytes(saltString);
            String password = "123456";
            if(user.getPassword()!=null){
                 password = new SimpleHash("MD5", user.getPassword(), salt, 1024)+"";
            }
            user.setSalt(saltString);
            user.setPassword(password);
            user.setCreateTime(new Date());
            user.setUsername(UUID.randomUUID().toString());
            user.setIsNotSign(sign);
            userJpaDao.save(user);
            map.put("code",200);
            map.put("msg","添加成功");
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","添加失败");
            e.printStackTrace();
        }
        return map;
    }

    @ApiOperation(value = "签到", httpMethod = "POST", response = String.class, notes = "传入用户名")
    @RequestMapping(value = "/sign",method = RequestMethod.POST)
    @ResponseBody
    public  HashMap  findByUserName( String phone) {
        HashMap<String, Object> map = new HashMap<>();
        User byPhone = userJpaDao.findByPhone(phone);
        if(byPhone!=null){
            byPhone.setIsNotSign(sign);
            userJpaDao.save(byPhone);
            map.put("code",200);
            map.put("msg","签到成功");
        }else{
            map.put("code",300);
            map.put("msg","填写个人信息");
        }
        return map;
    }
    @ApiOperation(value = "查询所有需要抽奖的人", httpMethod = "POST", response = String.class, notes = "传入用户名")
    @RequestMapping(value = "/findAll",method = RequestMethod.POST)
    @ResponseBody
    public  List<String>  findAll() {
        ArrayList<String> users = new ArrayList<>();
        User user = new User();
        user.setIsNotSign(sign);
        List<User> allByUser = userService.findAllByUser(user);
        for (User user1 : allByUser) {
            String phone = user1.getPhone();
            try {
                phone = phone.substring(0,3)+"****"+phone.substring(7,user1.getPhone().length());
            }catch (Exception e){
                phone = user1.getPhone();
            }

            users.add(user1.getNickname()+":"+phone);
        }
        return users;
    }

}