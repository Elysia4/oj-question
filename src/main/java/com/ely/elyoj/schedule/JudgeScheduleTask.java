package com.ely.elyoj.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ely.elyoj.mapper.QuestionSubmitMapper;
import com.ely.elyoj.model.entity.QuestionSubmit;
import com.ely.elyoj.service.JudgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 判题定时任务
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class JudgeScheduleTask {

    private final QuestionSubmitMapper questionSubmitMapper;
    private final JudgeService judgeService;

    /**
     * 每隔10秒检查一次是否有待判题的提交
     */
    @Scheduled(fixedRate = 10000)
    public void doJudge() {
        // 查询状态为等待中的题目提交列表，状态码为0表示等待中
        LambdaQueryWrapper<QuestionSubmit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionSubmit::getStatus, 0);
        // 每次最多处理10条
        queryWrapper.last("limit 10");
        List<QuestionSubmit> submitList = questionSubmitMapper.selectList(queryWrapper);
        
        if (submitList.isEmpty()) {
            return;
        }
        
        log.info("定时任务开始执行判题，待判题数量: {}", submitList.size());
        
        // 并行处理，对每个提交进行异步判题
        for (QuestionSubmit questionSubmit : submitList) {
            CompletableFuture.runAsync(() -> {
                try {
                    judgeService.doJudge(questionSubmit.getId());
                } catch (Exception e) {
                    log.error("判题失败，提交ID: " + questionSubmit.getId(), e);
                }
            });
        }
    }
} 