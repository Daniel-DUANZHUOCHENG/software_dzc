## 模块设计

### 模块内设计类的交互模型

本模块主要负责定义轮播图数据访问层的接口，提供对`carousel_images`数据库表的 CRUD 操作。它作为 Service 层与数据库之间的桥梁，通过 MyBatis 注解直接与 SQL 语句进行映射，实现轮播图数据的持久化管理，包括图片的插入、删除和查询（所有或按 ID）。

```mermaid
graph TD
    CarouselImageService["CarouselImageService"] -- "calls" --> CarouselImageMapper
    CarouselImageMapper -- "interacts with" --> Database["数据库"]
    CarouselImageMapper -- "uses" --> CarouselImage (Pojo)
```

### 设计类说明

_基于模块内设计类的交互模型创建的模块设计类图_

```mermaid
classDiagram
    interface CarouselImageMapper {
        +List<CarouselImage> getAllImages()
        +void insertImage(CarouselImage image)
        +void deleteImage(Long id)
        +CarouselImage getImageById(Long id)
    }
    CarouselImageMapper ..|> CarouselImage
```

<**类图详细说明模板（类或接口说明**>

|              |                     |        |                     |      |             |     |                        |
| ------------ | ------------------- | ------ | ------------------- | ---- | ----------- | --- | ---------------------- |
| 类名         | CarouselImageMapper | 所属包 | edu.neu.oaas.mapper |      |             |     |                        |
| 继承         | 无                  |        |                     |      |             |     |                        |
| 实现         | 无                  |        |                     |      |             |     |                        |
| 属性         |                     |        |                     |      |             |     |                        |
| 名称         | 类型                | 默认值 |                     |      | Pub/Prv/Pro |     |                        |
| 无           |                     |        |                     |      |             |     |                        |
| 方法         |                     |        |                     |      |             |     |                        |
| 名称         | 参数                |        | 返回值              | 异常 |             |     | 描述                   |
| getAllImages | 无                  |        | List<CarouselImage> | 无   |             |     | 获取所有轮播图片。     |
| insertImage  | CarouselImage image |        | void                | 无   |             |     | 插入新的轮播图片。     |
| deleteImage  | Long id             |        | void                | 无   |             |     | 根据 ID 删除轮播图片。 |
| getImageById | Long id             |        | CarouselImage       | 无   |             |     | 根据 ID 获取轮播图片。 |
| 事件         |                     |        |                     |      |             |     |                        |
| 名称         | 条件                |        | 参数                | 目的 |             |     |                        |
| 无           |                     |        |                     |      |             |     |                        |

</rewritten_file>
