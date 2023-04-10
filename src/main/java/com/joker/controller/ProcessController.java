package com.joker.controller;

import cn.hutool.core.util.IdUtil;
import com.joker.dto.DeploymentDTO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.DeploymentEntityImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private RuntimeService runtimeService;



    /**
     * 删除流程定义信息
     * @param processDefinitionId
     * @return
     */
    @DeleteMapping("/deleteProcessDefinition/{processDefinitionId}")
    public String deleteProcessDefinition(@PathVariable String processDefinitionId){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        // 流程定义所属部署id
        String deploymentId = processDefinition.getDeploymentId();
        log.info("删除-流程定义ID: {}", deploymentId);
        // 删除流程部署ID, 参数加true可以联级删除
        repositoryService.deleteDeployment(deploymentId);
        return processDefinitionId;
    }

    /**
     * 创建流程实例
     * @param deploymentDto
     * @return
     */
    @PostMapping("/createDeployment")
    public String createDeployment(@RequestBody DeploymentDTO deploymentDto){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("bpmn/" + deploymentDto.getId() + ".bpmn")
                .name(deploymentDto.getName())
                .deploy();
        List<ProcessDefinitionEntityImpl> deployedArtifacts = ((DeploymentEntityImpl) deployment).getDeployedArtifacts(ProcessDefinitionEntityImpl.class);
        Optional<ProcessDefinitionEntityImpl> optional = deployedArtifacts.stream().findFirst();
        String processDefinitionId = null;
        if (optional.isPresent()){
            processDefinitionId = optional.get().getId();
        }
        log.info("创建-流程实例ID: {}", processDefinitionId);
        return processDefinitionId;
    }

    /**
     * 开启流程实例
     * @param processDefinitionId
     * @return
     */
    @GetMapping("/startProcessInstance/{processDefinitionId}")
    public String startProcessInstance(@PathVariable String processDefinitionId){
        String businessKey = IdUtil.getSnowflakeNextIdStr();
        runtimeService.startProcessInstanceById(processDefinitionId, businessKey);
        log.info("开启-流程业务ID: {}", businessKey);
        return businessKey;
    }

    /**
     * 查询流程定义版本
     * @param processDefinitionKey
     * @return
     */
    @GetMapping("/processDefinitionList/{processDefinitionKey}")
    public List<Map<String, Object>> processDefinitionList(@PathVariable String processDefinitionKey){
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        List<Map<String, Object>> customTaskList = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", processDefinition.getId());
            map.put("name", processDefinition.getName());
            map.put("key", processDefinition.getKey());
            map.put("version", processDefinition.getVersion());
            map.put("deploymentId", processDefinition.getDeploymentId());
            customTaskList.add(map);
        }
        return customTaskList;
    }
}
