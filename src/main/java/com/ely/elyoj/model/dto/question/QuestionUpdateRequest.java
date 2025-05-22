package com.ely.elyoj.model.dto.question;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 题目更新请求
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目id
     */
    @NotNull(message = "题目id不能为空")
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 标签列表（JSON数组）
     */
    private String tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例（JSON数组）
     */
    private String judgeCase;

    /**
     * 判题配置（JSON对象）
     */
    private String judgeConfig;

    /**
     * 难度（0-简单、1-中等、2-困难）
     */
    private Integer difficulty;
} 