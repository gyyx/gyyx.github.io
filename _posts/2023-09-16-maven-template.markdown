---
layout: post
title:  "开发部 Maven 项目模版"
date:   2023-09-16 09:40:27 +0800
categories: dev
---

## 〇、首先修改maven的私有源地址
---

在主用户目录的 ~/.m2/ 下新建一个settings.xml文件，内容为

~~~ xml
<settings>
   <mirrors>
     <mirror>
       <id>nexus</id>
       <mirrorOf>*</mirrorOf>
       <url>http://lib.gydev.cn/repository/maven-public/</url>
     </mirror>
   </mirrors>
 </settings>

~~~

## 一、简单的单站点项目

创建默认的spring boot项目请使用.
如果做为子项目使用，创建完成后请删除Jenkinsfile文件

~~~ shell
mvn archetype:generate -DarchetypeArtifactId=single-project-archetype -DarchetypeGroupId=cn.gydev.template -DarchetypeVersion=0.0.3
~~~

1. 打开build-script/config.env变量文件。按注释提示进行变量赋值
2. 请进入pom.xml。适当修改包引用。
3. 打开jenkins-project.json文件。确认是否使用k8s的Configmap配置，如不使用请删除相关部署行
4. 打开build-script/k8s-script.yml文件。默认包含Ingress、Service、Deployment三层，请根据实际项目修改或删除
5. 打开内网CI环境，创建新项目。


目录说明
~~~ shell
.
├── Jenkinsfile                - CI构建主文件，作为多项目的子项目时可以删除此文件
├── README.md                  - 说明文档
├── build-script               - 构建脚本目录
│   ├── Dockerfile             - docker镜像生成用文档
│   ├── config.env             - 构建涉及变量文档
│   ├── configmap-template.yml - CM生成模板
│   └── k8s-script.yml         - k8s生成用脚本
├── jenkins-project.json       - 构建流程使用文档
├── pom.xml                    - maven管理文档
└── src
    └── main
        ├── java
        │   └── package name   - 项目代码目录
        └── resources          - 资源文件目录
            ├── application.yml
            ├── bootstrap.yml
            └── log4j2.xml
~~~

## 二、多项目站点，根结点

此模板适用于多项目在同一仓库情况下的模板。此模板仅用来建立根目录文档，具体的项目请继续使用单项目模板来追加

~~~ shell
mvn archetype:generate -DarchetypeArtifactId=multi-project-archetype -DarchetypeGroupId=cn.gydev.template -DarchetypeVersion=0.0.1
~~~

首次使用需要调整 Jenkinsfile中的 projectList 项目列表文档位置，填入正确的git URL即可。

~~~ shell
.
├── Jenkinsfile         - CI构建主文件
├── README.md           - 当前说明文档
├── pom.xml             - maven管理文档
└── project-list.yml    - *项目列表文档
~~~

增加子项目

~~~ shell
mvn archetype:generate -DarchetypeArtifactId=single-project-archetype -DarchetypeGroupId=cn.gydev.template -DarchetypeVersion=0.0.3
~~~


每次新增加子项目后，需要打开pom.xml和project-list.yml进行修改

## 三、不需要spring注入的项目

~~~ shell
mvn archetype:generate -DarchetypeArtifactId=lib-project-archetype -DarchetypeGroupId=cn.gydev.template -DarchetypeVersion=0.0.1
~~~

1. 核心类库一般是指不需要依赖spring流入的项目。
2. 为了兼容性，请使用jdk1.8进行编译。
3. 请使用单元测试覆盖所有方法的if分支

