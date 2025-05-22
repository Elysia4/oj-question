package com.ely.elyoj.service.impl;

import com.ely.elyoj.common.ErrorCode;
import com.ely.elyoj.exception.BusinessException;
import com.ely.elyoj.mapper.QuestionSubmitMapper;
import com.ely.elyoj.model.entity.Question;
import com.ely.elyoj.model.entity.QuestionSubmit;
import com.ely.elyoj.service.JudgeService;
import com.ely.elyoj.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 判题服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    // 直接使用Mapper而不是Service，避免循环依赖
    private final QuestionSubmitMapper questionSubmitMapper;
    private final QuestionService questionService;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // 1. 获取题目提交信息
        QuestionSubmit questionSubmit = questionSubmitMapper.selectById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交记录不存在");
        }
        
        // 2. 判断题目提交状态，如果不是等待中，则不进行判题
        Integer status = questionSubmit.getStatus();
        if (!status.equals(0)) {
            log.info("当前提交已经判题，题目ID: {}, 状态: {}", questionSubmitId, status);
            return questionSubmit;
        }
        
        // 3. 更新题目提交状态为判题中
        QuestionSubmit updateSubmit = new QuestionSubmit();
        updateSubmit.setId(questionSubmitId);
        updateSubmit.setStatus(1);
        int updated = questionSubmitMapper.updateById(updateSubmit);
        if (updated <= 0) {
            log.error("更新题目提交状态失败, 题目ID: {}", questionSubmitId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "判题状态更新失败");
        }
        
        // 4. 获取题目信息和判题用例
        Question question = questionService.getById(questionSubmit.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        
        // 5. 调用判题逻辑
        String submitLanguage = questionSubmit.getLanguage();
        String submitCode = questionSubmit.getCode();
        String judgeCase = question.getJudgeCase();
        String answer = question.getAnswer();
        
        // 6. 执行判题
        JSONObject judgeResult = judgeCode(submitLanguage, submitCode, judgeCase, answer);
        
        // 7. 更新题目提交状态和判题结果
        updateSubmit = new QuestionSubmit();
        updateSubmit.setId(questionSubmitId);
        updateSubmit.setStatus(judgeResult.getBoolean("success") ? 2 : 3); // 2-成功，3-失败
        updateSubmit.setJudgeInfo(judgeResult.toString());
        questionSubmitMapper.updateById(updateSubmit);
        
        // 8. 如果通过测试，更新题目通过数
        if (judgeResult.getBoolean("success")) {
            Question updateQuestion = new Question();
            updateQuestion.setId(question.getId());
            updateQuestion.setAcceptedNum(question.getAcceptedNum() + 1);
            boolean updatedQuestion = questionService.updateById(updateQuestion);
            if (!updatedQuestion) {
                log.error("更新题目通过数失败, 题目ID: {}", question.getId());
            }
        }
        
        return questionSubmitMapper.selectById(questionSubmitId);
    }
    
    /**
     * 执行判题
     * @param language 编程语言
     * @param code 提交代码
     * @param judgeCase 判题用例
     * @param answer 标准答案
     * @return 判题结果
     */
    private JSONObject judgeCode(String language, String code, String judgeCase, String answer) {
        JSONObject result = new JSONObject();
        
        try {
            // 模拟判题过程，实际项目中应该根据语言类型调用不同的编译和执行逻辑
            log.info("开始执行判题, 语言: {}", language);
            
            // 解析判题用例
            List<String> inputs = new ArrayList<>();
            List<String> expectedOutputs = new ArrayList<>();
            
            try {
                JSONArray caseArray = new JSONArray(judgeCase);
                for (int i = 0; i < caseArray.length(); i++) {
                    JSONObject caseObj = caseArray.getJSONObject(i);
                    inputs.add(caseObj.getString("input"));
                    expectedOutputs.add(caseObj.getString("output"));
                }
            } catch (Exception e) {
                log.error("解析判题用例失败", e);
                result.put("success", false);
                result.put("message", "判题用例格式错误");
                return result;
            }
            
            // 模拟执行代码，这里简化处理，实际需要根据不同语言调用不同的执行器
            // 模拟执行耗时
            TimeUnit.MILLISECONDS.sleep(200);
            
            // 这里是简化的判题逻辑，实际项目中需要根据代码语言类型进行编译和执行
            // 这里我们简单比较代码中是否包含题目答案中的关键代码段
            if (code.contains("return") && answer != null && code.contains(answer.trim())) {
                result.put("success", true);
                result.put("message", "提交通过");
                result.put("memory", 156); // 模拟内存占用 (KB)
                result.put("time", 128);   // 模拟执行时间 (ms)
            } else {
                result.put("success", false);
                result.put("message", "提交失败，答案不正确");
                result.put("memory", 150); // 模拟内存占用 (KB)
                result.put("time", 110);   // 模拟执行时间 (ms)
            }
            
        } catch (Exception e) {
            log.error("判题过程出错", e);
            result.put("success", false);
            result.put("message", "判题过程出错: " + e.getMessage());
        }
        
        return result;
    }
} 