---
layout: post
title:  "K8S-Jenkins-使用指南"
date:   2020-09-25 15:35:18 +0800
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
2. 更改系统配置：配置类库地址（上面提到的jenkins-json-build）、设置邮件通知（在系统管理-系统设置-系统管理员邮件地址 这里写上邮件地址，这个是发件人；在系统管理-系统设置-Extended E-mail Notification-SMTP server 这里写上邮箱服务器地址；在系统管理-系统设置-Extended E-mail Notification-高级 勾上Use SMTP Authentication，填写发件人邮箱和密码，注意这个邮箱 一定得和系统管理员邮件地址相同，切记！）
3. 主要涉及的插件安装：Kubernetes、Git相关（Git、Git Parameter）、Pipeline相关、SSH相关；
4. 视图管理：将同一项目的各个构建分配在同一视图下；
5. 节点管理：将要使用到的机器通过Launch agents via SSH进行连接；

### 部署系统
这是内部开发的一套系统，作用有以下几点
1. 提供外部接口，实现为Jenkins中为Dockfile打标签阶段时提供下版本号，目前为4位（0.0.0.0，第1位是项目版本，第2位是迭代版本，第3位是部署环境，第4位是部署原因），前提是需要在部署系统中进行相关的配置，参考下图；
2. 将打好标签的Dockerfile push到外网环境，以及进行跨部门的调用接口；


## 构建的具体实现

### 流程图如下
![Docker项目](/static/2020-09/Dockerbuild.png)

### 当同一仓库下包含多个站点时，实现方式有所不同。
项目根目录下的Jenkinsfile需要配置projectlist，实现在构建时选择哪个站点进行构建，文件内容如下：
![Jenkinsfile](/static/2020-09/Jenkinsfile.png)
project-list.yaml的key和value分别对应的是下拉选项的显示项和实际值（参考下图），其中value的值，为仓库根目录下的子目录层级的文件夹名称，并且在其目录下要存在jenkins-project.json文件，每一个想要构建的站点目录的下
json文件都是相互独立的，可个性化配置，这也是和之前的构建方式不同的一个地方，之前都是仓库下仅存在一份jenkins-project.json文件，所有需要构建的站点的部署方案通通写在里面，而且绝大多数会存在2种，部署内网和部署外网，
文件内容易重复、易混淆
![project-list](/static/2020-09/project-list.png)
下面，针对某一个站点的jenkins-project.json配置文件做下说明：  
1. 文件全局语法需要满足json的key-value形式，其中value可为字符串或者对象；  
2. RuntimeVariable中的变量PROJECT_VERSION为调用上文提到的部署接口发送HTTP Get请求来获得，拿到的结果为4位版本号，不过需要提前在部署系统中对${PROJECT_VERSION}进行相关配置，如下；  
   * 创建域名，分发机类型linux，自定义文件路径填写系统部的镜像路径；  
   * 为域名分配所属项目，如果没有，需要新建（创建完成后，记得还需要创建立项报告）；  
   * 在xx.xx.xx.xx的/data/JavaBuild/build 目录下创建和域名同名的文件夹，里面需要放置一个ROOT.war，可为空；  
   * 添加分发机xx.xx.xx.xx，需要添加3种类型（分发、测试、正式），其中测试和正式的需要配置 TomcatApp目录 和 Tomcat名称，值分别为/data/WEB/域名/website/和域名；  
   * 更改数据库，update [dbo].[domain_tb] set is_support_docker = 1 where [domain] = '域名';  
3. 其中“部署”集合下的对象中可包含多个部署方案，如上文提到的部署内网、部署外网等，每一个部署方案中的脚本执行类型分为COMMAND_STDOUT、COMMAND_STATUS、COMMAND_STATUS_FOR三种，根据需要设置成
其中一种即可，下面有对3中类型的简要说明，详细可参考* [类型说明](https://github.com/sunweisheng/jenkins-json-build#Json%E6%96%87%E6%A1%A3%E6%A0%BC%E5%BC%8F%E5%8F%8A%E8%BF%90%E8%A1%8C%E6%96%B9%E5%BC%8F)  
   * COMMAND_STDOUT：执行命令行脚本并输出脚本的标准输出内容；  
   * COMMAND_STATUS：执行命令行脚本并输出脚本的返回值（0代表成功，非0代表失败）；  
   * COMMAND_STATUS_FOR：循环创建需要执行的脚本然后用COMMAND_STATUS方式执行；  
4. Script中的命令格式中要注意特殊字符的转义问题，例如在双引号中使用双引号，应该是"\\""，如使用多行命令组合形式，可参考下面的一行；  
   * `"docker-tag分支": "if [ $DEPLOY_BUILD ]; then version=\\"$(curl ${projectoaurl}?user=${Jenkins@BUILD_USER}\\&domainname=${dockerAppName}\\&desc=${dockerImageServer_exter}/baseimages/${dockerImageName}:$version)\\";cd ${ProjectRoot}/${jarFilePath};curl -fL -o ${ProjectRoot}/${jarFilePath}/Dockerfile http://xx.xx.xx.xx/Dockerfile;echo $version;docker build -t ${dockerImageServer_exter}/baseimages/${dockerImageName}:$version .;docker push ${dockerImageServer_exter}/baseimages/${dockerImageName}:$version;rm -rf ${ProjectRoot}/${jarFilePath}/Dockerfile; fi"`

![jenkins-project](/static/2020-09/jenkins-project.png)






