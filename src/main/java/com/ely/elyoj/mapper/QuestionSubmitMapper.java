package com.ely.elyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ely.elyoj.model.entity.QuestionSubmit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目提交Mapper接口
 */
@Mapper
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {
} 