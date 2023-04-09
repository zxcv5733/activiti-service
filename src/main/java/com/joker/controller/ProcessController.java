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
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("zhangsan", "lisi");
        runtimeService.startProcessInstanceById(processDefinitionId, businessKey, variables);
        log.info("开启-流程业务ID: {}", businessKey);
        return businessKey;
    }
}
