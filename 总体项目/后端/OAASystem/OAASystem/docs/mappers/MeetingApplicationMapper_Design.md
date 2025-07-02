## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义会议申请数据访问层的接口，提供对`meeting_application`数据库表的 CRUD 操作及复杂查询。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现数据的持久化管理。

```mermaid
graph TD
    MeetingApplicationService -- calls --> MeetingApplicationMapper
    MeetingApplicationMapper -- interacts with --> Database
    MeetingApplicationMapper -- uses --> MeetingApplication (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface MeetingApplicationMapper {
        +int insertMeetingApplication(MeetingApplication application)
        +int checkExistingApplication(Integer meetingId, Integer applicantId)
        +List<MeetingApplication> getMeetingApplications(Integer meetingId)
        +List<MeetingApplication> getMeetingApplicationsByTenant(Integer meetingId, Integer tenantId)
        +MeetingApplication getMeetingApplicationById(Integer id)
        +int updateMeetingApplication(MeetingApplication application)
        +List<MeetingApplication> getUserApplications(Integer applicantId)
        +List<MeetingApplication> getTenantPendingApplications(Integer tenantId)
        +List<MeetingApplication> getTenantAllApplications(Integer tenantId)
        +int deleteMeetingApplication(Integer id)
        +int getTenantApplicationCountByStatus(Integer tenantId, String status)
        +int getApplicationCountByTimeRange(String startTime, String endTime)
        +List<MeetingApplication> getRecentApplications(Integer tenantId, int limit)
        +List<MeetingApplication> getApplicationsByApplicantTenant(Integer tenantId)
        +int batchUpdateApplicationStatus(List<Integer> ids, String status, Integer approverId, String approverName)
        +List<MeetingApplication> getMeetingApplicationsByStatus(String status)
        +List<MeetingApplication> getAllMeetingApplications()
    }
    MeetingApplicationMapper ..|> MeetingApplication
```

<**类图详细说明模板（类或接口说明**>

|                                   |                                                                           |        |                          |      |             |     |                                                        |
| --------------------------------- | ------------------------------------------------------------------------- | ------ | ------------------------ | ---- | ----------- | --- | ------------------------------------------------------ |
| 类名                              | MeetingApplicationMapper                                                  | 所属包 | edu.neu.oaas.mapper      |      |             |     |                                                        |
| 继承                              | 无                                                                        |        |                          |      |             |     |                                                        |
| 实现                              | 无                                                                        |        |                          |      |             |     |                                                        |
| 属性                              |                                                                           |        |                          |      |             |     |                                                        |
| 名称                              | 类型                                                                      | 默认值 |                          |      | Pub/Prv/Pro |     |                                                        |
| 无                                |                                                                           |        |                          |      |             |     |                                                        |
| 方法                              |                                                                           |        |                          |      |             |     |                                                        |
| 名称                              | 参数                                                                      |        | 返回值                   | 异常 |             |     | 描述                                                   |
| insertMeetingApplication          | MeetingApplication application                                            |        | int                      | 无   |             |     | 插入新的会议申请。                                     |
| checkExistingApplication          | Integer meetingId, Integer applicantId                                    |        | int                      | 无   |             |     | 检查指定会议和申请人是否已存在未拒绝的申请。           |
| getMeetingApplications            | Integer meetingId                                                         |        | List<MeetingApplication> | 无   |             |     | 根据会议 ID 获取所有会议申请。                         |
| getMeetingApplicationsByTenant    | Integer meetingId, Integer tenantId                                       |        | List<MeetingApplication> | 无   |             |     | 根据会议 ID 和租户 ID 获取会议申请列表（租户管理员）。 |
| getMeetingApplicationById         | Integer id                                                                |        | MeetingApplication       | 无   |             |     | 根据 ID 获取会议申请详情。                             |
| updateMeetingApplication          | MeetingApplication application                                            |        | int                      | 无   |             |     | 更新会议申请的状态和审批信息。                         |
| getUserApplications               | Integer applicantId                                                       |        | List<MeetingApplication> | 无   |             |     | 获取指定申请人的所有会议申请。                         |
| getTenantPendingApplications      | Integer tenantId                                                          |        | List<MeetingApplication> | 无   |             |     | 获取指定租户的所有待审批会议申请。                     |
| getTenantAllApplications          | Integer tenantId                                                          |        | List<MeetingApplication> | 无   |             |     | 获取指定租户的所有会议申请。                           |
| deleteMeetingApplication          | Integer id                                                                |        | int                      | 无   |             |     | 根据 ID 删除会议申请。                                 |
| getTenantApplicationCountByStatus | Integer tenantId, String status                                           |        | int                      | 无   |             |     | 根据租户 ID 和状态获取会议申请数量。                   |
| getApplicationCountByTimeRange    | String startTime, String endTime                                          |        | int                      | 无   |             |     | 获取指定时间段内的会议申请数量。                       |
| getRecentApplications             | Integer tenantId, int limit                                               |        | List<MeetingApplication> | 无   |             |     | 获取指定租户最近的会议申请列表（用于仪表盘）。         |
| getApplicationsByApplicantTenant  | Integer tenantId                                                          |        | List<MeetingApplication> | 无   |             |     | 根据申请人的租户 ID 获取会议申请列表。                 |
| batchUpdateApplicationStatus      | List<Integer> ids, String status, Integer approverId, String approverName |        | int                      | 无   |             |     | 批量更新会议申请状态。                                 |
| getMeetingApplicationsByStatus    | String status                                                             |        | List<MeetingApplication> | 无   |             |     | 根据状态获取会议申请列表。                             |
| getAllMeetingApplications         | 无                                                                        |        | List<MeetingApplication> | 无   |             |     | 获取所有会议申请（系统管理员）。                       |
| 事件                              |                                                                           |        |                          |      |             |     |                                                        |
| 名称                              | 条件                                                                      |        | 参数                     | 目的 |             |     |                                                        |
| 无                                |                                                                           |        |                          |      |             |     |                                                        |

</rewritten_file>
