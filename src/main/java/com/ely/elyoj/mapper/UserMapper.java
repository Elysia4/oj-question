package com.ely.elyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ely.elyoj.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
} 