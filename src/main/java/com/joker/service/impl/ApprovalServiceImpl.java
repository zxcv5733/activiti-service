package com.joker.service.impl;

import com.joker.dto.ApprovalDTO;
import com.joker.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:35
 * @description:
 */
@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {


    @Resource
    private TaskService taskService;


    /**
     * 审批
     *
     * @param approvalDto
     */
    @Override
    public void approval(ApprovalDTO approvalDto) {
        Task task = taskService.createTaskQuery()
                .taskAssignee(approvalDto.getAssignee())
                .processInstanceBusinessKey(approvalDto.getBusinessKey())
                .singleResult();
        log.info("审批人: {} 业务ID: {} 任务: {}", approvalDto.getAssignee(), approvalDto.getBusinessKey(), task);
        taskService.complete(task.getId());
    }
}
