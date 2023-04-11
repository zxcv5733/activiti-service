package com.joker.controller;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.AttachmentDTO;
import com.joker.dto.TimeLineDTO;
import com.joker.dto.UnfinishedTaskDTO;
import com.joker.service.ApprovalService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:32
 * @description: 审批中心
 */
@RestController
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    /**
     * 审批
     * @param approvalDto
     */
    @PostMapping("/approval")
    public void approval(@RequestBody ApprovalDTO approvalDto){
        approvalService.approval(approvalDto);
    }

    /**
     * 添加评论
     * @param approvalDto
     */
    @PostMapping("/addComment")
    public void addComment(@RequestBody ApprovalDTO approvalDto){
        approvalService.addComment(approvalDto);
    }

    /**
     * 上传附件
     */
    @PostMapping("/uploadAttachment")
    public void uploadAttachment(@RequestBody AttachmentDTO attachmentDto){
        approvalService.uploadAttachment(attachmentDto);
    }

    /**
     * 查询代办任务
     * @param unfinishedTaskDto
     * @return
     */
    @PostMapping("/unfinishedTask")
    public List<Map<String, Object>> unfinishedTask(@RequestBody UnfinishedTaskDTO unfinishedTaskDto){
        return approvalService.unfinishedTask(unfinishedTaskDto);
    }

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    @PostMapping("/timeLine")
    public List<Map<String, Object>>  timeLine(@RequestBody TimeLineDTO timeLineDto){
        return approvalService.timeLine(timeLineDto);
    }
}
