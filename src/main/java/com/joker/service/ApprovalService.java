package com.joker.service;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.UnfinishedTaskDTO;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:32
 * @description:
 */
public interface ApprovalService {

    /**
     * 审批
     * @param approvalDto
     */
    void approval(ApprovalDTO approvalDto);

    /**
     * 查询代办的任务
     * @param unfinishedTaskDto
     * @return
     */
    Object unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto);
}
