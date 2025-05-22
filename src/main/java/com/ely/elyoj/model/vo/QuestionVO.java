package com.ely.elyoj.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目视图（脱敏）
 */
@Data
public class QuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置（json 对象）
     */
    private String judgeConfig;

    /**
     * 难度（0 - 简单、1 - 中等、2 - 困难）
     */
    private Integer difficulty;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private String answer;
} 