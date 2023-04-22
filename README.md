# activiti-service

#### 表说明

表名默认以“ACT_”开头,并且表名的第二部分用两个字母表明表的用例，而这个用例也基本上跟Service API匹配。

ACT_GE_* : “GE”代表“General”（通用），用在各种情况下；

ACT_HI_* : “HI”代表“History”（历史），这些表中保存的都是历史数据，比如执行过的流程实例、变量、任务，等等。Activit默认提供了4种历史级别：

ACT_ID_* : “ID”代表“Identity”（身份），这些表中保存的都是身份信息，如用户和组以及两者之间的关系。如果Activiti被集成在某一系统当中的话，这些表可以不用，可以直接使用现有系统中的用户或组信息；

ACT_RE_* : “RE”代表“Repository”（仓库），这些表中保存一些‘静态’信息，如流程定义和流程资源（如图片、规则等）；

ACT_RU_* : “RU”代表“Runtime”（运行时），这些表中保存一些流程实例、用户任务、变量等的运行时数据。Activiti只保存流程实例在执行过程中的运行时数据，并且当流程结束后会立即移除这些数据，这是为了保证运行时表尽量的小并运行的足够快；

| 表分类       | 表名称                | 表含义                        |
| ------------ | --------------------- | ----------------------------- |
|              | act_evt_log           | 事件处理日志表                |
| 一般数据     | act_ge_bytearray      | 通用的流程定义和流程资源      |
|              | act_ge_property       | 系统相关属性                  |
| 流程历史记录 | act_hi_actinst        | 历史的流程实例                |
|              | act_hi_attachment     | 历史的流程附件                |
|              | act_hi_comment        | 历史的说明性信息              |
|              | act_hi_detail         | 历史的流程运行中的细节信息    |
|              | act_hi_identitylink   | 历史的流程运行过程中用户关系  |
|              | act_hi_procinst       | 历史的流程实例                |
|              | act_hi_taskinst       | 历史的任务实例                |
|              | act_hi_varinst        | 历史的流程运行中的变量信息    |
| 用户用户组表 | act_id_group          | 身份信息-组信息               |
|              | act_id_info           | 身份信息-组信息               |
|              | act_id_membership     | 身份信息-用户和组关系的中间表 |
|              | act_id_user           | 身份信息-用户信息             |
|              | act_procdef_info      | 死信任务                      |
| 流程定义表   | act_re_deployment     | 部署单元信息                  |
|              | act_re_model          | 模型信息                      |
|              | act_re_procdef        | 已部署的流程定义              |
| 运行实例表   | act_ru_deadletter_job | 执行失败任务表                |
|              | act_ru_event_subscr   | 运行时事件                    |
|              | act_ru_execution      | 运行时流程执行实例            |
|              | act_ru_identitylink   | 运行时用户关系信息            |
|              | act_ru_job            | 运行时作业                    |
|              | act_ru_suspended_job  | 运行时暂停任务                |
|              | act_ru_task           | 运行时任务                    |
|              | act_ru_timer_job      | 运行时定时任务                |
|              | act_ru_variable       | 运行时变量表                  |

