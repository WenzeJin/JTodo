# JTodo: Todo list based on Java swing by wenze Jin

> project for course "Software Engineering" of NJU CS

JTodo 是一个利用 Java Swing 作为 UI 框架开发的建议待办事项应用，主要实现待办事项的增删改查和提醒，远期实现和主流日历 API 的交互。

## 如何运行

### 环境准备

1. JDK version >= 19
2. IntelliJ IDEA

### 运行

1. 克隆本仓库；
2. 使用 IntelliJ IDEA 打开项目文件夹；
3. 进入`lib`目录，右键点击`org.json-1.6-20240205.jar`将其添加至项目库（本项目依赖`org.json`）；
4. 找到`Main`并运行，如果你已经在 IDEA 中打开本文件，可在此行左侧点击运行按钮；
5. 第一次运行后，将自动在项目文件夹下生成`settings.json`配置文件，用于进行简单的设置，包括本地持久化存储路径等；
6. 一旦对待办事项作任何改动，将触发自动保存（如果你没有修改默认设置），默认路径为`./tasks-saving.data`，如果文件不存在将创建，你也可以在配置文件中自定义路径。

远期将会打包为 JAR 一键运行

## 代码规范

本项目的代码以 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) 作为指导，使用大模型辅助规范代码。