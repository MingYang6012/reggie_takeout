package com.atming.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.User;
import com.atming.reggie.service.UserService;
import com.atming.reggie.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author karlo
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-11-15 16:28:36
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




