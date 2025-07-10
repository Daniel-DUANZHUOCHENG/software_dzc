## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义会议回执数据访问层的接口，提供对`conference_receipt`数据库表的插入操作。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现会议回执数据的持久化。

```mermaid
graph TD
    ConferenceReceiptService["ConferenceReceiptService"] -->|"calls"| ConferenceReceiptMapper
    ConferenceReceiptMapper -->|"interacts with"| Database["数据库"]
    ConferenceReceiptMapper -->|"uses"| ConferenceReceipt (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface ConferenceReceiptMapper {
        +void insertConferenceReceipt(ConferenceReceipt conferenceReceipt)
    }
    ConferenceReceiptMapper ..|> ConferenceReceipt
```

<**类图详细说明模板（类或接口说明**>

|                         |                                     |        |                     |      |             |     |                        |
| ----------------------- | ----------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------- |
| 类名                    | ConferenceReceiptMapper             | 所属包 | edu.neu.oaas.mapper |      |             |     |                        |
| 继承                    | 无                                  |        |                     |      |             |     |                        |
| 实现                    | 无                                  |        |                     |      |             |     |                        |
| 属性                    |                                     |        |                     |      |             |     |                        |
| 名称                    | 类型                                | 默认值 |                     |      | Pub/Prv/Pro |     |                        |
| 无                      |                                     |        |                     |      |             |     |                        |
| 方法                    |                                     |        |                     |      |             |     |                        |
| 名称                    | 参数                                |        | 返回值              | 异常 |             |     | 描述                   |
| insertConferenceReceipt | ConferenceReceipt conferenceReceipt |        | void                | 无   |             |     | 插入新的会议回执信息。 |
| 事件                    |                                     |        |                     |      |             |     |                        |
| 名称                    | 条件                                |        | 参数                | 目的 |             |     |                        |
| 无                      |                                     |        |                     |      |             |     |                        |

</rewritten_file>
