## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义职位数据访问层的接口，提供对`position`数据库表的 CRUD 操作。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现职位数据的持久化管理，包括职位的插入、删除和根据部门 ID 查询。

```mermaid
graph TD
    PositionService["PositionService"] -- "calls" --> PositionMapper
    PositionMapper -- "interacts with" --> Database["数据库"]
    PositionMapper -- "uses" --> Position (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface PositionMapper {
        +void insert(Position position)
        +void delete(Position position)
        +List<Position> getAll(Integer departmentId)
    }
    PositionMapper ..|> Position
```

<**类图详细说明模板（类或接口说明**>

|        |                      |        |                     |      |             |     |                            |
| ------ | -------------------- | ------ | ------------------- | ---- | ----------- | --- | -------------------------- |
| 类名   | PositionMapper       | 所属包 | edu.neu.oaas.mapper |      |             |     |                            |
| 继承   | 无                   |        |                     |      |             |     |                            |
| 实现   | 无                   |        |                     |      |             |     |                            |
| 属性   |                      |        |                     |      |             |     |                            |
| 名称   | 类型                 | 默认值 |                     |      | Pub/Prv/Pro |     |                            |
| 无     |                      |        |                     |      |             |     |                            |
| 方法   |                      |        |                     |      |             |     |                            |
| 名称   | 参数                 |        | 返回值              | 异常 |             |     | 描述                       |
| insert | Position position    |        | void                | 无   |             |     | 插入新的职位信息。         |
| delete | Position position    |        | void                | 无   |             |     | 删除职位信息。             |
| getAll | Integer departmentId |        | List<Position>      | 无   |             |     | 根据部门 ID 获取所有职位。 |
| 事件   |                      |        |                     |      |             |     |                            |
| 名称   | 条件                 |        | 参数                | 目的 |             |     |                            |
| 无     |                      |        |                     |      |             |     |                            |

</rewritten_file>
