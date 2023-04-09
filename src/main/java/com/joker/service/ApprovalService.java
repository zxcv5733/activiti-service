package com.joker.service;

import com.joker.dto.ApprovalDTO;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:32
 * @description:
 */
public interface ApprovalService {

    /**
     * 审批
     * @param approvalDto
     */
    void approval(ApprovalDTO approvalDto);
}
