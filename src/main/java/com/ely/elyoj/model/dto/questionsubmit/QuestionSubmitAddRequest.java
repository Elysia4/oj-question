package com.ely.elyoj.model.dto.questionsubmit;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 题目提交请求
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目id
     */
    @NotNull(message = "题目id不能为空")
    private Long questionId;

    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;

    /**
     * 用户代码
     */
    @NotBlank(message = "代码不能为空")
    private String code;
} 