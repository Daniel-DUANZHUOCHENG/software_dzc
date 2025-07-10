## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义课程数据访问层的接口，提供对`course`数据库表的 CRUD 操作及多种查询功能。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现课程数据的持久化管理，包括课程的全面检索、按名称和编号搜索、增删改查，以及对课程审批状态的查询和更新。

```mermaid
graph TD
    CourseService["CourseService"] -- "calls" --> CourseMapper
    CourseMapper -- "interacts with" --> Database["数据库"]
    CourseMapper -- "uses" --> Course (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface CourseMapper {
        +List<Course> selectAllCourses()
        +List<Course> searchCourses(String coursename, String number)
        +void insertCourse(Course course)
        +int updateCourse(Course course)
        +void deleteCourse(Integer courseID)
        +Course selectCourseById(Integer courseID)
        +List<Course> selectPendingCourses()
        +List<Course> selectCoursesByApprovalStatus(String approvalStatus)
        +int approveCourse(Integer courseID, String approvalStatus, String rejectionReason)
        +List<Course> selectApprovedCourses()
    }
    CourseMapper ..|> Course
```

<**类图详细说明模板（类或接口说明**>

|                               |                                                                 |        |                     |      |             |     |                              |
| ----------------------------- | --------------------------------------------------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------------- |
| 类名                          | CourseMapper                                                    | 所属包 | edu.neu.oaas.mapper |      |             |     |                              |
| 继承                          | 无                                                              |        |                     |      |             |     |                              |
| 实现                          | 无                                                              |        |                     |      |             |     |                              |
| 属性                          |                                                                 |        |                     |      |             |     |                              |
| 名称                          | 类型                                                            | 默认值 |                     |      | Pub/Prv/Pro |     |                              |
| 无                            |                                                                 |        |                     |      |             |     |                              |
| 方法                          |                                                                 |        |                     |      |             |     |                              |
| 名称                          | 参数                                                            |        | 返回值              | 异常 |             |     | 描述                         |
| selectAllCourses              | 无                                                              |        | List<Course>        | 无   |             |     | 选择所有课程。               |
| searchCourses                 | String coursename, String number                                |        | List<Course>        | 无   |             |     | 根据课程名称和编号搜索课程。 |
| insertCourse                  | Course course                                                   |        | void                | 无   |             |     | 插入新课程。                 |
| updateCourse                  | Course course                                                   |        | int                 | 无   |             |     | 更新课程信息。               |
| deleteCourse                  | Integer courseID                                                |        | void                | 无   |             |     | 根据课程 ID 删除课程。       |
| selectCourseById              | Integer courseID                                                |        | Course              | 无   |             |     | 根据课程 ID 选择课程。       |
| selectPendingCourses          | 无                                                              |        | List<Course>        | 无   |             |     | 选择所有待审批的课程。       |
| selectCoursesByApprovalStatus | String approvalStatus                                           |        | List<Course>        | 无   |             |     | 根据审批状态选择课程。       |
| approveCourse                 | Integer courseID, String approvalStatus, String rejectionReason |        | int                 | 无   |             |     | 审批课程。                   |
| selectApprovedCourses         | 无                                                              |        | List<Course>        | 无   |             |     | 选择所有已审批的课程。       |
| 事件                          |                                                                 |        |                     |      |             |     |                              |
| 名称                          | 条件                                                            |        | 参数                | 目的 |             |     |                              |
| 无                            |                                                                 |        |                     |      |             |     |                              |

</rewritten_file>
