package com.joker.service.impl;

import com.joker.dto.ApprovalDTO;
import com.joker.dto.TimeLineDTO;
import com.joker.dto.UnfinishedTaskDTO;
import com.joker.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
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
        // 需要添加此句否则审批意见表中ACT_HI_COMMENT，审批人的userId是空的
        Authentication.setAuthenticatedUserId(approvalDto.getAssignee());
        // 添加备注
        taskService.addComment(task.getId(), task.getProcessInstanceId(), approvalDto.getRemarks());
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
    public List<Map<String, Object>> unfinishedTask(UnfinishedTaskDTO unfinishedTaskDto) {

        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(unfinishedTaskDto.getAssignee())
                .processInstanceBusinessKey(unfinishedTaskDto.getBusinessKey()).list();

        // 报错: Could not write content: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.impl.persistence.entity.TaskEntity[“variableInstances”]);
        // nested exception is com.fasterxml.jackson.databind.JsonMappingException: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.
        // 解决办法: 数据转换自定义对象
        List<Map<String, Object>> customTaskList = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("taskId", task.getId());
            map.put("taskDefinitionKey", task.getTaskDefinitionKey());
            map.put("taskName", task.getName());
            customTaskList.add(map);
        }

        return customTaskList;
    }

    /**
     * 获取时间轴
     * @param timeLineDto
     * @return
     */
    @Override
    public List<Map<String, Object>> timeLine(TimeLineDTO timeLineDto) {
        // 查询流程图中所有节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(timeLineDto.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        List<Map<String, Object>> customList = new ArrayList<>();

        for (UserTask userTask : userTasks) {
            String assignee = userTask.getAssignee();
            // 查询历史信息，所有完成的任务都会在历史信息中
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceBusinessKey(timeLineDto.getBusinessKey())
                    .taskAssignee(assignee)
                    .singleResult();
            Map<String, Object> map = new LinkedHashMap<>();
            Date endTime = null;
            String status = "未审批";
            // historicTaskInstance不为空值说明已经完成或者当前到该节点审批
            if (Objects.nonNull(historicTaskInstance)){
                endTime = historicTaskInstance.getEndTime();
                // endTime不为空值说明已经完成审批
                if (Objects.nonNull(endTime)){
                    status = "已审批";
                }
            }
            List<String> commentList = new ArrayList<>();
            // 取出对应评论表中对应当前人的审批意见
            List<Comment> comments = taskService.getProcessInstanceComments(historicTaskInstance.getProcessInstanceId()).stream().filter(m -> m.getUserId().equals(assignee)).collect(Collectors.toList());
            if (comments.size() != 0){
                commentList = comments.stream().map(Comment::getFullMessage).collect(Collectors.toList());
            }
            map.put("assignee", assignee);
            map.put("endTime", endTime);
            map.put("status", status);
            map.put("comment", commentList);
            log.info("节点信息: {}", map);
            customList.add(map);
        }
        return customList;
    }

}
