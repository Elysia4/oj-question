package com.ely.elyoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ely.elyoj.common.BaseResponse;
import com.ely.elyoj.common.ResultUtils;
import com.ely.elyoj.model.dto.question.QuestionAddRequest;
import com.ely.elyoj.model.dto.question.QuestionQueryRequest;
import com.ely.elyoj.model.dto.question.QuestionUpdateRequest;
import com.ely.elyoj.model.vo.QuestionVO;
import com.ely.elyoj.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 创建题目
     * @param questionAddRequest 题目创建请求
     * @param request HTTP请求
     * @return 题目id
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        long id = questionService.addQuestion(questionAddRequest, request);
        return ResultUtils.success(id);
    }

    /**
     * 删除题目
     * @param id 题目id
     * @param request HTTP请求
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Boolean> deleteQuestion(@PathVariable("id") Long id, HttpServletRequest request) {
        boolean success = questionService.deleteQuestion(id, request);
        return ResultUtils.success(success);
    }

    /**
     * 更新题目
     * @param questionUpdateRequest 题目更新请求
     * @param request HTTP请求
     * @return 是否成功
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        boolean success = questionService.updateQuestion(questionUpdateRequest, request);
        return ResultUtils.success(success);
    }

    /**
     * 根据id获取题目
     * @param id 题目id
     * @param request HTTP请求
     * @return 题目
     */
    @GetMapping("/{id}")
    public BaseResponse<QuestionVO> getQuestionById(@PathVariable("id") Long id, HttpServletRequest request) {
        QuestionVO questionVO = questionService.getQuestionVO(id, request);
        return ResultUtils.success(questionVO);
    }

    /**
     * 分页获取题目列表
     * @param questionQueryRequest 题目查询请求
     * @param request HTTP请求
     * @return 题目分页
     */
    @GetMapping("/list")
    public BaseResponse<Page<QuestionVO>> listQuestionByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        Page<QuestionVO> page = questionService.getQuestionVOPage(questionQueryRequest, request);
        return ResultUtils.success(page);
    }
}