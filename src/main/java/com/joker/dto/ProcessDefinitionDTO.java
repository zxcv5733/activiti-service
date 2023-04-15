package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/15 13:19
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDefinitionDTO implements Serializable {
    /**
     * 当前页数
     */
    private Integer pageIndex;

    /**
     * 页面大小
     */
    private Integer pageSize;
}
