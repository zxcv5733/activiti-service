package com.joker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/15 13:06
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessDefinitionVO implements Serializable {
    /**
     * 流程定义ID
     */
    private String id;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程key
     */
    private String key;
    /**
     * 流程版本
     */
    private Integer version;
    /**
     * 流程部署ID
     */
    private String deploymentId;

}
