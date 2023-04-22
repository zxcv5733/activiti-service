package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/22 20:44
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollbackDTO implements Serializable {
    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 受理人
     */
    private String assignee;

    /**
     * 评审备注
     */
    private String remarks;

}
