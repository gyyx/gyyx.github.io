---
layout: post
title:  "K8S-Jenkins-使用指南"
date:   2020-09-18 13:59:18 +0800
categories: dev
---

## 指南简介
先在这里简单交代一下我们之前采用的构建部署模式，Jenkins 服务器（Mac）+ 节点机器（Linux、Windows、Mac），涉及构建的项目包含Java、.Net、IOS、Andriod以及微信小程序，构建脚本采用在Jenkinsfile中编写Pipelines，代码存储
采用Git进行管理，每一个项目的Git仓库下都会包含Jenkinsfile（包含构建全程的几个阶段）以及Jenkins-project.json（文件名自己起的，包含构建阶段们的具体实现），构建开始时，用户选择节点机器，由Jenkins master进行任务分配，
节点可并发可串行进行处理具体实现步骤。

最近我们构建和部署的方式与原来相比，已经大不相同。首先，Java项目（产生war文件）不在具体的节点机器（实体机或者虚拟机）上进行构建，而是采用Docker的pod（容器），构建结束，进行回收，其余项目依旧采用节点机器构建模式。
其次，构建脚本模式也发生了改变，对于编写者而言，基本上不再需要具有Pipelines语法知识作为前提，而是可以通过配置json或者yaml文件就可以实现构建的具体步骤，具体参考
* [jenkins-json-build](https://github.com/sunweisheng/jenkins-json-build)，在这里仅做讲述有关现在正在使用的整套CI\CD流程。

## 涉及系统及介绍

### Jenkins
1. 开启用户管理：用户注册、游客不可访问、分配用户权限（采用安全矩阵）；
2. 更改系统配置：配置类库地址（上面提到的jenkins-json-build）、设置邮件通知（在系统管理-系统设置-系统管理员邮件地址 这里写上邮件地址，这个是发件人；在系统管理-系统设置-Extended E-mail Notification-SMTP server 这里写上邮箱服务器地址；在系统管理-系统设置-Extended E-mail Notification-高级 勾上Use SMTP Authentication，填写发件人邮箱和密码，注意这个邮箱 一定得和系统管理员邮件地址邮件地址相同，切记！）
3. 主要涉及的插件安装：Kubernetes、Git相关（Git、Git Parameter）、Pipeline相关、SSH相关；
4. 视图管理：将同一项目的各个构建分配在同一视图下；
5. 节点管理：将要使用到的机器通过Launch agents via SSH进行连接；

### 部署系统
这是内部开发的一套系统，主要作为Jenkins 构建完成之后的操作流程，作用有以下几点
1. 提供外部接口，实现为Jenkins中的某一阶段为Dockfile打标签时提供下版本号，目前为4位（0.0.0.0，第1位是项目版本，第2位是迭代版本，第3位是部署环境，第4位是部署原因），前提是需要在部署系统中进行相关的配置，参考下图；
2. 将打好标签的Dockerfile push到外网环境，以及进行跨部门的调用接口；
3. 在进行非Docker项目部署时实现传送文件的目的；

    ![产生版本号需要的前提](/static/2020-09/产生版本号需要的前提.png)


## 构建的具体实现

### Docker项目，参考以下流程图
    ![Docker项目](/static/2020-09/Docker构建流程.png)

### 微信小程序项目，需要在Jenkins中额外安装2个插件，如下
	插件一：description setter，用于在给构建的历史记录中添加描述，这也就是我们要把二维码图片的html标签拍在的地方
	插件二：OWASP Markup Formatter Plugin，这个主要是要在Jenkins的全局安全配置的标记格式器中启动Safe HTML，要不上面二维码图片的html标签就会显示为纯文本了

	实现过程：
        1. 杀掉构建机器上所有wechatweb的进程；
        2. 启动微信开发者工具在3000端口；
        3. 调用微信开发者工具cli -p 工程目录来预览二维码，并wget到构建机器的Tomcat的ROOT下，格式为构建项目名称_构建编号.png；
        4. 在Jenkinsfile调用触发器，
                成功的时候：给构建历史打上图片描述，请求地址http://xxx.xxx.xx:8080/job/构建名称/构建编号/submitDescription，内容为description=<img src=\"http://xx.xx.xx.xx:8080/${JOB_NAME}_${BUILD_ID}.png\" width=\"200\" height=\"200\" >
                失败的时候：发送邮件







