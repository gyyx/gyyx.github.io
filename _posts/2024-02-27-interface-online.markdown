---
layout: post
title:  "interface.account 开发-上线问题总结"
date:   2024-02-27 13:40:27 +0800
categories: dev
---
# interface.account 开发-上线问题总结
## 一 开发阶段
### 1.1 接口实现
#### 1.1.1 接口实现的基本流程
对于interface.account相关接口的选择实现，主要分为以下几步：
1. 向系统部索要interface.account的一周nginx访问接口（去重）后的path列表
2. 分别找出接口源码，评估实现的必要性和可优化性
3. 确认初步需要实现的接口列表，并画出流程图
4. 开会确认接口业务逻辑，现场优化并确认最终逻辑
5. 接口实现、提测、与测试连调最终上线。
6. 开会Review代码，找出并解决问题，最终修改上线。
7. 确认时间，进行百分比灰度。

#### 1.1.2 查询是否有遗漏的接口
es查询语法 
```sql
server_name : interface.account.gyyx.cn and (not request : loginlog Register)
```
可以排除我们已经实现的接口，查询目前线上是否有请求。

### 1.2 与.NET版本的一致性
#### 1.2.1 状态码的一致性
在.NET版本，会根据不同的业务，返回不同的状态码，比如200、400、500等，所以在做java版本的时候，需要手动返回对应的状态码，要与.NET强一致。
推荐使用Spring自带的ResponseEntity，可以很精确的返回需要的状态码和响应头。
```java
@GetMapping("/xxx")
public ResponseEntity<Object> testXXX() {
    ...
    xxx
    ...
    return new ResponseEntity<>(xxxx, HttpStatus.OK);
    }
```
#### 1.2.2 入参的一致性
在.NET中，会出现大驼峰或下划线分割的参数或者其他与java小驼峰不一致的情况。
推荐处理方式
get请求
```java
@RequestParam("UserName") String userName
```
post请求
```java
@Data
public class OldModifyGamePwdDTO {

    @JsonProperty("ServerID")
    public int serverID;

    @JsonProperty("Account")
    public String account;
    
    }
```
路径传参
```java
    @GetMapping("/{userId}/reginfo")
    public ResponseEntity<Object> getUserRegInfo(@PathVariable Integer userId) {}
```
#### 1.2.3 出参的一致性
1. 返回结果要强一致
如
```javascript
return "5, 该服务器人数已满，暂停激活";
```
这里的5和提示语一定要与旧版一致，因为在游戏或者其他业务，可能会接收并做后续处理。

2. 返回值的编码要一致

java默认返回utf-8，但是有些地方，如游戏会要GBK等其他编码，需要开发抓包，并保持一致。

3. 返回实体还是json要一致

在游戏等业务中，会解析我们的返回值，一定要确认要的格式，是要实体还是json，是空字符串还是null。

在.NET中有时候会返回一个类似于匿名的结构体，在一个接口中是不统一的，有时候参数多，有时候参数少，一定要根据具体的业务，返回一模一样的返回值。

#### 1.2.4 路由的一致性
在.NET中，可能会配置统一路由，即可以通过包名+控制器名+方法名访问到接口，

也可能会指定[ActionName("xxx")]来自定义访问路由，并且两者可以共存，甚至有时候不区分大小写，所以需要在java中进行兼容。

```java
@RequestMapping({"/WenDaoService", "/wendaoservice", "/WenDaoHaiWai", "/wendaohaiwai"})
```

### 1.3 日志打印与统一风格
#### 1.3.1 建议完全打印入参和出参
为了排查问题，需要开发全量打印入参和出参,并在网关或者站点拦截器内，打印所有请求，如请求来404，或者请求到，但是参数问题导致400等，都需要打印。
#### 1.3.2 接口要捕获异常并打印error日志
接口全局捕获异常，并打印有用的error日志，方便上线看日志和加监控报警。
#### 1.3.3 建议建实体，而不是Map.of或写死字符串等不通用方法
为了通用性，需要开发尽量建立实体而不是写死字符串
#### 1.3.4 规范包结构
需要开发根据业务划分不同Controller和Service，不要都写在一个里面。
如果涉及在已有非interface业务的站点上开发，需要创建子包以区分业务类型。
```tree
├── controller
│   └── interfacecontroller
└── service
    └── interfaceservice
```
严禁在controller一把梭，将所有业务都写入控制器！

## 二 测试阶段
### 2.1 优先找到业务进行测试
优先找该接口是哪个业务调用的，用业务来测接口。
### 2.2 自动化覆盖
需要接口自动化覆盖，确保入参出参的一致性。

## 三 上线阶段
### 2.1 调用方为游戏
#### 2.1.1 确认游戏能正确解析接口返回值
如果游戏解析返回值异常，需要系统部在对应区组服务器上配合抓包，开发进行分析，需要分析请求头和响应头等因素，必要时需要将数据包全量抓下进行分析。甚至可以去系统部看游戏服务器内的异常日志。进行分析。
#### 2.1.2 游戏解析返回值后 后续业务是否正常
如果游戏成功解析返回值，确保游戏数据库内数据正常，必要时需要和游戏开发进行交流。
### 2.2 调用方为光宇通、支付等其他社区业务
一定要确保没有报异常，后续业务正常进行。

## 四 灰度阶段
### 3.1 增加站点的异常状态码和error日志监控
需要系统部帮加上相关告警监控和群提醒
### 3.2 开发盯日志
开发需要盯日志，有问题立刻回滚并解决问题。


