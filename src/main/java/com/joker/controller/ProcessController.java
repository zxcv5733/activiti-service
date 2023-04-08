package com.joker.controller;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: Li dong
 * @date: 2023/4/8 15:48
 * @description: 流程定义
 */
@RestController
@Slf4j
public class ProcessController {

    @Resource
    private RepositoryService repositoryService;


    /**
     * 删除流程定义信息
     * @param processDefinitionKey
     */
    @GetMapping("/deleteProcessDefinition/{processDefinitionKey}")
    public String deleteProcessDefinition(@PathVariable String processDefinitionKey){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .singleResult();
        String processDefinitionId = processDefinition.getId();
        log.info("流程定义ID: " + processDefinition.getId());
        // 删除流程部署ID, 参数加true可以联级删除
        repositoryService.deleteDeployment(processDefinitionId, true);
        return processDefinitionId;
    }

    /**
     * 创建流程实例
     * @param name
     * @return
     */
    @GetMapping("/createDeployment/{name}")
    public String createDeployment(@PathVariable String name){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/" + name + ".bpmn")
                .name(name)
                .deploy();
        String deploymentId = deployment.getId();
        log.info("流程实例ID: " + deploymentId);
        return deploymentId;
    }
}
