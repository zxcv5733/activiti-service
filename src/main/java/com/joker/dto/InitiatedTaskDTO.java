package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/9 20:31
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiatedTaskDTO implements Serializable {
    /**
     * 发起人
     */
    private String user;

    /**
     * 当前页数
     */
    private Integer pageIndex;

    /**
     * 页面大小
     */
    private Integer pageSize;
}
