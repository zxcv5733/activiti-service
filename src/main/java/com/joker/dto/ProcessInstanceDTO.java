package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: Li dong
 * @date: 2023/4/15 21:32
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstanceDTO implements Serializable {
    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * EL表达式动态指定审批人
     */
    private Map<String, Object> variables;
}
