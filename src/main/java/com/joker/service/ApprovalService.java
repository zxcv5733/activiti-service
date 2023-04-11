package com.joker.service;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.AttachmentDTO;
import com.joker.dto.TimeLineDTO;
import com.joker.dto.UnfinishedTaskDTO;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询代办的任务
     * @param unfinishedTaskDto
     * @return
     */
    List<Map<String, Object>> unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto);

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    List<Map<String, Object>> timeLine(TimeLineDTO timeLineDto);

    /**
     * 添加评论
     * @param approvalDto
     */
    void addComment(ApprovalDTO approvalDto);

    /**
     * 上传附件
     * @param attachmentDto
     */
    void uploadAttachment(AttachmentDTO attachmentDto);
}
