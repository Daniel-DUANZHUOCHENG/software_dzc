## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义用户行为数据访问层的接口，提供对`user_behavior`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现用户行为数据的持久化管理，包括用户行为的插入、全量查询、条件搜索（按行为和时间戳），以及统计在线用户数和按用户 ID 查询行为。

```mermaid
graph TD
    UserBehaviorService["UserBehaviorService"] -- "calls" --> UserBehaviorMapper
    UserBehaviorMapper -- "interacts with" --> Database["数据库"]
    UserBehaviorMapper -- "uses" --> UserBehavior (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface UserBehaviorMapper {
        +void insertUserBehavior(UserBehavior userBehavior)
        +List<UserBehavior> selectAllUserBehaviors()
        +List<UserBehavior> searchUserBehaviors(String action, String timestamp)
        +int countOnlineUsers()
        +List<UserBehavior> getUserBehaviorsByUserId(Integer userId)
    }
    UserBehaviorMapper ..|> UserBehavior
```

<**类图详细说明模板（类或接口说明**>

|                          |                                 |        |                     |      |             |     |                                                       |
| ------------------------ | ------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ----------------------------------------------------- |
| 类名                     | UserBehaviorMapper              | 所属包 | edu.neu.oaas.mapper |      |             |     |                                                       |
| 继承                     | 无                              |        |                     |      |             |     |                                                       |
| 实现                     | 无                              |        |                     |      |             |     |                                                       |
| 属性                     |                                 |        |                     |      |             |     |                                                       |
| 名称                     | 类型                            | 默认值 |                     |      | Pub/Prv/Pro |     |                                                       |
| 无                       |                                 |        |                     |      |             |     |                                                       |
| 方法                     |                                 |        |                     |      |             |     |                                                       |
| 名称                     | 参数                            |        | 返回值              | 异常 |             |     | 描述                                                  |
| insertUserBehavior       | UserBehavior userBehavior       |        | void                | 无   |             |     | 插入新的用户行为记录。                                |
| selectAllUserBehaviors   | 无                              |        | List<UserBehavior>  | 无   |             |     | 选择所有用户行为记录。                                |
| searchUserBehaviors      | String action, String timestamp |        | List<UserBehavior>  | 无   |             |     | 根据动作和时间戳搜索用户行为记录。                    |
| countOnlineUsers         | 无                              |        | int                 | 无   |             |     | 统计过去 5 分钟内的在线用户数量（根据用户 ID 去重）。 |
| getUserBehaviorsByUserId | Integer userId                  |        | List<UserBehavior>  | 无   |             |     | 根据用户 ID 获取其所有行为记录。                      |
| 事件                     |                                 |        |                     |      |             |     |                                                       |
| 名称                     | 条件                            |        | 参数                | 目的 |             |     |                                                       |
| 无                       |                                 |        |                     |      |             |     |                                                       |
