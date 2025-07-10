## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义租户数据访问层的接口，提供对`tenants`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现租户数据的持久化管理，包括租户的新增、查询（按 ID、名称、多条件）、更新和删除。

```mermaid
graph TD
    TenantService["TenantService"] -- "calls" --> TenantMapper
    TenantMapper -- "interacts with" --> Database["数据库"]
    TenantMapper -- "uses" --> Tenant (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface TenantMapper {
        +void insertTenant2(Tenant tenant)
        +void insertTenant(Tenant tenant)
        +List<Tenant> findAll()
        +Tenant getTenantByName(String tenantName)
        +List<Tenant> getAllTenants()
        +List<Tenant> getAll()
        +Tenant getById(Integer id)
        +List<Tenant> getByName(String tenantName)
        +void updateTenant2(String adminUsername, String password, String contactPerson, String phone, String tenantName, LocalDateTime createdAt, String icon, String remark, Integer rootDepartmentId, Integer id)
        +void updateTenant(Tenant tenant)
        +void deleteById(Integer id)
        +List<Tenant> searchTenants(String tenantName, String contactPerson, String phone, LocalDate startDate, LocalDate endDate)
        +int getMaxTenantId()
        +List<Tenant> getAllTenantNames()
        +void updateTenant3(Tenant tenant)
        +Tenant reget(Tenant tenant)
    }
    TenantMapper ..|> Tenant
```

<**类图详细说明模板（类或接口说明**>

|                   |                                                                                                                                                                                         |        |                     |      |             |     |                                    |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------------------- |
| 类名              | TenantMapper                                                                                                                                                                            | 所属包 | edu.neu.oaas.mapper |      |             |     |                                    |
| 继承              | 无                                                                                                                                                                                      |        |                     |      |             |     |                                    |
| 实现              | 无                                                                                                                                                                                      |        |                     |      |             |     |                                    |
| 属性              |                                                                                                                                                                                         |        |                     |      |             |     |                                    |
| 名称              | 类型                                                                                                                                                                                    | 默认值 |                     |      | Pub/Prv/Pro |     |                                    |
| 无                |                                                                                                                                                                                         |        |                     |      |             |     |                                    |
| 方法              |                                                                                                                                                                                         |        |                     |      |             |     |                                    |
| 名称              | 参数                                                                                                                                                                                    |        | 返回值              | 异常 |             |     | 描述                               |
| insertTenant2     | Tenant tenant                                                                                                                                                                           |        | void                | 无   |             |     | 插入租户（带 rootDepartmentId）。  |
| insertTenant      | Tenant tenant                                                                                                                                                                           |        | void                | 无   |             |     | 插入租户（通用）。                 |
| findAll           | 无                                                                                                                                                                                      |        | List<Tenant>        | 无   |             |     | 查找所有租户。                     |
| getTenantByName   | String tenantName                                                                                                                                                                       |        | Tenant              | 无   |             |     | 根据租户名称获取租户。             |
| getAllTenants     | 无                                                                                                                                                                                      |        | List<Tenant>        | 无   |             |     | 获取所有租户（旧方法）。           |
| getAll            | 无                                                                                                                                                                                      |        | List<Tenant>        | 无   |             |     | 获取所有租户（通用方法）。         |
| getById           | Integer id                                                                                                                                                                              |        | Tenant              | 无   |             |     | 根据 ID 获取租户。                 |
| getByName         | String tenantName                                                                                                                                                                       |        | List<Tenant>        | 无   |             |     | 根据租户名称模糊查询租户。         |
| updateTenant2     | String adminUsername, String password, String contactPerson, String phone, String tenantName, LocalDateTime createdAt, String icon, String remark, Integer rootDepartmentId, Integer id |        | void                | 无   |             |     | 更新租户信息（参数列表形式）。     |
| updateTenant      | Tenant tenant                                                                                                                                                                           |        | void                | 无   |             |     | 更新租户信息（对象形式，旧方法）。 |
| deleteById        | Integer id                                                                                                                                                                              |        | void                | 无   |             |     | 根据 ID 删除租户。                 |
| searchTenants     | String tenantName, String contactPerson, String phone, LocalDate startDate, LocalDate endDate                                                                                           |        | List<Tenant>        | 无   |             |     | 根据多个条件搜索租户。             |
| getMaxTenantId    | 无                                                                                                                                                                                      |        | int                 | 无   |             |     | 获取最大的租户 ID。                |
| getAllTenantNames | 无                                                                                                                                                                                      |        | List<Tenant>        | 无   |             |     | 获取所有租户的名称列表。           |
| updateTenant3     | Tenant tenant                                                                                                                                                                           |        | void                | 无   |             |     | 更新租户信息（对象形式，新方法）。 |
| reget             | Tenant tenant                                                                                                                                                                           |        | Tenant              | 无   |             |     | 根据租户名称和联系人重新获取租户。 |
| 事件              |                                                                                                                                                                                         |        |                     |      |             |     |                                    |
| 名称              | 条件                                                                                                                                                                                    |        | 参数                | 目的 |             |     |                                    |
| 无                |                                                                                                                                                                                         |        |                     |      |             |     |                                    |

</rewritten_file>
