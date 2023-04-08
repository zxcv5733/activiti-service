package com.joker.service.impl;

import cn.hutool.core.util.IdUtil;
import com.joker.service.ApprovalService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author: Li dong
 * @date: 2023/4/8 14:35
 * @description:
 */
@Service
public class ApprovalServiceImpl implements ApprovalService {


    @Resource
    private TaskService taskService;

    @Resource
    private RuntimeService runtimeService;

    /**
     * 添加审批
     */
    @Override
    public void add() {
        String taskId = IdUtil.getSnowflakeNextIdStr();
        runtimeService.startProcessInstanceByKey("task", taskId);

        Task task = taskService.createTaskQuery()
                .processDefinitionKey("task")
                .taskAssignee("custom")
                .processInstanceBusinessKey(taskId)
                .singleResult();

        System.out.println(task);

    }
}
