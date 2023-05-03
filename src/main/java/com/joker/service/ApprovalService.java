package com.joker.service;

import com.joker.dto.*;
import com.joker.vo.NodeInfoVO;
import com.joker.vo.RejectNodeVO;
import com.joker.vo.TaskVO;
import java.util.List;

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
    List<TaskVO> unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto);

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    List<NodeInfoVO> timeLine(TimeLineDTO timeLineDto);

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

    /**
     * 已经完成任务
     * @param finishedTaskDto
     * @return
     */
    List<TaskVO> finishedTask(FinishedTaskDTO finishedTaskDto);

    /**
     * 我发起的任务
     * @param initiatedTaskDto
     * @return
     */
    List<TaskVO> initiatedTask(InitiatedTaskDTO initiatedTaskDto);

    /**
     * 驳回的节点列表
     * @param businessKey
     * @return
     */
    List<RejectNodeVO> rejectNodeList(String businessKey);

    /**
     * 驳回到任务节点
     * @param rejectAnyNodeDto
     */
    void rejectAnyNode(RejectAnyNodeDTO rejectAnyNodeDto);

    /**
     * 驳回到上一个节点
     * @param rollbackDto
     */
    void rejectBeforeNode(RollbackDTO rollbackDto);
}
