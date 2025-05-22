package com.ely.elyoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ely.elyoj.common.BaseResponse;
import com.ely.elyoj.common.ResultUtils;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.QuestionSubmitVO;
import com.ely.elyoj.service.QuestionSubmitService;
import com.ely.elyoj.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 题目提交控制器
 */
@RestController
@RequestMapping("/question_submit")
@RequiredArgsConstructor
public class QuestionSubmitController {

    private final QuestionSubmitService questionSubmitService;
    private final UserHolder userHolder;

    /**
     * 提交题目
     * @param questionSubmitAddRequest 题目提交请求
     * @param request HTTP请求
     * @return 提交id
     */
    @PostMapping("/submit")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        User loginUser = userHolder.getLoginUser(request);
        long id = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(id);
    }

    /**
     * 分页获取题目提交列表
     * @param questionSubmitQueryRequest 题目提交查询请求
     * @param request HTTP请求
     * @return 题目提交分页
     */
    @GetMapping("/list")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        User loginUser = userHolder.getLoginUser(request);
        Page<QuestionSubmitVO> page = questionSubmitService.getQuestionSubmitVOPage(questionSubmitQueryRequest, loginUser);
        return ResultUtils.success(page);
    }

    /**
     * 根据id获取题目提交
     * @param id 题目提交id
     * @param request HTTP请求
     * @return 题目提交
     */
    @GetMapping("/{id}")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitById(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            return ResultUtils.error(com.ely.elyoj.common.ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userHolder.getLoginUser(request);
        QuestionSubmitVO questionSubmitVO = questionSubmitService.getQuestionSubmitVO(questionSubmitService.getById(id), loginUser);
        return ResultUtils.success(questionSubmitVO);
    }
} 