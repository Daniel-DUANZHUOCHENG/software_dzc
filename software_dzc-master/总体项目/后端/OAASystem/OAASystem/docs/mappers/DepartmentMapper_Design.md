## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义部门数据访问层的接口，提供对`departments`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现部门数据的持久化管理，包括根据租户 ID、路径前缀、部门名称和状态进行查询，以及部门的新增、更新和删除。

```mermaid
graph TD
    DepartmentService["DepartmentService"] -- "calls" --> DepartmentMapper
    DepartmentMapper -- "interacts with" --> Database["数据库"]
    DepartmentMapper -- "uses" --> Department (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface DepartmentMapper {
        +void insertDepartment2(Department department)
        +List<Department> findAll()
        +Department getDepartmentById(int departmentId)
        +List<Department> getDepartmentsByTenantId(int tenantId)
        +List<Department> getAllByPrefix(String pathPrefix)
        +List<Department> getAllByPrefixAndName(String path, String departmentName, String status)
        +Department reget(Integer parentDepartment, String departmentName)
        +int deleteDepartment(int departmentId)
        +void insertDepartment(String departmentName, String status, LocalDateTime createdAt, Integer parentDepartment, String manager, String managerPhone, String managerEmail, String path, Integer tenantId)
        +void updateDepartment(Integer id, String departmentName, String status, LocalDateTime createdAt, Integer parentDepartment, String manager, String managerPhone, String managerEmail, String path, Integer tenantId)
        +void delete(String path)
        +void deleteByTenantId(Integer tenantId)
    }
    DepartmentMapper ..|> Department
```

<**类图详细说明模板（类或接口说明**>

|                          |                                                                                                                                                                                              |        |                     |      |             |     |                                          |
| ------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------------------------- |
| 类名                     | DepartmentMapper                                                                                                                                                                             | 所属包 | edu.neu.oaas.mapper |      |             |     |                                          |
| 继承                     | 无                                                                                                                                                                                           |        |                     |      |             |     |                                          |
| 实现                     | 无                                                                                                                                                                                           |        |                     |      |             |     |                                          |
| 属性                     |                                                                                                                                                                                              |        |                     |      |             |     |                                          |
| 名称                     | 类型                                                                                                                                                                                         | 默认值 |                     |      | Pub/Prv/Pro |     |                                          |
| 无                       |                                                                                                                                                                                              |        |                     |      |             |     |                                          |
| 方法                     |                                                                                                                                                                                              |        |                     |      |             |     |                                          |
| 名称                     | 参数                                                                                                                                                                                         |        | 返回值              | 异常 |             |     | 描述                                     |
| insertDepartment2        | Department department                                                                                                                                                                        |        | void                | 无   |             |     | 插入新部门（用于特定场景）。             |
| findAll                  | 无                                                                                                                                                                                           |        | List<Department>    | 无   |             |     | 查找所有部门。                           |
| getDepartmentById        | int departmentId                                                                                                                                                                             |        | Department          | 无   |             |     | 根据部门 ID 获取部门信息。               |
| getDepartmentsByTenantId | int tenantId                                                                                                                                                                                 |        | List<Department>    | 无   |             |     | 根据租户 ID 获取部门列表。               |
| getAllByPrefix           | String pathPrefix                                                                                                                                                                            |        | List<Department>    | 无   |             |     | 根据路径前缀获取所有部门（限定范围内）。 |
| getAllByPrefixAndName    | String path, String departmentName, String status                                                                                                                                            |        | List<Department>    | 无   |             |     | 根据路径前缀、部门名称和状态搜索部门。   |
| reget                    | Integer parentDepartment, String departmentName                                                                                                                                              |        | Department          | 无   |             |     | 根据父部门 ID 和部门名称重新获取部门。   |
| deleteDepartment         | int departmentId                                                                                                                                                                             |        | int                 | 无   |             |     | 根据部门 ID 删除部门。                   |
| insertDepartment         | String departmentName, String status, LocalDateTime createdAt, Integer parentDepartment, String manager, String managerPhone, String managerEmail, String path, Integer tenantId             |        | void                | 无   |             |     | 插入新部门。                             |
| updateDepartment         | Integer id, String departmentName, String status, LocalDateTime createdAt, Integer parentDepartment, String manager, String managerPhone, String managerEmail, String path, Integer tenantId |        | void                | 无   |             |     | 更新部门信息。                           |
| delete                   | String path                                                                                                                                                                                  |        | void                | 无   |             |     | 根据路径删除部门及其子部门。             |
| deleteByTenantId         | Integer tenantId                                                                                                                                                                             |        | void                | 无   |             |     | 根据租户 ID 删除所有相关部门。           |
| 事件                     |                                                                                                                                                                                              |        |                     |      |             |     |                                          |
| 名称                     | 条件                                                                                                                                                                                         |        | 参数                | 目的 |             |     |                                          |
| 无                       |                                                                                                                                                                                              |        |                     |      |             |     |                                          |

</rewritten_file>
