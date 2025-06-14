package com.ely.elyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ely.elyoj.model.entity.Question;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目Mapper接口
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
} 