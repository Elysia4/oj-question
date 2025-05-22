package com.ely.elyoj.model.dto.question;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 题目创建请求
 */
@Data
public class QuestionAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 题目内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 标签列表（JSON数组）
     */
    private String tags;

    /**
     * 题目答案
     */
    @NotBlank(message = "答案不能为空")
    private String answer;

    /**
     * 判题用例（JSON数组）
     */
    @NotBlank(message = "判题用例不能为空")
    private String judgeCase;

    /**
     * 判题配置（JSON对象）
     */
    @NotBlank(message = "判题配置不能为空")
    private String judgeConfig;

    /**
     * 难度（0-简单、1-中等、2-困难）
     */
    @NotNull(message = "难度不能为空")
    private Integer difficulty;
} 