package com.ely.elyoj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ely.elyoj.model.dto.question.QuestionAddRequest;
import com.ely.elyoj.model.dto.question.QuestionQueryRequest;
import com.ely.elyoj.model.dto.question.QuestionUpdateRequest;
import com.ely.elyoj.model.entity.Question;
import com.ely.elyoj.model.vo.QuestionVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 题目服务接口
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验
     * @param question 题目
     * @param add 是否为创建校验
     */
    void validQuestion(Question question, boolean add);

    /**
     * 创建题目
     * @param questionAddRequest 题目创建请求
     * @param request HTTP请求
     * @return 题目id
     */
    long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    /**
     * 删除题目
     * @param id 题目id
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean deleteQuestion(long id, HttpServletRequest request);

    /**
     * 更新题目
     * @param questionUpdateRequest 题目更新请求
     * @param request HTTP请求
     * @return 是否成功
     */
    boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request);

    /**
     * 根据id获取题目封装
     * @param id 题目id
     * @param request HTTP请求
     * @return 题目封装
     */
    QuestionVO getQuestionVO(long id, HttpServletRequest request);

    /**
     * 分页获取题目封装
     * @param questionQueryRequest 题目查询条件
     * @param request HTTP请求
     * @return 题目分页
     */
    Page<QuestionVO> getQuestionVOPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);
} 