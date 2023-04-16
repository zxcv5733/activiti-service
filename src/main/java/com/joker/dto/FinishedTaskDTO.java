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
public class FinishedTaskDTO implements Serializable {
    /**
     * 受理人
     */
    private String assignee;

    /**
     * 当前页数
     */
    private Integer pageIndex;

    /**
     * 页面大小
     */
    private Integer pageSize;
}
