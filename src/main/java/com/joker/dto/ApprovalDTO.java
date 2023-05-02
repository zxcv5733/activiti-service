package com.joker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Li dong
 * @date: 2023/4/8 21:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalDTO implements Serializable {
    /**
     * 受理人
     */
    private String assignee;

    /**
     * 业务ID
     */
    private String businessKey;

    /**
     * 评审备注
     */
    private String remarks;

    /**
     * 审批状态
     */
    private Integer state;
}
