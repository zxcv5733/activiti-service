package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/10 21:43
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeLineDTO implements Serializable {
    /**
     * 流程实例ID
     */
    private String processDefinitionId;
    /**
     * 业务ID
     */
    private String businessKey;
}
