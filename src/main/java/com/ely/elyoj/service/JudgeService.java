package com.ely.elyoj.service;

import com.ely.elyoj.model.entity.QuestionSubmit;

/**
 * 判题服务接口
 */
public interface JudgeService {
    
    /**
     * 判题
     * @param questionSubmitId 题目提交ID
     * @return 判题结果
     */
    QuestionSubmit doJudge(Long questionSubmitId);
} 