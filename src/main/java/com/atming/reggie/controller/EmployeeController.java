package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import com.atming.reggie.entity.Employee;
import com.atming.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static com.atming.reggie.common.R.success;

/**
 * @CreateTime: 2022-11-08-15:52
 * @Author: Hello77
 * @toUser:
 * @note:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     *  处理登入的请求，同时接受浏览器传入的用户数据,处理完成后,向浏览器中响应信息
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户传入的用户名,在数据库中进行查找,
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,username);
        Employee data = employeeService.getOne(queryWrapper);

        //3.若没有查到,返回错误信息
        if (data == null){
            return R.error("该用户不存在");
        }

        //4.将返回的用户信息的加密密码与我们处理的加密密码的用户信息进行对比,不匹配,返回密码错误
        if (!data.getPassword().equals(password)){
            return R.error("密码错误,请重新输入");
        }

        //5.判断返回的用户名的status，若为禁用状态,则返回禁用状态
        if (data.getStatus() != 1){
            return R.error("该用户已被禁止使用");
        }

        //6.若都没问题,则登入成功,将用户的id传入Session域中
        request.getSession().setAttribute("employee",data.getId()); //将获取到的用户Id放入session域对象中,可以方便后面使用

        return R.success(data);

    }


    /**
     * 完成退出的功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        log.info("拦截到请求：{}",request.getRequestURI());
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工信息
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("添加用户信息的用户姓名：{}",employee.getName());

        //简单的设置用户的各个信息（未从参数中传入的）
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


        /*Date date = new Date();
        //使用时间戳对象,将java类中java.util.date类型,转化为数据库中datetime的类型
        Timestamp timestamp = new Timestamp(date.getTime());

        employee.setCreateTime(timestamp);
        employee.setUpdateTime(timestamp);
        //获取当前用户的用户id
        Long empId = (Long) request.getSession().getAttribute("employee");

        //将创建当前用户的用户复制到数据库中
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        //用户数据设置完，可以将当前用户信息存入数据库中
        boolean save = employeeService.save(employee);

        if(save) {
            return R.success("新增员工成功");
        }

        return R.error("新增员工为成功");
    }

    /**
     *  分页查询的功能
     * @param page 浏览器传来的页面的页码
     * @param pageSize 页面的显示条数
     * @param name 查询指定用户名的条件
     * @return 返回结果类,将查询到的page信息封装到这个结果类中,返回给浏览器
     */
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "name",defaultValue = "") String name){

        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        Page<Employee> employeePage = new Page<>(page,pageSize);

        //根据用户要求的页码和一页显示的用户数进行查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        //判断用户是否要进行判断查询
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getCreateTime);

        //执行查询操作
        employeePage = employeeService.page(employeePage, queryWrapper);

        //返回查询到的数据
        return R.success(employeePage);
    }

    /**
     * 更新用户数据的通用方法
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("要修改的当前用户的id为 {}",employee.getId());

        /*Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        //要将用户的最新修改时间和修改人的id一起修改了
        employee.setUpdateTime(timestamp);
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/

        //执行修改方法
        boolean update = employeeService.updateById(employee);
        if (update) {
            return R.success("执行修改方法完成");
        }

        return R.error("用户修改未成功");
    }

    //根据用户id来查询并且将数据进行回响
    //(注) member.js中// 修改页面反查详情接口 调用这个方法
    @GetMapping("/{id}")
    public R<Employee> getUserById(@PathVariable Long id){
        log.info("数据回回响");

        Employee employee = employeeService.getById(id);

        if (employee != null){
            return R.success(employee);
        }

        return R.success(null);
    }























}
