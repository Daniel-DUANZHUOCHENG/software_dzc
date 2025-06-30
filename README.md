# OAASystem - 办公自动化系统

本项目是一个基于前后端分离架构的办公自动化（OA）系统。前端采用 Vue.js 和 Element Plus 构建，后端采用 Java Spring Boot 和 MyBatis 实现。

## ✨ 功能特性

- **用户管理**：支持用户的注册、登录、信息修改和管理。
- **租户管理**：支持多租户模式，不同租户拥有独立的数据空间。
- **组织架构**：支持部门和职位的管理。
- **会议管理**：发布会议通知，管理参会回执。
- **课程管理**：发布和管理内部培训课程。
- **信息发布**：发布公司新闻、公告等信息。
- **轮播图管理**：首页轮播图的动态配置。
- **用户行为监控**：记录和分析用户操作日志。

## 🛠️ 技术栈

- **前端**

  - [Vue.js](https://vuejs.org/)
  - [Vite](https://vitejs.dev/)
  - [Element Plus](https://element-plus.org/)
  - [Vue Router](https://router.vuejs.org/)
  - [ECharts](https://echarts.apache.org/)
  - [Axios](https://axios-http.com/)

- **后端**
  - [Spring Boot](https://spring.io/projects/spring-boot)
  - [MyBatis](https://mybatis.org/mybatis-3/)
  - [MySQL](https://www.mysql.com/)
  - [Maven](https://maven.apache.org/)

## 📁 项目结构

```
.
├── 总体项目/
│   ├── web前端/      # 前端 Vue.js 项目
│   └── 后端/         # 后端 Spring Boot 项目
├── 总体报告.doc
├── 视频.mp4
└── 答辩ppt.pptx
```

## 🚀 快速开始

### 环境准备

- [Git](https://git-scm.com/)
- [JDK](https://www.oracle.com/java/technologies/downloads/) (1.8 或更高版本)
- [Maven](https://maven.apache.org/download.cgi)
- [Node.js](https://nodejs.org/) (14.x 或更高版本)
- [MySQL](https://www.mysql.com/downloads/) (5.7 或更高版本)

### 后端启动

1.  **克隆项目**

    ```bash
    git clone <your-repository-url>
    ```

2.  **数据库设置**

    - 创建一个名为 `cmh` 的 MySQL 数据库。
    - 将项目中的 SQL 文件（如果提供）导入到该数据库中。
    - 打开 `总体项目/后端/OAASystem/OAASystem/src/main/resources/application.properties` 文件。
    - 修改 `spring.datasource.url`, `spring.datasource.username`, 和 `spring.datasource.password` 以匹配您的数据库配置。

3.  **启动应用**
    - 使用 IDE (如 IntelliJ IDEA) 打开 `总体项目/后端/OAASystem` 目录。
    - 等待 Maven 自动下载依赖。
    - 找到并运行 `OaaSystemApplication.java` 文件的主方法。
    - 服务将启动在 `http://localhost:9049`。

### 前端启动

1.  **进入前端目录**

    ```bash
    cd 总体项目/web前端
    ```

2.  **安装依赖**

    ```bash
    npm install
    ```

3.  **运行开发服务器**

    ```bash
    npm run dev
    ```

4.  **访问应用**
    - 在浏览器中打开 Vite 提供的本地访问地址（通常是 `http://localhost:5173`）。

---

感谢使用！
