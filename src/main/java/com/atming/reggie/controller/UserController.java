package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.entity.User;
import com.atming.reggie.service.UserService;
import com.atming.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @CreateTime: 2022-11-15-16:29
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码的方法
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("要获取验证码的手机号是:{}",user.getPhone());

        //获取到要生成验证码的手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){

            //生成一个随机的4位的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //调用阿里云提供的短信服务api完成发送短信
            log.info("生成的验证码为:{}",code);

            //将生成的验证码保存到session域对象中
            session.setAttribute("phone",code);

            return R.success("短信验证码发送成功");
        }


        //返回数据
        return R.error("验证码发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session){
        log.info("用户提交的数据为{}",user);
        //获取到登入的手机号
        String phone = user.get("phone").toString();

        //后去用户传入的验证码
        String code = user.get("code").toString();

        //从session域中获取到随机生成的验证码
        String codeInSession = (String) session.getAttribute("phone");

        //将用户传入的验证码与生成的验证码做比较,比较成功,则登入成功
        if (code != null && codeInSession.equals(code)){
            //在对用户的手机号放入数据库中进行查询,若数据库中没有改手机号，则默认将用户注册到数据库中
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User one = userService.getOne(queryWrapper);

            //判断当前数据库中是否有当前用户, ==0为没有
            if (one == null){
                one = new User();

                one.setPhone(phone);
                one.setStatus(1);

                userService.save(one);
            }
            session.setAttribute("user",one.getId());
            return R.success(one);


        }

        return R.error("用户登入失败");

    }

}