具体的表结构及详细介绍可参考[
Activiti数据库表结构](https://links.jianshu.com/go?to=https%3A%2F%2Fblog.csdn.net%2Fhj7jay%2Farticle%2Fdetails%2F51302829) 里面有详细的每个表介绍。

#### 流程概念和术语

(1) 一个ProcessDefinition代表的业务流程。它用于定义流程中不同步骤的结构和行为。

(2) 部署流程定义意味着将流程定义加载到Activiti数据库中。

(3) 流程定义主要由BPMN 2.0标准定义,也可以使用Java代码定义它们，定义的所有术语也可用作Java类。

(4) 一旦我们开始运行流程定义，就可以称为一个流程process。

(5) processInstance是ProcessDefinition一个执行实例。

(6) 一个StartEvent与每一个业务流程有关，它表示该流程的切入点，同样，有一个EndEvent表示流程的结束。我们可以定义这些事件的条件。

(7) 开始和结束之间的所有步骤（或元素）称为任务，任务可以是各种类型的。最常用的任务是UserTasks和ServiceTasks。

顾名思义，UserTasks需要由用户手动执行，另一方面，ServiceTasks配置了一段代码，每当执行到达这段代码时，代码将被执行。

SequenceFlows用来连接任务，我们可以通过它将源元素和目标元素连接起来定义一个SequenceFlows；同样，我们还可以在SequenceFlow上定义条件，这样能在流程中创建条件路径。

有了流程定义，我们可以使用Activiti提供的服务功能进行流程运行。

#### Activiti提供的服务

RepositoryService帮助我们实现流程定义的部署。此服务会处理与流程定义相关的静态数据。
RuntimeService管理 ProcessInstances（当前正在运行的流程）以及流程变量
TaskService会跟踪 UserTasks，需要由用户手动执行的任务是Activiti API的核心。我们可以使用此服务创建任务，声明并完成任务，分配任务的受让人等。
FormService是一项可选服务，它用于定义中开始表单和任务表单。
IdentityService管理用户和组。
HistoryService会跟踪Activiti Engine的历史记录。我们还可以设置不同的历史级别。
ManagementService与元数据相关，在创建应用程序时通常不需要。
DynamicBpmnService帮助我们在不重新部署的情况下更改流程中的任何内容。

#### activiti bpmn流程设计器activiti-bpmn-tool

#### 介绍

activiti 流程图 设计工具 基于bpmnjs 的vue版本 可实现流程图设计 属性配置 下载bpmn设计图,svg图片,打开本地bpmn图并编辑

```
https://gitee.com/SweetLei/activiti-bpmn-tool
```

#### 参考地址

```
https://blog.csdn.net/sihai12345/article/details/128349463
```

#### 查询任务代办列表报错

I would like to get the active Tasks List , using:

```java
return processEngine.getTaskService().createTaskQuery().active().list();
```

but I have this error:

```java
10:05:37.238 [http-nio-1061-exec-18] ERROR e.e.e.o.k.f.c.s.ControllerAdvice - Could not write JSON: lazy loading outside command context; nested exception is com.fasterxml.jackson.databind.JsonMappingException: lazy loading outside command context (through reference chain: java.util.ArrayList[0]->org.activiti.engine.impl.persistence.entity.TaskEntityImpl["variableInstances"])
```

解决方法:

```java
public List<Map<String, Object>> getTaskList() {
List<Tasks> taskList = gprocessEngine.getTaskService().createTaskQuery().active().list();

    List<Map<String, Object>> customTaskList = new ArrayList<>();
        for (Task task : taskList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("taskId", task.getId());
            map.put("taskDefinitionKey", task.getTaskDefinitionKey());
            map.put("taskName", task.getName());
    
            customTaskList.add(map);
        }
        return customTaskList;
```

平台功能

| 服务             | 进度 | 备注                       |
| :--------------- | ---- | -------------------------- |
| 创建流程实例     | ✅    | 通过bpmn流程图创建流程实例 |
| 开启流程实例     | ✅    | 通过上面流程实例开启流程   |
| 删除流程定义     | ✅    | 删除流程定义               |
| 查询流程定义     | ✅    | 查询所有流程定义           |
| 查询流程详情     | ✅    | 查询某一个流程定义详情     |
|                  |      |                            |
| 查询我代办任务   | ✅    | 查询所有我代办的任务       |
| 查询已完成任务   | ✅    | 查询由我完成的任务         |
| 查询我发起的任务 | ✅    | 查询由我发起的任务         |
| 点击评审         | ✅    | 点击通过审批流程           |
| 添加评论         | ✅    | 在审批流程中添加评论       |
| 上传附件         | ✅    | 上传审批流程中的附件       |
| 驳回审批         |      |                            |
| 获取时间线       |      |                            |
