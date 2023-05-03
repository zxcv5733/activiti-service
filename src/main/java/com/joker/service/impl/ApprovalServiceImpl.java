package com.joker.service.impl;

import cn.hutool.core.util.IdUtil;
import com.joker.dto.*;
import com.joker.enums.ApproveTypeEnum;
import com.joker.service.ApprovalService;
import com.joker.vo.AttachmentVO;
import com.joker.vo.NodeInfoVO;
import com.joker.vo.RejectNodeVO;
import com.joker.vo.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
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
        String assignee = approvalDto.getAssignee();
        Task task = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .processInstanceBusinessKey(approvalDto.getBusinessKey())
                .singleResult();
        log.info("审批人: {} 业务ID: {} 任务: {}", assignee, approvalDto.getBusinessKey(), task);
        String taskId = task.getId();
        // 添加评论
        addComment(taskId, task.getProcessInstanceId(), approvalDto.getRemarks(), assignee);

        Integer state = approvalDto.getState();
        // 完成任务
        taskComplete(taskId, state);
    }



    /**
     * 查询代办的任务
     *
     * @param unfinishedTaskDto
     * @return
     */
    @Override
    public List<TaskVO> unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto) {

        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(unfinishedTaskDto.getAssignee())
                .orderByTaskCreateTime().desc()
                .listPage(unfinishedTaskDto.getPageIndex() - 1, unfinishedTaskDto.getPageSize());

        // 报错: Could not write content: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.impl.persistence.entity.TaskEntity[“variableInstances”]);
        // nested exception is com.fasterxml.jackson.databind.JsonMappingException: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.
        // 解决办法: 数据转换自定义对象
        Set<String> processInstanceIds = tasks.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceIds(processInstanceIds)
                .list();
        return getTaskList(historicProcessInstances);
    }

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    @Override
    public List<NodeInfoVO> timeLine(TimeLineDTO timeLineDto) {
        // 实际所有节点
        List<NodeInfoVO> nodeInfoVos = new ArrayList<>();

        // 添加流程发起人
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(timeLineDto.getBusinessKey())
                .singleResult();

        nodeInfoVos.add(NodeInfoVO.builder().startUser(historicProcessInstance.getStartUserId())
                .startTime(historicProcessInstance.getStartTime()).assignee("").build());
        // 流程实例ID
        String processInstanceId = historicProcessInstance.getId();

        // 查询历史信息，所有完成的任务都会在历史信息中
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list()
                .stream().filter(m -> "userTask".equals(m.getActivityType()))
                .collect(Collectors.toList());

        // 取出对应评论表中对应当前人的审批意见
        Map<String, List<String>> commentMap = getCommentMap(processInstanceId);

        // 查询附件
        Map<String, List<AttachmentVO>> attachmentMap = getAttachmentMap(processInstanceId);

        // 查询节点变量
        Map<String, Object> variableMap = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream().filter(m -> Objects.nonNull(m.getTaskId()))
                .filter(m -> "state".equals(m.getVariableName()))
                .collect(Collectors.toMap(HistoricVariableInstance::getTaskId, HistoricVariableInstance::getValue));


        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            String taskId = historicActivityInstance.getTaskId();
            List<String> commentList = new ArrayList<>();
            List<AttachmentVO> attachmentVos = new ArrayList<>();

            // 判断当前节点中是否有评论信息
            if (Objects.nonNull(commentMap)){
                List<String> resultList = commentMap.get(taskId);
                if (Objects.nonNull(resultList)){
                    commentList = resultList;
                }
            }

            // 判断当前节点中是否有上传附件
            if (Objects.nonNull(attachmentMap)){
                List<AttachmentVO> attachmentList = attachmentMap.get(taskId);
                if (Objects.nonNull(attachmentList)){
                    attachmentVos = attachmentList;
                }
            }

            // 判断当前节点中变量状态
            Integer state = ApproveTypeEnum.AWAIT_APPROVAL.getKey();
            if (variableMap.size() != 0){
                state = (int)variableMap.get(taskId);
            }
            Date endTime = historicActivityInstance.getEndTime();
            if (Objects.isNull(endTime)){
                state = ApproveTypeEnum.AWAIT_APPROVAL.getKey();
            }

            NodeInfoVO nodeInfoVo = NodeInfoVO.builder().assignee(historicActivityInstance.getAssignee())
                    .comments(commentList)
                    .attachments(attachmentVos)
                    .startTime(historicActivityInstance.getStartTime())
                    .endTime(endTime).state(state).build();
            log.info("节点: {}",nodeInfoVo);
            nodeInfoVos.add(nodeInfoVo);

        }

        // 获取原始节点
        List<NodeInfoVO> sourceNode = getSourceNode(timeLineDto);

        // 取集合的差集，求出全量的节点
        Map<String, NodeInfoVO> sourceMap = sourceNode.stream().collect(Collectors.toMap(NodeInfoVO::getAssignee, m -> m));
        for (String s : sourceMap.keySet()) {
            long count = nodeInfoVos.stream().filter(m -> m.getAssignee().equals(s)).count();
            if (count == 0){
                nodeInfoVos.add(sourceMap.get(s));
            }
        }

        return nodeInfoVos;
    }

    /**
     * 获取原始节点
     * @param timeLineDto
     * @return
     */
    private List<NodeInfoVO> getSourceNode(TimeLineDTO timeLineDto) {
        // 查询流程图中所有节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(timeLineDto.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);


        // 原始节点
        List<NodeInfoVO> nodeInfoVos = new ArrayList<>();

        // 获取全部的节点信息
        for (UserTask userTask : userTasks) {
            NodeInfoVO nodeInfoVo = NodeInfoVO.builder().assignee(userTask.getAssignee())
                    .attachments(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .state(ApproveTypeEnum.AWAIT_APPROVAL.getKey()).build();
            nodeInfoVos.add(nodeInfoVo);
        }
        return nodeInfoVos;
    }

    /**
     * 获取评论
     * @param processInstanceId
     * @return
     */
    private Map<String, List<String>> getCommentMap(String processInstanceId) {
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId, "comment").stream()
                .filter(m -> Objects.nonNull(m.getUserId())).collect(Collectors.toList());
        Map<String, List<String>> commentMap = null;
        if (comments.size() != 0){
            commentMap = comments.stream().collect(Collectors.groupingBy(Comment::getTaskId,
                    Collectors.collectingAndThen(Collectors.toList(), m -> m.stream().map(Comment::getFullMessage).collect(Collectors.toList()))));
        }
        return commentMap;
    }

    /**
     * 获取附件
     * @param processInstanceId
     * @return
     */
    private Map<String, List<AttachmentVO>> getAttachmentMap(String processInstanceId) {
        List<Attachment> attachments = taskService.getProcessInstanceAttachments(processInstanceId).stream()
                .filter(m -> Objects.nonNull(m.getUserId())).collect(Collectors.toList());
        Map<String, List<AttachmentVO>> attachmentMap = null;
        if (attachments.size() != 0){
            attachmentMap = attachments.stream().collect(Collectors.groupingBy(Attachment::getTaskId,
                    Collectors.collectingAndThen(Collectors.toList(),
                            m -> m.stream().map(attachment -> AttachmentVO.builder().attachmentName(attachment.getName())
                            .attachmentDescription(attachment.getDescription())
                            .url(attachment.getUrl()).build()).collect(Collectors.toList()))));
        }
        return attachmentMap;
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
            addComment(task.getId(), task.getProcessInstanceId(), approvalDto.getRemarks(), assignee);
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

    /**
     * 已经完成任务
     *
     * @param finishedTaskDto
     * @return
     */
    @Override
    public List<TaskVO> finishedTask(FinishedTaskDTO finishedTaskDto) {

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(finishedTaskDto.getAssignee())
                .finished()
                .orderByHistoricActivityInstanceStartTime().desc()
                .listPage(finishedTaskDto.getPageIndex() - 1, finishedTaskDto.getPageSize());

        Set<String> processInstanceIds = historicActivityInstances.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toSet());

        // 查询历史流程实例
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceIds(processInstanceIds)
                .list();
        return getTaskList(historicProcessInstances);
    }

    /**
     * 我发起的任务
     *
     * @param initiatedTaskDto
     * @return
     */
    @Override
    public List<TaskVO> initiatedTask(InitiatedTaskDTO initiatedTaskDto) {
        // startedBy 查询我发起的
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .startedBy(initiatedTaskDto.getUser())
                .orderByProcessInstanceStartTime().desc()
                .listPage(initiatedTaskDto.getPageIndex() - 1, initiatedTaskDto.getPageSize());

        return getTaskList(historicProcessInstances);
    }

    /**
     * 驳回的节点列表
     * @param businessKey
     * @return
     */
    @Override
    public List<RejectNodeVO> rejectNodeList(String businessKey) {
        // 取出当前节点信息
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .orderByHistoricTaskInstanceStartTime()
                .desc()
                .list()
                .get(0);

        String assignee = historicTaskInstance.getAssignee();

        // 获取节点为userTask类型的数据
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .executionId(historicTaskInstance.getExecutionId())
                .activityType("userTask")
                .finished()
                .list();

        // 数据转换
        List<RejectNodeVO> rejectNodeVos = new ArrayList<>();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            // 获取当前节点之前的审批人信息
            if (assignee.equals(historicActivityInstance.getAssignee())){
                break;
            }
            RejectNodeVO rejectNodeVO = RejectNodeVO.builder()
                    .assignee(historicActivityInstance.getAssignee())
                    .taskId(historicActivityInstance.getTaskId())
                    .executionId(historicActivityInstance.getExecutionId())
                    .processDefinitionId(historicActivityInstance.getProcessDefinitionId()).build();
            rejectNodeVos.add(rejectNodeVO);
        }

        return rejectNodeVos;

    }

    /**
     * 驳回到任务节点
     *
     * @param rejectAnyNodeDto
     * @return
     */
    @Override
    public void rejectAnyNode(RejectAnyNodeDTO rejectAnyNodeDto) {
        List<HistoricTaskInstance> historicTaskInstances = getHistoricTaskInstances(rejectAnyNodeDto.getBusinessKey());

        Integer size = 2;

        if (Objects.isNull(historicTaskInstances) || historicTaskInstances.size() < size) {
            return;
        }

        // 当前任务实例
        HistoricTaskInstance currentTaskInstance = historicTaskInstances.get(0);

        BpmnModel bpmnModel = repositoryService.getBpmnModel(rejectAnyNodeDto.getProcessDefinitionId());

        String beforeActivityId = null;
        // 得到ActivityId，只有HistoricActivityInstance对象里才有此方法
        Optional<String> optional = historyService.createHistoricActivityInstanceQuery()
                .activityType("userTask")
                .executionId(rejectAnyNodeDto.getExecutionId()).finished().list()
                .stream().filter(m -> m.getTaskId().equals(rejectAnyNodeDto.getTaskId()))
                .map(HistoricActivityInstance::getActivityId).findFirst();

        if (optional.isPresent()){
            beforeActivityId = optional.get();
        }
        // 版本回退
        rollback(rejectAnyNodeDto.getRemarks(), currentTaskInstance, bpmnModel, beforeActivityId);
    }

    private List<HistoricTaskInstance> getHistoricTaskInstances(String businessKey) {
        return historyService.createHistoricTaskInstanceQuery()
                    .processInstanceBusinessKey(businessKey)
                    .orderByHistoricTaskInstanceStartTime()
                    .desc()
                    .list();
    }

    /**
     * 驳回到上一个节点
     *
     * @param rollbackDto
     */
    @Override
    public void rejectBeforeNode(RollbackDTO rollbackDto) {
        List<HistoricTaskInstance> historicTaskInstances = getHistoricTaskInstances(rollbackDto.getBusinessKey());

        Integer size = 2;

        if (Objects.isNull(historicTaskInstances) || historicTaskInstances.size() < size) {
            return;
        }

        // 当前任务实例
        HistoricTaskInstance currentTaskInstance = historicTaskInstances.get(0);

        // 上一个任务实例
        HistoricTaskInstance beforeTaskInstance = historicTaskInstances.get(1);


        BpmnModel bpmnModel = repositoryService.getBpmnModel(beforeTaskInstance.getProcessDefinitionId());

        String beforeActivityId = null;
        // 得到ActivityId，只有HistoricActivityInstance对象里才有此方法
        Optional<String> optional = historyService.createHistoricActivityInstanceQuery()
                .executionId(beforeTaskInstance.getExecutionId()).finished().list()
                .stream().filter(m -> Objects.nonNull(m.getTaskId()))
                .filter(m -> m.getTaskId().equals(beforeTaskInstance.getId()))
                .map(HistoricActivityInstance::getActivityId).findFirst();

        if (optional.isPresent()){
            beforeActivityId = optional.get();
        }
        // 版本回退
        rollback(rollbackDto.getRemarks(), currentTaskInstance, bpmnModel, beforeActivityId);
    }

    /**
     * 进行节点回退
     * @param remarks
     * @param currentTaskInstance
     * @param bpmnModel
     * @param beforeActivityId
     */
    private void rollback(String remarks, HistoricTaskInstance currentTaskInstance, BpmnModel bpmnModel, String beforeActivityId) {
        if (Objects.nonNull(beforeActivityId)){
            // 得到上个节点的信息
            FlowNode beforeFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(beforeActivityId);

            // 取得当前节点的信息
            Execution execution = runtimeService.createExecutionQuery().executionId(currentTaskInstance.getExecutionId()).singleResult();
            FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(execution.getActivityId());

            // 记录当前节点的原活动方向
            List<SequenceFlow> sourceSequenceFlowList = new ArrayList<>(currentFlowNode.getOutgoingFlows());

            // 清理活动方向
            currentFlowNode.getOutgoingFlows().clear();

            // 建立新方向
            List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
            SequenceFlow newSequenceFlow = new SequenceFlow();
            newSequenceFlow.setId(IdUtil.simpleUUID());
            newSequenceFlow.setSourceFlowElement(currentFlowNode);
            newSequenceFlow.setTargetFlowElement(beforeFlowNode);
            newSequenceFlowList.add(newSequenceFlow);
            currentFlowNode.setOutgoingFlows(newSequenceFlowList);

            // 添加评论
            addComment(currentTaskInstance.getId(), currentTaskInstance.getProcessInstanceId(), remarks, currentTaskInstance.getAssignee());

            // 完成任务
            taskComplete(currentTaskInstance.getId(), ApproveTypeEnum.REJECT.getKey());

            // 恢复原方向
            currentFlowNode.setOutgoingFlows(sourceSequenceFlowList);

        }
    }


    /**
     * 获取任务列表
     * @param historicProcessInstances
     * @return
     */
    private List<TaskVO> getTaskList(List<HistoricProcessInstance> historicProcessInstances) {
        List<TaskVO> taskVos = new ArrayList<>();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            TaskVO taskVo = TaskVO.builder()
                    .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                    .businessKey(historicProcessInstance.getBusinessKey())
                    .processDefinitionKey(historicProcessInstance.getProcessDefinitionKey())
                    .processDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion())
                    .processDefinitionName(historicProcessInstance.getProcessDefinitionName())
                    .startTime(historicProcessInstance.getStartTime())
                    .build();
            taskVos.add(taskVo);
        }

        return taskVos;
    }

    /**
     * 添加评论
     * @param taskId
     * @param processInstanceId
     * @param remarks
     * @param assignee
     */
    private void addComment(String taskId, String processInstanceId, String remarks, String assignee){
        if (Objects.nonNull(remarks)){
            // 需要添加此句否则审批意见表中ACT_HI_COMMENT，审批人的userId是空的
            Authentication.setAuthenticatedUserId(assignee);
            // 添加备注
            taskService.addComment(taskId, processInstanceId, remarks);
        }
    }

    /**
     * 完成任务
     * @param taskId
     * @param state
     */
    private void taskComplete(String taskId, Integer state) {
        HashMap<String, Object> variables = new HashMap<String, Object>(1) {{
            put("state", state);
        }};
        // 点击完成
        taskService.complete(taskId, variables, true);
    }
}
