package com.atming.reggie.mapper;

import com.atming.reggie.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}