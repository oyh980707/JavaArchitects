# Spring framework 源码分析

## Spring 的发展过程

### Spring 的设计初衷

 可以追溯到一个最根本的使命：简化开发

Spring 立志于全方面的简化Java开发，对此，她主要采取了4个关键策略

```text
- 基于POJO的轻量级和最小侵入性编程；
- 通过依赖注入和面向接口松耦合；
- 基于切面和惯性进行声明式编程；
- 通过切面和模板减少样板式代码；

而他主要是通过：面向Bean(BOP)、依赖注入（DI）以及面向切面（AOP）这三种方式来达成的。
```

### Spring5 系统架构

所有的模块集合：核心容器(Core Container)，AOP(Aspect Oriented Programming)，设备支持(Intrument)，数据访问及集成(Data Access/Integration)，Web，报文发送(Messaging)，Test

spring的核心模块

| 模块名称               | 主要功能                                                     |
| ---------------------- | ------------------------------------------------------------ |
| Spring之核心模块       |                                                              |
| spring-core            | 依赖注入IOC与DI的最基本的实现                                |
| spring-beans           | Bean工厂与Bean的装配                                         |
| spring-context         | 定义基础的Spring的Context上下文即IOC容器                     |
| spring-context-support | 对Spring IOC容器的扩展支持，以及IOC子容器                    |
| spring-context-indexer | Spring的类管理组件和Classpath扫描                            |
| spring-expression      | Spring表达式语言                                             |
| Spring之切面编程       |                                                              |
| spring-aop             | 面向切面编程的应用模块，整合Asm、CGLib、JDKProxy             |
| spring-aspects         | 集成AspectJ，AOP应用框架                                     |
| spring-instrument      | 动态Class Loading模块                                        |
| Spring之数据访问与集成 |                                                              |
| spring-jdbc            | Spring提供的JDBC抽象框架的主要实现模块，用于简化Spring JDBC操作 |
| spring-tx              | Spring JDBC事务控制模块                                      |
| spring-orm             | 主要集成Hibernate,Java Persistence API(JPA)和Java Data Ojects(JDO) |
| spring-oxm             | 将Java对象映射成XML数据，或者将XML数据映射成Java对象         |
| spring-jms             | Java Messaging Service能够发送和接收信息                     |
| Spring之Web组件        |                                                              |
| spring-web             | 提供了最基础Web支持，主要建立于核心容器之上，通过Servlet或者Listeners来初始化IOC容器 |
| spring-webmvc          | 实现Spring MVC(model-view-controller)的Web应用程序           |
| spring-websocket       | 主要是与Web前端的全双工通讯协议                              |
| spring-webflux         | 一个新的非阻塞函数式Reactive Web框架，可以用来建立异步的，非阻塞，事件驱动的服务 |
| Spring之通信报文       |                                                              |
| spring-messaging       | 从Spring 4开始新加入的一个模块,主要职责是为Spring集成一些基础的报文传送应用 |
| Spring之集成测试       |                                                              |
| spring-test            | 主要为测试提供支持的                                         |
| Spring之集成兼容       |                                                              |
| spring-framework-bom   | Bill of Materials，解决Spring的不同模块依赖版本不同的问题    |

### Spring版本命名规则

以Linux Kernel版本命名为例：

| Linux Kernel | 0.01    1.0.0    2.6.32 | 若用X.Y.Z表示，则偶数Y表示稳定版本，奇数Y表示开发版本 |

语义化版本命名通行规则

| 序号 | 格式要求 | 说明                                                         |
| ---- | -------- | ------------------------------------------------------------ |
| X    | 非负整数 | 表示主版本号(Major)，当API的兼容性变化时，X需递增            |
| Y    | 非负整数 | 表示次版本号(Minor)，当增加功能时(不影响API的兼容性)，Y需递增 |
| Z    | 非负整数 | 表示修订号(Patch)，当做Bug修复时(不影响API的兼容性)，Z需递增 |

Spring版本命名规则

| 描述方式 | 说明     | 含义                                                 |
| -------- | -------- | ---------------------------------------------------- |
| Snapshot | 快照版   | 尚不稳定，尚处于开发中的版本                         |
| Release  | 稳定版   | 功能相对稳定，可以对外发行，但有时间限制             |
| GA       | 正式版   | 代表广泛可用的稳定版(Geneeral Availability)          |
| M        | 里程碑版 | M(Milestone)具有一些新的功能或是具有里程碑意义的版本 |
| RC       | 终测版   | Release Candidate，即将作为正式版本发布              |


































































