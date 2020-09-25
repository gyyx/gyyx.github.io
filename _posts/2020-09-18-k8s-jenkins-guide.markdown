---
layout: post
title:  "K8S-Jenkins-使用指南"
date:   2020-09-24 13:59:18 +0800
categories: dev
---

## 指南简介
先在这里简单交代一下我们之前采用的构建部署模式，Jenkins 服务器（Mac）+ 节点机器（Linux、Windows、Mac），涉及构建的项目包含Java、.Net、IOS、Andriod以及微信小程序，构建脚本采用在Jenkinsfile中编写Pipelines，代码存储
采用Git进行管理，每一个项目的Git仓库下都会包含Jenkinsfile（包含构建全程的几个阶段）以及Jenkins-project.json（文件名自己起的，包含构建阶段们的具体实现），构建开始时，用户选择节点机器，由Jenkins master进行任务分配，
节点可并发可串行进行处理具体实现步骤。

最近我们构建和部署的方式与原来相比，已经大不相同。首先，Java项目（产生war文件）不在具体的节点机器（实体机或者虚拟机）上进行构建，而是采用Docker的pod（容器），构建结束，进行回收，其余项目依旧采用节点机器构建模式。
其次，构建脚本模式也发生了改变，对于编写者而言，基本上不再需要具有Pipelines语法知识作为前提，而是可以通过配置json或者yaml文件就可以实现构建的具体步骤，具体参考
* [jenkins-json-build](http://git.gyyx.cn/lib/cn-gyyx-jenkins-libraries.git)，在这里仅做讲述有关现在正在使用的整套CI\CD流程。

## 涉及系统及介绍

### Jenkins
1. 开启用户管理：用户注册、游客不可访问、分配用户权限（采用安全矩阵）；
2. 更改系统配置：配置类库地址（上面提到的jenkins-json-build）、设置邮件通知（在系统管理-系统设置-系统管理员邮件地址 这里写上邮件地址，这个是发件人；在系统管理-系统设置-Extended E-mail Notification-SMTP server 这里写上邮箱服务器地址；在系统管理-系统设置-Extended E-mail Notification-高级 勾上Use SMTP Authentication，填写发件人邮箱和密码，注意这个邮箱 一定得和系统管理员邮件地址邮件地址相同，切记！）
3. 主要涉及的插件安装：Kubernetes、Git相关（Git、Git Parameter）、Pipeline相关、SSH相关；
4. 视图管理：将同一项目的各个构建分配在同一视图下；
5. 节点管理：将要使用到的机器通过Launch agents via SSH进行连接；

### 部署系统
这是内部开发的一套系统，作用有以下几点
1. 提供外部接口，实现为Jenkins中为Dockfile打标签阶段时提供下版本号，目前为4位（0.0.0.0，第1位是项目版本，第2位是迭代版本，第3位是部署环境，第4位是部署原因），前提是需要在部署系统中进行相关的配置，参考下图；
2. 将打好标签的Dockerfile push到外网环境，以及进行跨部门的调用接口；
    ![产生版本号需要的前提](/static/2020-09/产生版本号需要的前提.png)


## 构建的具体实现

### Java项目，参考以下流程图
![Java项目](/static/2020-09/Docker构建流程.png)







