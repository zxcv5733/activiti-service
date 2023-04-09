package com.joker.controller;

import com.joker.dto.ApprovalDTO;
import com.joker.service.ApprovalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:32
 * @description: 审批中心
 */
@RestController
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    @PostMapping("/approval")
    public void approval(@RequestBody ApprovalDTO approvalDto){
        approvalService.approval(approvalDto);
    }
}
