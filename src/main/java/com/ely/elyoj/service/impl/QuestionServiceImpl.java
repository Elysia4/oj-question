package com.ely.elyoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.mapper.QuestionMapper;
import com.ely.elyoj.model.dto.question.QuestionAddRequest;
import com.ely.elyoj.model.dto.question.QuestionQueryRequest;
import com.ely.elyoj.model.dto.question.QuestionUpdateRequest;
import com.ely.elyoj.model.entity.Question;
import com.ely.elyoj.model.entity.User;
import com.ely.elyoj.model.vo.QuestionVO;
import com.ely.elyoj.model.vo.UserVO;
import com.ely.elyoj.service.QuestionService;
import com.ely.elyoj.service.UserService;
import com.ely.elyoj.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final UserService userService;
    private final UserHolder userHolder;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();

        // 创建时，参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(title, content, answer, judgeCase, judgeConfig)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
        if (StringUtils.isNotBlank(tags) && tags.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签过长");
        }
    }

    @Override
    public long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        // 校验是否登录
        User loginUser = userHolder.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // 设置提交和通过数为0
        question.setSubmitNum(0);
        question.setAcceptedNum(0);
        // 设置创建用户id
        question.setUserId(loginUser.getId());
        
        // 校验
        validQuestion(question, true);
        
        // 保存
        boolean success = this.save(question);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        
        return question.getId();
    }

    @Override
    public boolean deleteQuestion(long id, HttpServletRequest request) {
        // 校验是否登录
        User loginUser = userHolder.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        // 校验题目是否存在
        Question question = this.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 只有管理员或者创建者可以删除
        if (!loginUser.getRole().equals("admin") && !question.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        
        // 删除
        return this.removeById(id);
    }

    @Override
    public boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        // 校验是否登录
        User loginUser = userHolder.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        
        if (questionUpdateRequest == null || questionUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 校验题目是否存在
        Question oldQuestion = this.getById(questionUpdateRequest.getId());
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        
        // 只有管理员或者创建者可以修改
        if (!loginUser.getRole().equals("admin") && !oldQuestion.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        
        // 更新题目
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        
        // 校验
        validQuestion(question, false);
        
        // 更新
        return this.updateById(question);
    }

    @Override
    public QuestionVO getQuestionVO(long id, HttpServletRequest request) {
        Question question = this.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return getQuestionVO(question);
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 构建查询条件
        Question questionQuery = new Question();
        BeanUtils.copyProperties(questionQueryRequest, questionQuery);
        
        // 分页参数
        long current = questionQueryRequest.getCurrent();
        long pageSize = questionQueryRequest.getPageSize();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        
        // 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 构建查询条件
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        
        // 标题模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionQuery.getTitle()), Question::getTitle, questionQuery.getTitle());
        // 内容模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionQuery.getContent()), Question::getContent, questionQuery.getContent());
        // 难度等于查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionQuery.getDifficulty()), Question::getDifficulty, questionQuery.getDifficulty());
        // 用户id查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionQuery.getUserId()), Question::getUserId, questionQuery.getUserId());
        
        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            // 升序
            if ("ascend".equals(sortOrder)) {
                queryWrapper.orderByAsc(Question::getCreateTime);
            } else {
                queryWrapper.orderByDesc(Question::getCreateTime);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(Question::getCreateTime);
        }
        
        // 分页查询
        Page<Question> questionPage = this.page(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为VO
        Page<QuestionVO> questionVOPage = new Page<>(current, pageSize, questionPage.getTotal());
        List<QuestionVO> questionVOList = questionPage.getRecords().stream()
                .map(this::getQuestionVO)
                .collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        
        return questionVOPage;
    }
    
    /**
     * 获取题目VO
     * @param question 题目实体
     * @return 题目VO
     */
    private QuestionVO getQuestionVO(Question question) {
        if (question == null) {
            return null;
        }
        
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        
        // 填充创建者信息
        Long userId = question.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                questionVO.setUserVO(userVO);
            }
        }
        
        return questionVO;
    }
} 