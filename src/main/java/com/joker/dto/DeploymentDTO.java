package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/8 19:21
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentDTO implements Serializable {
    /**
     * 流程名字
     */
    private String name;
    /**
     * 流程id
     */
    private String id;
}
