## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义会议数据访问层的接口，提供对`conference`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现会议数据的持久化管理，包括会议的全面检索、按名称、创建者和开始时间搜索、增删改查，以及对会议审批状态的查询和更新。

```mermaid
graph TD
    ConferenceService["ConferenceService"] -->|"calls"| ConferenceMapper
    ConferenceMapper -->|"interacts with"| Database["数据库"]
    ConferenceMapper -->|"uses"| Conference (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface ConferenceMapper {
        +List<Conference> getAllConferences()
        +void insertConference(Conference conference)
        +void deleteConference(Integer conferenceID)
        +int updateConference(Conference conference)
        +List<Conference> searchConferences(String conferencename, String creator, String starttime)
        +Conference getConferenceById(Integer conferenceID)
        +List<Conference> selectPendingConferences()
        +List<Conference> selectConferencesByApprovalStatus(String approvalStatus)
        +int approveConference(Integer conferenceID, String approvalStatus, String rejectionReason)
        +List<Conference> selectApprovedConferences()
    }
    ConferenceMapper ..|> Conference
```

<**类图详细说明模板（类或接口说明**>

|                                   |                                                                     |        |                     |      |             |     |                                          |
| --------------------------------- | ------------------------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------------------------- |
| 类名                              | ConferenceMapper                                                    | 所属包 | edu.neu.oaas.mapper |      |             |     |                                          |
| 继承                              | 无                                                                  |        |                     |      |             |     |                                          |
| 实现                              | 无                                                                  |        |                     |      |             |     |                                          |
| 属性                              |                                                                     |        |                     |      |             |     |                                          |
| 名称                              | 类型                                                                | 默认值 |                     |      | Pub/Prv/Pro |     |                                          |
| 无                                |                                                                     |        |                     |      |             |     |                                          |
| 方法                              |                                                                     |        |                     |      |             |     |                                          |
| 名称                              | 参数                                                                |        | 返回值              | 异常 |             |     | 描述                                     |
| getAllConferences                 | 无                                                                  |        | List<Conference>    | 无   |             |     | 获取所有会议列表。                       |
| insertConference                  | Conference conference                                               |        | void                | 无   |             |     | 插入新会议。                             |
| deleteConference                  | Integer conferenceID                                                |        | void                | 无   |             |     | 删除指定会议。                           |
| updateConference                  | Conference conference                                               |        | int                 | 无   |             |     | 更新会议信息。                           |
| searchConferences                 | String conferencename, String creator, String starttime             |        | List<Conference>    | 无   |             |     | 根据会议名称、创建者和开始时间搜索会议。 |
| getConferenceById                 | Integer conferenceID                                                |        | Conference          | 无   |             |     | 根据会议 ID 获取会议详情。               |
| selectPendingConferences          | 无                                                                  |        | List<Conference>    | 无   |             |     | 获取所有待审批的会议列表。               |
| selectConferencesByApprovalStatus | String approvalStatus                                               |        | List<Conference>    | 无   |             |     | 根据审批状态获取会议列表。               |
| approveConference                 | Integer conferenceID, String approvalStatus, String rejectionReason |        | int                 | 无   |             |     | 审批会议（批准或拒绝）。                 |
| selectApprovedConferences         | 无                                                                  |        | List<Conference>    | 无   |             |     | 获取所有已审批的会议列表。               |
| 事件                              |                                                                     |        |                     |      |             |     |                                          |
| 名称                              | 条件                                                                |        | 参数                | 目的 |             |     |                                          |
| 无                                |                                                                     |        |                     |      |             |     |                                          |

</rewritten_file>
