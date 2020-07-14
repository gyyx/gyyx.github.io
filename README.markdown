## 光宇开发部知识库使用指南

该知识库是用来记载分享内部的学习成果，让全员学习使用。网址为 [http://know.gydev.cn](http://know.gydev.cn)

### 在本地打开本项目
客隆代码到本地
~~~ shell
git clone git@github.com:gyyx/gyyx.github.io.git know
~~~

使用你喜欢的IDE打开本仓库目录，推荐使用免费的vscode。  
目录说明：
* 正常的文件请放到_post目录中，文件名规则为 日期-文件英文标题.markdown
* 静态资源指文章中涉及到小于1M的文件，请放到static目录下，并以当前月份为目录名创建新目录。大于1M文件请另外保存
* category目录下为文章分类，如增加请分类。请先与管理员进行沟通

![dir](/static/2020-07/project-dir.png)

### 创建以及书写文章
请在_post目录下创建新文章，请注意文件名格式。打开新创建的文件。文件头格式应为
~~~ markdown
---
layout: post
title:  "CocoaPods 环境安装"
date:   2020-07-03 09:40:27 +0800
categories: ios
---
~~~
文章内容请使用标准markdown格式来书写。引用本地图片时，请将图片放入static中，并用  `![注释](/static/2020-07/xxx.png)` 的方式来引用。  
markdown格式使用帮助请参考[https://www.markdownguide.org/basic-syntax/](https://www.markdownguide.org/basic-syntax/)

### 本地测试
在本地调试有两种方式：
1. 安装调试环境，首先需要安装你的开发平台的ruby。
~~~ shell
~ $ gem install bundler jekyll
~ $ cd know
~/know $ bundle exec jekyll serve
# => Now browse to http://localhost:4000
~~~
2. 是比较推荐的直接使用docker环境
~~~ shell
docker run --rm -p 4000:4000 -v know_dir:/opt/know gyyx/jekyll 
# => Now browse to http://localhost:4000
~~~

### 上线部署
本地测试满意后，确保与线上版本无冲突，直接提交代码即可。
~~~ shell
git push
~~~