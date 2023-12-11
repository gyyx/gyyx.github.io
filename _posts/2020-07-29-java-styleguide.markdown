---
layout: post
title:  "Java开发规范指南 V1.2"
date:   2020-07-29 09:40:27 +0800
categories: dev
---

## 指南目的

随着团队使用Java语言范围的增加。目前Java语言已经变成团队的主流开发语言，但在开发中还有很多不标准的地方，对于程序的部署、后人的阅读理解都有一些困惑的地方。在此基础上我们指定了此开发规范指南，此指南并不等同于代码规范，更详细的部门代码规范还请看[这里（限内网）](http://git.gydev.cn/support/ratingcode/wikis/Java_Code_Rules)本指南会把大家容易犯错的地方单独拿出来进行说明，并会指定jdk以及常用版本的版本。并总结过往开发中所犯的错误在开过中如何避免的方法。

## 开发工具以及版本的指定

### JDK版本

目前JDK统一要求使用1.8的大版本，小版本号为 Java SE Development Kit 8u192。下载链接

* [Linux x64](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html#license-lightbox)
* [Mac OS X x64](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html#license-lightbox)
* [Windows x64](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html#license-lightbox)

### 推荐的IDE

Java语言开发有众多的IDE可以使用，但在团队内部为了统一标准推荐使用Jetbrain家的[IDEA](https://www.jetbrains.com/idea/)。

### 线上环境

目前我们线上统一使用kubernetes来做环境部署，也就是所有发布的产品包都要基于Docker来进行分发。Java的项目的Docker基础包为 gyyx/debian11-jdk11:1.0。如果是新项目我们建议尽量使用SpringBoot框架来进行开发,引用gydev-dependencies最新稳定版本

## 语法规范

### 源文件规范

应该保证同一个.java文件中只出现一个类、接口。文件名同于源文件内的顶级类。源文件编码格式应为UTF-8。一个源文件应包含

1. 版权信息（可选，对内项目不需提供。对外开源项目需提供）示例见注释章节
2. package语句：包名定义见下面
3. import语句：不允许使用.*
4. 一个顶级类（并只能有一个，内部类除外）
5. 以上每个部分之间需要一个空行
6. 源文件内，应该构造方法在最上面，然后是公有方法。最后是私有方法。重构的多个方法应排在一起。
7. <font color="red">【强制】</font>请不要使用过时的类或方法
8. 对于实体类不用依次书写属性的get\set方法，请直接引用lombok组件

``` java
java项目(游戏)
    1. 控制层：UserController.java
    2. 逻辑层：UserService.java; UserServiceImpl.java
    3. 业务层：UserBll.java / UserReaderBll.java / UserWriterBll.java ;  UserBllImpl.java / UserReaderBllImpl.java / UserWriterBllImpl.java
    4. 逻辑层和业务层非必须有接口和实现，简单逻辑可以直接写实现类
    5. 数据持久层： UserMapper.java
    6. 实体： 业务实体（不准使用拼音组合）-Bean结尾
```

### 命名规范

1. 标识符必须使用ASCII内的26个字母加上10个数字。尽量使用完整的英语单词来进行描述，严禁使用拼音、英语缩写。处静态常量外避免使用下划线进行单词的分隔。   
    错误：`fuwuqiList`  正确：`serverList`
2. 普通变量的命名必须使用lowerCamelCase风格   
    正确：`localValue, userName`
3. 【注意】在JavaBeans类中如果有布尔类型变量，在变量名前不要加is。否则框架解析序列化时会出错。   
    错误：`isSuccess`  正确：`success`
4. 不要使用item、data之类的不明确命名   
    错误：`data`  正确：`userData`
5. 静态常量使用全大写命名   
    正确：`ACTION_CODE`
6. 如无特殊说明，所有包均要以cn.gyyx开头
7. 类名使用UpperCamelCase风格。如`GameService`
8. 对于Service和DAO类，有一些命名前缀的需求
   * 获取单个对象的方法用get做前缀
   * 获取多个对象集合时用list做前缀
   * 获取计数值的方法用count做前缀
   * 插入新数据请使用insert做前缀
   * 删除新数据请使用delete做前缀
   * 修改数据请使用update做前缀
9. 枚举类名带上Enum后缀，枚举成员名称需要全大写，单词间用下划线隔开
10. 使用后缀表达意图
控制器以Controller为后缀AccountController
业务服务类以Service为后缀AccountService
通用业务层类以Manager为后缀AccountManager
数据访问类以Dao为后缀AccountDao
业务实体以Bean为后缀AccountBean
视图对象以Vo为后缀，例如:请求LoginRequestVo,
响应LoginResponseVo
工厂类以Factory为后缀SmsFactory
策略类以Strategy为后缀RewardStrategy
基类以Base为后缀SmsBase
业务接口实现类以Impl为后缀AccoutServiceImpl
11. 使用前缀表达意图
抽象类以Abstract为前缀AbstractWorkOrder
默认实现类以Default为前缀DefaultWorkOrder



### 注释

在一个类文件中，至少要包含：

0. 类、类属性、类方法必须使用javadoc格式注释
1. 文件注释：对于面向公司内部的项目可以不写文件注释。如果项目是对外开源的需要写文件注释，写明版权信息和首次创建日期。

    ``` java
    /*
     * Copyright (C) GYYX.CN
     *
     * @version 1.0
     * @date 2020-1-1 0:0:0
     */
    ```

2. 类注释：需要写名类包含的的功能。能让其他人看明白。并要声明类的作者。日期和版本声明为可选

    ``` java
    /**
    * 获得游戏服务器相关信息的控制器
    * @author syp
    * @date 2020-1-1
    * @version 1.0
    */
    public class GameController {
        /** 省略类代码 */
    }
    ```

3. 方法注释：需要写名方法的功能，参数的定义，以及返回值的说明。示例如下：

    ``` java
    /**
     * 根据游戏ID获取服务器列表
     * @param gameId 数字形式的游戏编号
     * @return 成功时：
     *     返回结构体内success为true
     *     data内游戏服的集合，
     *     失败时：
     *     success为false
     *     message为失败原因
     */
    @RequestMapping("/getServerList")
    public ResultBean<Object> getServerList(Integer gameId) {
        /** 代码省略 */
    }
    ```

4. 在使用IDEA做为开发工具时，可以使用`//region 描述`和`//endregion`对做同件事的代码块进行包含，方便对不影响主要业务逻辑的代码进行折叠。快捷键(Ctrl+Alt+t/Com+Alt+t)

    ``` java
    //region 参数检查
    if (gameId == null || gameId.intValue() < 0) {
        log.info("getServerList param error");
        ResultBean.paramError("游戏ID不能为空");
    }
    //endregion
    ```

5. 代码中对于调用第三方接口的地方要写注释加以说明，注释要放在待说明代码的上侧。

    ``` java
    // 调用game module网络接口，获得指定游戏的服务器列表
    StageResultBean<List<GameServerDto>> serverListResult = gameModuleFeign.getGameServer(gameId);
    ```

6. 对于业务逻辑出现if的地方需要说明判断的业务原因

    ``` java
    // 错误的，没有注释说明为什么充值额错误了
    if(form.getRmb()%10!=0){
            log.info("充值金额错误");
            return new ResultBean<>(false, "充值金额错误", ErrorType.PARAM_ERROR, null);
    }

    //正确的：
    // 需求上要求充值额度必须为10的倍数
    if (form.getRmb()%PAY_MULTIPLE != 0) {
        log.info("充值金额错误");
        return new ResultBean<>(false, "充值金额错误", ErrorType.PARAM_ERROR, null);
    }
    ```

7. 中国人的母语是汉语，请把你的注释用汉语进行书写。除非遇到专有名词，像http tcp/ip 之类。
8. 所有枚举项目都要注释说明数据项的用途
9. 【强制】在代码修改的时候，注释也要跟着修改。包括修改代码的方法注释。
10. 不用的代码不要用注释的方式，不用了就删除掉，通过版本控制可以找回该代码的历史记录
11. 使用正则的时候一定要有注释，说明你想匹配的内容。也许你所写的正则是错误的，后人没有注释无法理解你想匹配的正确 内容。

### 格式规范

1. 大括号与if, else, for, do, while等语句一起使用时，即使只有一条语句，也要把大括号写上。

    ``` java
    // 错误的
    if (serverName.equals(actionServer))
        actionStatus = 3;
    // 正确的
    if (serverName.equals(actionServer)) {
        actionStatus = 3;
    }
    ```

2. 对于空代码块，大括号可以不换行。

    ``` java
    void doNothin() {}
    ```

3. 列限制要控制在80个字符以里。

### 更多代码规范

1. 对于Spring的注入不推荐使用field注入，推荐使用构建方法式注入

    ``` java
    // 不推荐，会有警告[Field injection is not recommended]
    @Autowired
    private GameModuleFeign gameModuleFeign;
    // 推荐
    private final GameModuleFeign gameModuleFeign;

    public GameService(GameModuleFeign gameModuleFeign) {
        this.gameModuleFeign = gameModuleFeign;
    }
    ```

2. 程序当中不要使用魔法值，而应生成静态常量加以说明

    ``` java
    // 错误的示例，含义不清晰
    if (!"1".equals(serverListResult.getStatus())) {
        return ResultBean.statusError("获取游戏服务器列表失败");
    }
    // 正确的
    /**
     * 网络请求成功标记
     */
    public static final String REQUEST_SUCCESS = "1";


    if (!REQUEST_SUCCESS.equals(serverListResult.getStatus())) {
        return ResultBean.statusError("获取游戏服务器列表失败");
    }
    ```

3. 使用请安装代码规范检查插件，目前推荐使用Alibaba Java Code Guidelines。来做项目的代码规范检查。我们要清除掉所有不规范的代码   
    ![alibaba-java-code-guid](/static/2020-08/alibaba-java-code-guid.png)

## 工程规范

### 架构
1. 使用Spring cloud微服务架构
2. 网关使用Spring Cloud Gateway
3. 网关需要拆分成不同的端，例如小程序、app、后台、api等网关，微服务也拆分成用户端和后台等微服务
4. 服务注册与发现使用的是k8s的Service
5. 配置中心使用k8s的configmap和secret,敏感信息以及内外网配置不一样的需要放在secret里
6. 定时任务新项目使用xxl-job,老项目逐步从spring task转移到xxl-job，定时任务必须使用独立的服务
7. 持久层框架使用基于注解的mybatis,不能使用一些更高级的工具例如tk之类的
8. 同一业务不同服务之间dao和beans使用这个业务的公共项目，如果发现共用逻辑也需要抽离到公共项目
9. 分层架构（出行）
  表现层 接受和响应
  服务层 用来做业务的统一编排
  通用业务层 包含第三方接口封装，以及缓存、mq等操作封装，以及组装多个dao
  数据访问层 封装对数据库表的操作

上层能调用下层的
服务层可以直接调用数据访问层，其它层不允许
同层之间的类是不能互相调用的
下层不能调用上层

### 工程结构
1. controller 
放所有的控制器类
2. annotation
放所有自定义注解
3. config 
spring bean的注入
4. feign 
项目内微服务之间的调用
5. interceptor
放项目内的拦截器
6. service
主要负责业务逻辑编排，如果既有接口又有实现类把实现类放在service.impl下
7. manager
主要是封装第三方接口，对缓存、消息队列等操作，对多个dao的组装
8. dao
放数据访问相关类
9. beans
放业务实体相关类，实体上不允许使用和数据库表或字段相关的注解
10. vo
用于存放接收与返回外界需要组装的信息
11. .gitignore
把所有不需要放到git仓库的从这里排出，例如开发工具相关的文件，编译后的文件等
12. resources
bootstrap.yml 只用于指定configmap和secret
application.yml 存放configmap里的内容
log4j2.xml 日志配置文件
	console 控制台输出的日志会收集在kibana需要收集
	/data/logs/项目名/readable/ 存放人可读的日志，不会被收集

### 项目命名规范

1. GroupID格式： cn.公司名.业务线，三级构成，如果非光宇公司业务，请使用相应公司顶级域名倒转使用，例如 光宇游戏cn.gyyx.业务线，光宇出行cn.costrip.业务线
               如果是光宇通用cn.gydev.业务线。
2. ArtifactID格式：产品名-模块名。如 账号业务下的对外接口  account-api
3. Version格式: 使用三位分割 主版本号.次版本号.修订号
    1. 主版本号：当然项目发生大的变更时进行升级，由部门级别决定
    2. 次版本号：小版本开发时升级，目前等于迭代号。主版本号升级时该项清零。
    3. 修订号：当发生BUG时，二次部署时进行升级。次版本号升级时该项清零。

### 类库引用规则

1. 【强制】线上业务严禁引用SNAPSHOT版本。
2. 引用第三方库时需先查询第三方类库列表确定可使用的版本。如果列表中未规定需与技术经理确定，并增补该列表。

### 接口
1. 接口主动返回的http状态都是200
2. 接口返回的数据格式

    | 属性名称 | 数据类型 | 属性描述 |
    |----|-----|----|
    |retCode|String|请求成功为success, paramError代表参数不合法，statusError代表业务规则不满足，tokenError代表用户登录状态不正常，serverError代表服务器发生错误|
    |subRetCode|String|子返回码，不是必须的，如果业务特殊的可以自定义，没有错误的话此字段为空字符串|
    |message|String|中文友好的消息提示|
    |data|Object|当success为true和error为空字符串，并且接口需要返回业务数据的时候返回，否则是null|
 
    {"retCode":"success","subRetCode":"","message":"","data":{"code":1,"name":"test"}}
3. 接口文档使用apidocx插件自动生成


### 日志
1. 统一使用slf4j与log4j2写日志
2. 生产环境关闭debug
3. 异常必须使用error级别记录日志
4. 不影响业务流程的异常使用warn记录日志
5. 通过info日志记录业务执行流程

### 安全
1. 正式环境的密钥和密码等敏感信息统一由运维生成管理，开发人员不应该知道并且也不能查看
2. 提供给系统外部服务器调用的接口必须要签名验证，如果是内部跨系统之间调用还需要使用ip白名单机制
3. 业务敏感信息的需要打码返回，例如手机号135*****111,姓名、身份证号、身份证照片、人脸照片、银行卡号、支付宝账号等
4. 业务敏感信息网络传递时需要加密传递
5. 业务敏感信息在数据库中存储时应加密存储

### 高并发
1. 对实时要求不高的查询使用缓存
2. 对于有些不适合使用缓存的可以使用从库查询，执行速度必须要快
3. 对于使用队列解决重复消费的问题
4. 使用分布式锁或数据库锁避免并发导致的数据问题，数据库锁用于安全性特别高的场景例如和钱相关的
5. 非核心流程，先记录然后异步做后续处理

### 可配置参数

1. 可配置参数一定不要在程序中写死，要放到数据库或配置文件中。
2. 为了减少二次加载，应将配置项在启动时即装入内存中。

### URL规范

1. 【强制】url中不出现大写字母
2. 【强制】url中不出现版本号，如果需要做版本号识别放在http header中。K:version,V:1.0
3. 如果需要分割单词使用-不用_
4. 不出现过深级别的url，避免过于难维护
5. 根据浏览器的限制，url的最大长度不超过1000个字符。

### 连接串

1. 所有连接串中不要出现机器IP，请使用域名进行链接
2. memcahced和redis会出现需要在请求端负载的情况，请使用部门内缓存的XML文件位置获取请求连接串。

### 线程类

1. 非必要，不要在BS程序的服务器端中手工创建新线程。
2. 所有线程类操作必须通过线程池来维护线程，不能在代码中自行创建线程
3. 并发大的时候，需要串行执行的部分，会严重降低性能。考虑是否可以不使用锁用其他方式代替。

### 返回值还是抛出异常
1. 对于共享电单车以及三轮车项目可以抛出异常，现在是试运行阶
2. 只有需要终止整个请求的才能抛出异常
3. 代码可以抛出统一的业务异常，统一对异常进行捕获处理转换成ResultBean
4. service可以不返回ResultBean，可以在controller内返回ResultBean
5. 各业务在一个类中定义异常分类和描述

### 其他

1. 对于返回给前端的敏感数据必须进行脱敏处理，比如手机号、账号中间几位必须替换为*号
2. 对于携带订单号的操作必须保证一个订单号只操作一次
3. 对于有可能被用户通过重试得到利益的接口，必须使用验证码来进行防范。
4. 对于来自用户的输入会直接显示在前台展示给其他用户查看的信息，务必小心。
   1. 要做关键字过滤防止提交不良信息
   2. 要防止用户写入JS脚本恶意活的其他用户数据。

## 附件

### 类库引用版本指南

* 验证码类库

    | 库功能 | groupId | artifactId | version | 备注 |
    | :---: | :-----: | :--------: | :---: | :-: |
    | 验证码 | cn.gyyx.validation | captcha-module-sdk | 1.1.6 | |
    | springboot | org.springframework.boot | spring-boot-starter-parent | 2.2.9.RELEASE | 整个组都需要使用2.2.9.RELEASE版本 |