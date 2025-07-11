## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义用户数据访问层的接口，提供对`users`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现用户数据的持久化管理，包括用户的增删改查、根据多种条件（如 ID、租户 ID、用户名、部门 ID、路径前缀）查询，以及分页和导入功能。

```mermaid
graph TD
    UserService["UserService"] -- "calls" --> UserMapper
    UserMapper -- "interacts with" --> Database["数据库"]
    UserMapper -- "uses" --> User (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface UserMapper {
        +void insertUser2(User user)
        +void insertUser(User user)
        +User getUserById(int Id)
        +List<User> getUsersByTenantId(int tenantId)
        +User getUserByUsername(String username)
        +User getUserByEmail(String email)
        +User getUserByName(String username)
        +List<User> getAllUser()
        +List<User> getUserByDepartmentId(Integer departmentId)
        +void deleteByTenantId(Integer tenantId)
        +List<User> findByPathPrefix(String pathPrefix)
        +int updateUser2(User user)
        +void deleteById(Integer id)
        +void deleteByPath(String pathPrefix)
        +void updateUser(Integer id, String username, String password, String nickname, String phoneNumber, String email, String gender, Integer departmentId, String status, String role, LocalDateTime createdAt, String position, String remark, String avatar, Integer tenantId, String path)
        +List<User> getUsersByPage(int offset, int pageSize)
        +int getTotalUserCount()
        +List<User> searchUsers(String username, String phoneNumber, String status, LocalDate startDate, LocalDate endDate)
    }
    UserMapper ..|> User
```

<**类图详细说明模板（类或接口说明**>

|                       |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |
| --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | -------------------------------------------------- |
| 类名                  | UserMapper                                                                                                                                                                                                                                                              | 所属包 | edu.neu.oaas.mapper |      |             |     |                                                    |
| 继承                  | 无                                                                                                                                                                                                                                                                      |        |                     |      |             |     |                                                    |
| 实现                  | 无                                                                                                                                                                                                                                                                      |        |                     |      |             |     |                                                    |
| 属性                  |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |
| 名称                  | 类型                                                                                                                                                                                                                                                                    | 默认值 |                     |      | Pub/Prv/Pro |     |                                                    |
| 无                    |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |
| 方法                  |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |
| 名称                  | 参数                                                                                                                                                                                                                                                                    |        | 返回值              | 异常 |             |     | 描述                                               |
| insertUser2           | User user                                                                                                                                                                                                                                                               |        | void                | 无   |             |     | 插入新用户（不带路径）。                           |
| insertUser            | User user                                                                                                                                                                                                                                                               |        | void                | 无   |             |     | 插入新用户（带路径）。                             |
| getUserById           | int Id                                                                                                                                                                                                                                                                  |        | User                | 无   |             |     | 根据 ID 获取用户。                                 |
| getUsersByTenantId    | int tenantId                                                                                                                                                                                                                                                            |        | List<User>          | 无   |             |     | 根据租户 ID 获取用户列表。                         |
| getUserByUsername     | String username                                                                                                                                                                                                                                                         |        | User                | 无   |             |     | 根据用户名获取用户。                               |
| getUserByEmail        | String email                                                                                                                                                                                                                                                            |        | User                | 无   |             |     | 根据电子邮件获取用户。                             |
| getUserByName         | String username                                                                                                                                                                                                                                                         |        | User                | 无   |             |     | 根据用户名获取用户（同 getUserByUsername）。       |
| getAllUser            | 无                                                                                                                                                                                                                                                                      |        | List<User>          | 无   |             |     | 获取所有用户。                                     |
| getUserByDepartmentId | Integer departmentId                                                                                                                                                                                                                                                    |        | List<User>          | 无   |             |     | 根据部门 ID 获取用户列表。                         |
| deleteByTenantId      | Integer tenantId                                                                                                                                                                                                                                                        |        | void                | 无   |             |     | 根据租户 ID 删除所有用户。                         |
| findByPathPrefix      | String pathPrefix                                                                                                                                                                                                                                                       |        | List<User>          | 无   |             |     | 根据路径前缀查找用户。                             |
| updateUser2           | User user                                                                                                                                                                                                                                                               |        | int                 | 无   |             |     | 更新用户信息（通过 User 对象）。                   |
| deleteById            | Integer id                                                                                                                                                                                                                                                              |        | void                | 无   |             |     | 根据 ID 删除用户。                                 |
| deleteByPath          | String pathPrefix                                                                                                                                                                                                                                                       |        | void                | 无   |             |     | 根据路径前缀删除用户。                             |
| updateUser            | Integer id, String username, String password, String nickname, String phoneNumber, String email, String gender, Integer departmentId, String status, String role, LocalDateTime createdAt, String position, String remark, String avatar, Integer tenantId, String path |        | void                | 无   |             |     | 更新用户信息（通过参数列表）。                     |
| getUsersByPage        | int offset, int pageSize                                                                                                                                                                                                                                                |        | List<User>          | 无   |             |     | 分页获取用户列表。                                 |
| getTotalUserCount     | 无                                                                                                                                                                                                                                                                      |        | int                 | 无   |             |     | 获取总用户数。                                     |
| searchUsers           | String username, String phoneNumber, String status, LocalDate startDate, LocalDate endDate                                                                                                                                                                              |        | List<User>          | 无   |             |     | 根据用户名、电话号码、状态和创建日期范围搜索用户。 |
| 事件                  |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |
| 名称                  | 条件                                                                                                                                                                                                                                                                    |        | 参数                | 目的 |             |     |                                                    |
| 无                    |                                                                                                                                                                                                                                                                         |        |                     |      |             |     |                                                    |

</rewritten_file>
