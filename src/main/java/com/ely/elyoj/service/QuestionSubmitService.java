package com.ely.elyoj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ely.elyoj.model.entity.QuestionSubmit;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.QuestionSubmitVO;

/**
 * 题目提交服务接口
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    
    /**
     * 提交题目
     * @param questionSubmitAddRequest 题目提交请求
     * @param loginUser 登录用户
     * @return 提交id
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取题目提交封装
     * @param questionSubmit 题目提交信息
     * @param loginUser 登录用户
     * @return 题目提交封装
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交封装
     * @param questionSubmitQueryRequest 题目提交查询条件
     * @param loginUser 登录用户
     * @return 题目提交分页
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser);
} 