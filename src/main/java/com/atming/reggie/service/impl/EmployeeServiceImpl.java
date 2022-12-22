package com.atming.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.Employee;
import com.atming.reggie.service.EmployeeService;
import com.atming.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author karlo
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2022-11-08 15:47:07
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




