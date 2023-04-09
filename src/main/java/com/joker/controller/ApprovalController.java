package com.joker.controller;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.UnfinishedTaskDTO;
import com.joker.service.ApprovalService;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:32
 * @description: 审批中心
 */
@RestController
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    /**
     * 审批
     * @param approvalDto
     */
    @PostMapping("/approval")
    public void approval(@RequestBody ApprovalDTO approvalDto){
        approvalService.approval(approvalDto);
    }

    /**
     * 查询代办任务
     * @param unfinishedTaskDto
     * @return
     */
    @PostMapping("/unfinishedTask")
    public Object unfinishedTask(@RequestBody UnfinishedTaskDTO unfinishedTaskDto){
        return approvalService.unfinishedTask(unfinishedTaskDto);
    }
}
