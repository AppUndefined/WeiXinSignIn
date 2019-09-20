package com.discern.discern.controller;

import com.alibaba.fastjson.JSONObject;
import com.discern.discern.constant.UserConstants;
import com.discern.discern.entity.User;
import com.discern.discern.service.RedisService;
import com.discern.discern.service.UserService;
import com.discern.discern.utils.VerifyCodeUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author:XiaoYao
 * @date:2018/7/5 13:29
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;

    /**
     * 验证码
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public byte[] getVerifyCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        //存入会话session
        HttpSession session = request.getSession(true);
        //删除以前的
        // redis 存储 verifyCode
        String uuid = UUID.randomUUID().toString();
        redisService.setData(uuid, verifyCode, UserConstants.DEFAULT_VALID_CODE_EXPIRE_TIME);
        // 将验证码key，及验证码的图片返回
        Cookie cookie = new Cookie("validCode", uuid);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        //生成图片
        int w = 100, h = 30;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, bao, verifyCode);
        Byte[] bytes = new Byte[1012];
        return bao.toByteArray();
    }


    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @ApiOperation(value = "登录", httpMethod = "POST", response = String.class, notes = "传入username 和 password")
    @RequestMapping(value = "/ajaxLogin", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxLogin(@RequestParam("username") String username, @RequestParam("password") String password,HttpServletRequest request,HttpServletResponse response) {
        redisService.setData("currentProjectId","0",10000);
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        User user = userService.findByUsername(username);
        if (null != user) {
            username = user.getUsername();
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            jsonObject.put("token", subject.getSession().getId());
            jsonObject.put("msg", "登录成功");
            Cookie cookie = new Cookie("level",user.getRoleList().iterator().next().getName());
            cookie.setPath("/");
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
            jsonObject.put("code",200);
        } catch (IncorrectCredentialsException e) {
            jsonObject.put("msg", "密码错误");
            jsonObject.put("code",500);
        } catch (LockedAccountException e) {
            jsonObject.put("msg", "登录失败，该用户已被冻结");
            jsonObject.put("code",500);
        } catch (AuthenticationException e) {
            jsonObject.put("msg", "该用户不存在");
            jsonObject.put("code",500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


}
