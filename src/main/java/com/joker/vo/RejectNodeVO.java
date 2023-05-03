package com.joker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/5/3 10:19
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectNodeVO implements Serializable {
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
}
