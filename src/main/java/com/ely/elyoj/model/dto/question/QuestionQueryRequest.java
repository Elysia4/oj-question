package com.ely.elyoj.model.dto.question;

import com.ely.elyoj.model.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 题目查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目id
     */
    private Long id;

    /**
     * 题目标题（模糊查询）
     */
    private String title;

    /**
     * 题目内容（模糊查询）
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
     * 创建用户id
     */
    private Long userId;

    /**
     * 难度（0-简单、1-中等、2-困难）
     */
    private Integer difficulty;
} 