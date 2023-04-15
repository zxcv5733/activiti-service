package com.joker.service.impl;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.AttachmentDTO;
import com.joker.dto.TimeLineDTO;
import com.joker.dto.UnfinishedTaskDTO;
import com.joker.service.ApprovalService;
import com.joker.vo.AttachmentVO;
import com.joker.vo.NodeInfoVO;
import com.joker.vo.UnfinishedTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:35
 * @description:
 */
@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {


    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RuntimeService runtimeService;


    /**
     * 审批
     *
     * @param approvalDto
     */
    @Override
    public void approval(ApprovalDTO approvalDto) {
        Task task = taskService.createTaskQuery()
                .taskAssignee(approvalDto.getAssignee())
                .processInstanceBusinessKey(approvalDto.getBusinessKey())
                .singleResult();
        log.info("审批人: {} 业务ID: {} 任务: {}", approvalDto.getAssignee(), approvalDto.getBusinessKey(), task);
        // 点击完成
        taskService.complete(task.getId());
    }

    /**
     * 查询代办的任务
     *
     * @param unfinishedTaskDto
     * @return
     */
    @Override
    public List<UnfinishedTaskVO> unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto) {

        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(unfinishedTaskDto.getAssignee())
                .orderByTaskCreateTime().desc()
                .listPage(unfinishedTaskDto.getPageIndex() - 1, unfinishedTaskDto.getPageSize());


        // 报错: Could not write content: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.impl.persistence.entity.TaskEntity[“variableInstances”]);
        // nested exception is com.fasterxml.jackson.databind.JsonMappingException: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.
        // 解决办法: 数据转换自定义对象
        List<UnfinishedTaskVO> unfinishedTasks = new ArrayList<>();
        for (Task task : tasks) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult();

            UnfinishedTaskVO unfinishedTaskVo = UnfinishedTaskVO.builder()
                    .processDefinitionId(processInstance.getProcessDefinitionId())
                    .businessKey(processInstance.getBusinessKey())
                    .processDefinitionKey(processInstance.getProcessDefinitionKey())
                    .processDefinitionVersion(processInstance.getProcessDefinitionVersion())
                    .processDefinitionName(processInstance.getProcessDefinitionName())
                    .startTime(processInstance.getStartTime())
                    .build();
            unfinishedTasks.add(unfinishedTaskVo);
        }

        return unfinishedTasks;
    }

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    @Override
    public List<NodeInfoVO> timeLine(TimeLineDTO timeLineDto) {
        // 查询流程图中所有节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(timeLineDto.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        String status = "待审批";

        // 获取全部的节点信息
        List<NodeInfoVO> nodeInfoVos = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            NodeInfoVO nodeInfoVo = NodeInfoVO.builder().assignee(userTask.getAssignee())
                    .attachments(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .status(status).build();
            nodeInfoVos.add(nodeInfoVo);
        }


        // 查询历史信息，所有完成的任务都会在历史信息中
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(timeLineDto.getBusinessKey())
                .list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            String assignee = historicTaskInstance.getAssignee();
            String processInstanceId = historicTaskInstance.getProcessInstanceId();

            List<String> commentList = new ArrayList<>();
            List<AttachmentVO> attachmentVos = new ArrayList<>();

            // 取出对应评论表中对应当前人的审批意见
            List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId).stream()
                    .filter(m -> "comment".equals(m.getType()))
                    .filter(m -> Objects.nonNull(m.getUserId()))
                    .filter(m -> m.getUserId().equals(assignee)).collect(Collectors.toList());
            if (comments.size() != 0){
                commentList = comments.stream().map(Comment::getFullMessage).collect(Collectors.toList());
            }
            // 查询附件
            List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId);

            if (attachments.size() != 0){
                for (Attachment attachment : attachments) {
                    if (Objects.nonNull(attachment.getUserId()) && attachment.getUserId().equals(assignee)){
                        AttachmentVO attachmentVo = AttachmentVO.builder().attachmentName(attachment.getName())
                                .attachmentDescription(attachment.getDescription())
                                .url(attachment.getUrl()).build();
                        attachmentVos.add(attachmentVo);
                    }
                }
            }
            Date endTime = historicTaskInstance.getEndTime();

            if (Objects.nonNull(endTime)){
                status = "已审批";
            }

            NodeInfoVO nodeInfoVo = NodeInfoVO.builder().assignee(assignee)
                    .comments(commentList)
                    .attachments(attachmentVos)
                    .startTime(historicTaskInstance.getStartTime())
                    .endTime(endTime).status(status).build();
            log.info("节点: {}",nodeInfoVo);
            // 判断全部节点中有则移除，添加当前节点
            Optional<NodeInfoVO> optional = nodeInfoVos.stream().filter(m -> m.getAssignee().equals(assignee)).findFirst();
            optional.ifPresent(nodeInfoVos::remove);
            nodeInfoVos.add(nodeInfoVo);
        }
        return nodeInfoVos;
    }

    /**
     * 添加评论
     *
     * @param approvalDto
     */
    @Override
    public void addComment(ApprovalDTO approvalDto) {
        String assignee = approvalDto.getAssignee();
        String businessKey = approvalDto.getBusinessKey();
        log.info("评论人: {} 业务ID: {}", assignee, businessKey);
        Task task = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        if (Objects.nonNull(task)){
            // 需要添加此句否则审批意见表中ACT_HI_COMMENT，审批人的userId是空的
            Authentication.setAuthenticatedUserId(assignee);
            // 添加备注
            taskService.addComment(task.getId(), task.getProcessInstanceId(), approvalDto.getRemarks());
        }

    }

    /**
     * 上传附件
     *
     * @param attachmentDto
     */
    @Override
    public void uploadAttachment(AttachmentDTO attachmentDto) {
        String assignee = attachmentDto.getAssignee();
        // 添加附件到task中
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(attachmentDto.getBusinessKey())
                .taskAssignee(assignee)
                .singleResult();
        // 需要添加此句否则审批意见表中ACT_HI_ATTACHMENT，审批人的userId是空的
        Authentication.setAuthenticatedUserId(assignee);
        taskService.createAttachment("url", task.getId(), task.getProcessInstanceId(), attachmentDto.getAttachmentName(), attachmentDto.getAttachmentDescription(), attachmentDto.getUrl());

    }

}
