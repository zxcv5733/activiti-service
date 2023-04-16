package com.joker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: Li dong
 * @date: 2023/4/15 11:04
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskVO implements Serializable {
    /**
     * 流程实例ID
     */
    private String processDefinitionId;

    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 流程定义版本号
     */
    private Integer processDefinitionVersion;

    /**
     * 流程定义的key
     */
    private String processDefinitionKey;

    /**
     * 流程定义的名字
     */
    private String processDefinitionName;

    /**
     * 开始时间
     */
    private Date startTime;

}
