package com.discern.discern.controller;
import com.alibaba.fastjson.JSONObject;
import com.discern.discern.constant.UserConstants;
import com.discern.discern.entity.Role;
import com.discern.discern.entity.User;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.RoleService;
import com.discern.discern.service.UserService;
import com.discern.discern.utils.ASMBeanUtils;
import com.discern.discern.utils.CookieUtils;
import com.discern.discern.utils.FileUtil;
import com.discern.discern.utils.RandomString;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RoleService roleService;
    //excel导入用户
    @ApiOperation(value = "导入用户", httpMethod = "POST", response = String.class, notes = "传入roleId,和user对象")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public Map addUser(MultipartFile file) {
        HashMap<String, Object> map = null;
        try {
            map = new HashMap<>();
            Workbook wb = null;
            if (FileUtil.getSuffix(file.getOriginalFilename()).equals("xlsx")) {
                wb = new XSSFWorkbook(file.getInputStream());
            } else if(FileUtil.getSuffix(file.getOriginalFilename()).equals("xls")){
                wb = new HSSFWorkbook(file.getInputStream());
            }else{
                map.put("code",500);
                map.put("msg","上传文件格式不正确");
                return map;
            }
            userService.importUser(wb);
            map.put("code",200);
            map.put("msg","添加成功");
        } catch (IOException e) {
            map.put("code",500);
            map.put("msg",e.toString());
            return map;
        }
        return map;
    }
    //excel导出用户
    @ApiOperation(value = "导出用户", httpMethod = "POST", response = String.class, notes = "")
    @RequestMapping(value = "/export")
    @ResponseBody
    public void export(HttpServletResponse response) {
        try {
            File file =   userService.export();
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));

            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=Bj.xls");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //新增用户
    @ApiOperation(value = "新增用户方法", httpMethod = "POST", response = String.class, notes = "传入roleId,和user对象")
    @RequestMapping(value = "/addUser/{roleId}", method = RequestMethod.POST)
    @ResponseBody
    public Map addUser(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //验证用户名是否存在
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
            Role role = new Role();
            //给用户绑定角色
            user.setRoleList(roles);
            //给用户设置与角色相同的部门
            user.setDepartment(role.getDepartment());
            //初始化日期
            user.setCreateTime(new Date());
            user.setUsername(UUID.randomUUID().toString());
            userService.addUser(user);
            map.put("code",200);
            map.put("msg","添加成功");
        } catch (Exception e) {
            map.put("code",500);
            map.put("msg","添加失败");
            e.printStackTrace();
        }
        return map;
    }

    //根据用户名查找用户
    @ApiOperation(value = "根据用户名查找用户", httpMethod = "POST", response = String.class, notes = "传入用户名")
    @RequestMapping(value = "/findByUserName")
    @ResponseBody
    public  HashMap  findByUserName(HttpServletRequest request, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10")Integer limit, User user2) {
        HashMap<String, Object> map = new HashMap<>();
        String userName = user2.getUsername();
        List<User> content = null;
        try {
            //根据当前用户所属的部门查询该部门下的用户（department为空的为超级管理员能查询所有人）
            String token = CookieUtils.getCookieValue(request,"JSESSIONID");
            String jsonUser = redisService.getData(token);

            User user = JSONObject.parseObject(jsonUser, User.class);
            if(user==null||"".equals(jsonUser)||jsonUser==null){
                map.put("code",500);
                map.put("message","检查请求参数");
                return map;
            }
            Page<User> pageUser = userService.findByUsername(offset/limit, limit, user2,user.getDepartment());
             content = pageUser.getContent();
            for (User user1 : content) {
                if(user1.getRoleList().size()>0){
                    Role role = user1.getRoleList().iterator().next();
                    user1.setSalt(role.getName()+"("+role.getDesc()+")");
                }
                user1.setRoleList(null);
            }
            map.put("rows",content);
            map.put("total",pageUser.getTotalElements());

        }catch (Exception e){
            e.printStackTrace();
            map.put("code", 500);
            return map;
        }
        return map;
    }
    //根据用户Id查找用户
    @RequestMapping(value = "/findById")
    @ResponseBody
    public  Map  findById( Integer userId) {
        HashMap<String, Object> map = new HashMap<>();
        User user = userService.findById(userId);
        Set<Role> roleList = user.getRoleList();
        map.put("user",user);
        if(user.getRoleList()!=null&&user.getRoleList().size()>0){
            map.put("role",user.getRoleList().iterator().next());
        }
        return map;
    }
    //删除用户
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Map delete(Integer userId) {

        HashMap<String, Object> map = new HashMap<>();
        try {
            if(userId==null||userId.equals(1)){
                map.put("code",500);
                map.put("message","请检查请求参数");
                return  map;
            }
            userService.delete(userId);
        }catch (Exception e){
            e.printStackTrace();
            map.put("code", 500);
            return map;
        }
        map.put("code",200);
        map.put("message","成功");
        return map;
    }
    //用户登出
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ApiOperation(value = "用户登出", httpMethod = "POST", response = String.class, notes = "不需要参数")
    @ResponseBody
    public String logout() {
        // 基于shiro完成退出
        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        return "成功退出";
    }
    //编辑用户用户
    @RequestMapping(value = "/update/{roleId}", method = RequestMethod.POST)
    @ResponseBody
    @RequiresRoles("管理员")
    public Map update(User user) {
        HashMap<String, Object> map = new HashMap<>();
        User user2=null;
        if(user.getUserId()==null){
            map.put("code",500);
            map.put("message","缺少该用户id");
            return map;
        }
        //验证用户名是否存在
        User user1 = userService.findByUsername(user.getUsername());
        user2 = userService.findById(user.getUserId());
        if(user1!=null){
            //如果该用户名也不等于原用户名
            user.setUsername(user2.getUsername());
        }
        try {
            ASMBeanUtils.copyProperties(user2,user);
            userService.update(user2);
            map.put("code",200);
            map.put("message","成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("code", 500);
            return map;
        }
        return map;

    }



}