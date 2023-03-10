package com.atming.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atming.reggie.entity.AddressBook;
import com.atming.reggie.service.AddressBookService;
import com.atming.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author karlo
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-11-16 16:06:11
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




