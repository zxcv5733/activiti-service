package com.joker.controller;

import com.joker.dto.*;
import com.joker.service.ApprovalService;
import com.joker.vo.NodeInfoVO;
import com.joker.vo.TaskVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


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
    public List<TaskVO> unfinishedTask(@RequestBody UnfinishedTaskDTO unfinishedTaskDto){
        return approvalService.unfinishedTask(unfinishedTaskDto);
    }

    /**
     * 查询已经完成任务
     * @return
     */
    @PostMapping("/finishedTask")
    public List<TaskVO> finishedTask(@RequestBody FinishedTaskDTO finishedTaskDto){
        return approvalService.finishedTask(finishedTaskDto);
    }

    /**
     * 我发起的任务
     * @return
     */
    @PostMapping("/initiatedTask")
    public List<TaskVO> initiatedTask(@RequestBody InitiatedTaskDTO initiatedTaskDto){
        return approvalService.initiatedTask(initiatedTaskDto);
    }

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    @PostMapping("/timeLine")
    public List<NodeInfoVO>  timeLine(@RequestBody TimeLineDTO timeLineDto){
        return approvalService.timeLine(timeLineDto);
    }

    /**
     * 驳回
     * @return
     */
    @PostMapping("/reject")
    public void reject(@RequestBody RollbackDTO rollbackDto){
        approvalService.reject(rollbackDto);
    }
}
