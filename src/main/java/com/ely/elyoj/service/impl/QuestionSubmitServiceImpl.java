package com.ely.elyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.mapper.QuestionSubmitMapper;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.ely.elyoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.ely.elyoj.model.entity.Question;
import com.ely.elyoj.model.entity.QuestionSubmit;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.QuestionSubmitVO;
import com.ely.elyoj.model.vo.QuestionVO;
import com.ely.elyoj.model.vo.UserVO;
import com.ely.elyoj.service.QuestionService;
import com.ely.elyoj.service.QuestionSubmitService;
import com.ely.elyoj.service.UserService;
import com.ely.elyoj.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 题目提交服务实现类
 */
@Service
@RequiredArgsConstructor
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService {

    private final QuestionService questionService;
    private final UserService userService;
    private final JudgeService judgeService;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不能为空");
        }
        // 校验题目是否存在
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 校验用户是否存在
        Long userId = loginUser.getId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        // 创建题目提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitAddRequest, questionSubmit);
        questionSubmit.setUserId(userId);
        // 设置初始状态
        questionSubmit.setStatus(0);
        // 设置初始判题信息
        questionSubmit.setJudgeInfo("{}");
        
        // 保存
        boolean success = this.save(questionSubmit);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据插入失败");
        }
        
        // 更新题目提交数
        Question updateQuestion = new Question();
        updateQuestion.setId(questionId);
        updateQuestion.setSubmitNum(question.getSubmitNum() + 1);
        boolean updated = questionService.updateById(updateQuestion);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目提交数更新失败");
        }
        
        // 执行判题服务（异步）
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmit.getId());
        });
        
        return questionSubmit.getId();
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        if (questionSubmit == null) {
            return null;
        }
        
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        
        // 脱敏：仅本人和管理员能看到自己的代码
        Long userId = loginUser.getId();
        if (!userId.equals(questionSubmit.getUserId()) && !loginUser.getRole().equals("admin")) {
            questionSubmitVO.setCode(null);
        }
        
        // 填充提交用户信息
        Long submitUserId = questionSubmit.getUserId();
        User submitUser = userService.getById(submitUserId);
        if (submitUser != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(submitUser, userVO);
            questionSubmitVO.setUserVO(userVO);
        }
        
        // 填充题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question != null) {
            QuestionVO questionVO = new QuestionVO();
            BeanUtils.copyProperties(question, questionVO);
            questionSubmitVO.setQuestionVO(questionVO);
        }
        
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(QuestionSubmitQueryRequest questionSubmitQueryRequest, User loginUser) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 构建查询条件
        QuestionSubmit questionSubmitQuery = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitQueryRequest, questionSubmitQuery);
        
        // 分页参数
        long current = questionSubmitQueryRequest.getCurrent();
        long pageSize = questionSubmitQueryRequest.getPageSize();
        
        // 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 构建查询条件
        LambdaQueryWrapper<QuestionSubmit> queryWrapper = new LambdaQueryWrapper<>();
        
        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionSubmitQuery.getLanguage()), QuestionSubmit::getLanguage, questionSubmitQuery.getLanguage());
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionSubmitQuery.getStatus()), QuestionSubmit::getStatus, questionSubmitQuery.getStatus());
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionSubmitQuery.getQuestionId()), QuestionSubmit::getQuestionId, questionSubmitQuery.getQuestionId());
        
        // 只有管理员和本人才能查看本人的提交记录
        if (!loginUser.getRole().equals("admin")) {
            Long userId = loginUser.getId();
            // 查询条件没有用户id时，只查看自己的
            if (questionSubmitQuery.getUserId() == null) {
                queryWrapper.eq(QuestionSubmit::getUserId, userId);
            } else {
                // 查询条件有用户id，但不是自己的，只能查看题目id
                if (!questionSubmitQuery.getUserId().equals(userId)) {
                    queryWrapper.eq(QuestionSubmit::getUserId, questionSubmitQuery.getUserId());
                    queryWrapper.eq(QuestionSubmit::getQuestionId, questionSubmitQuery.getQuestionId());
                }
            }
        } else {
            // 管理员可以查看任何人的提交
            if (ObjectUtils.isNotEmpty(questionSubmitQuery.getUserId())) {
                queryWrapper.eq(QuestionSubmit::getUserId, questionSubmitQuery.getUserId());
            }
        }
        
        // 按创建时间降序
        queryWrapper.orderByDesc(QuestionSubmit::getCreateTime);
        
        // 分页查询
        Page<QuestionSubmit> questionSubmitPage = this.page(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为VO
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(current, pageSize, questionSubmitPage.getTotal());
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitPage.getRecords().stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        
        return questionSubmitVOPage;
    }
} 