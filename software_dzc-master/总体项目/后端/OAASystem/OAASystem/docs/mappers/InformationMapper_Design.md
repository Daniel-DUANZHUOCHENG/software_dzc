## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义资讯数据访问层的接口，提供对`information`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现资讯数据的持久化管理，包括根据路径前缀、标题、作者和租户 ID 进行查询，以及资讯的新增、更新、删除和审批状态管理。

```mermaid
graph TD
    InformationService["InformationService"] -- "calls" --> InformationMapper
    InformationMapper -- "interacts with" --> Database["数据库"]
    InformationMapper -- "uses" --> Information (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface InformationMapper {
        +List<Information> findByPathPrefix(String pathPrefix)
        +List<Information> searchByTitleAndAuthor(String pathPrefix, String title, String author)
        +void insertInformation(Information information)
        +int updateInformation(Information information)
        +void deleteInformation(int id)
        +Information getInformationById(int id)
        +List<Information> getInformationByTenantId(int tenantId)
        +void deleteByPathPrefix(String pathPrefix)
        +List<Information> selectPendingInformation()
        +List<Information> selectInformationByApprovalStatus(String approvalStatus)
        +int approveInformation(int id, String approvalStatus, String rejectionReason)
        +List<Information> selectApprovedInformation()
        +List<Information> getAllInformation()
    }
    InformationMapper ..|> Information
```

<**类图详细说明模板（类或接口说明**>

|                                   |                                                       |        |                     |      |             |     |                                    |
| --------------------------------- | ----------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------------------- |
| 类名                              | InformationMapper                                     | 所属包 | edu.neu.oaas.mapper |      |             |     |                                    |
| 继承                              | 无                                                    |        |                     |      |             |     |                                    |
| 实现                              | 无                                                    |        |                     |      |             |     |                                    |
| 属性                              |                                                       |        |                     |      |             |     |                                    |
| 名称                              | 类型                                                  | 默认值 |                     |      | Pub/Prv/Pro |     |                                    |
| 无                                |                                                       |        |                     |      |             |     |                                    |
| 方法                              |                                                       |        |                     |      |             |     |                                    |
| 名称                              | 参数                                                  |        | 返回值              | 异常 |             |     | 描述                               |
| findByPathPrefix                  | String pathPrefix                                     |        | List<Information>   | 无   |             |     | 根据路径前缀查找资讯列表。         |
| searchByTitleAndAuthor            | String pathPrefix, String title, String author        |        | List<Information>   | 无   |             |     | 根据标题、作者和路径前缀搜索资讯。 |
| insertInformation                 | Information information                               |        | void                | 无   |             |     | 插入新的资讯。                     |
| updateInformation                 | Information information                               |        | int                 | 无   |             |     | 更新资讯信息。                     |
| deleteInformation                 | int id                                                |        | void                | 无   |             |     | 根据 ID 删除资讯。                 |
| getInformationById                | int id                                                |        | Information         | 无   |             |     | 根据 ID 获取资讯详情。             |
| getInformationByTenantId          | int tenantId                                          |        | List<Information>   | 无   |             |     | 根据租户 ID 获取资讯列表。         |
| deleteByPathPrefix                | String pathPrefix                                     |        | void                | 无   |             |     | 根据路径前缀删除资讯。             |
| selectPendingInformation          | 无                                                    |        | List<Information>   | 无   |             |     | 选择所有待审批的资讯。             |
| selectInformationByApprovalStatus | String approvalStatus                                 |        | List<Information>   | 无   |             |     | 根据审批状态选择资讯。             |
| approveInformation                | int id, String approvalStatus, String rejectionReason |        | int                 | 无   |             |     | 审批资讯。                         |
| selectApprovedInformation         | 无                                                    |        | List<Information>   | 无   |             |     | 选择所有已审批的资讯。             |
| getAllInformation                 | 无                                                    |        | List<Information>   | 无   |             |     | 获取所有资讯。                     |
| 事件                              |                                                       |        |                     |      |             |     |                                    |
| 名称                              | 条件                                                  |        | 参数                | 目的 |             |     |                                    |
| 无                                |                                                       |        |                     |      |             |     |                                    |

</rewritten_file>
