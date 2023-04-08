package com.joker.controller;

import com.joker.service.ApprovalService;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/add")
    public void add(){
        approvalService.add();
    }
}
