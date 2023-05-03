package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/5/3 11:19
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectAnyNodeDTO implements Serializable {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 执行节点的ID
     */
    private String executionId;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 评审备注
     */
    private String remarks;
}
