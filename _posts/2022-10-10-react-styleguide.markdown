---
layout: post
title:  "React代码规范 V1.0"
date:   2022-10-10 09:40:27 +0800
categories: dev
---

## 指南目的

主要解决日后项目开发阶段，各位开发人员的开发风格差异性造成的维护成本。此文档用于供各开发人员进行开发规范参考，该文档所定义的开发规范均采用公司及业界普遍认可的规范。

## 推荐工具以及版本

### 版本
[ECMA-262](https://www.ecma-international.org/publications-and-standards/standards/ecma-262/)
[nodejs 16.10+](https://nodejs.org/zh-cn/)
[react 16+](https://zh-hans.reactjs.org/)
[yarn]

### 开发工具

[WebStorm](https://www.jetbrains.com/webstorm/)
[vscode](https://code.visualstudio.com/)

### 修改npm源为内网

~~~ shell
npm config set registry http://lib.gydev.cn/repository/npm-group/
~~~

## 语法规范

TypeScript 是一种基于 JavaScript 的强类型编程语言，可为您提供任何规模的更好的工具。所以我们推荐在项目中使用TS。

推荐使用yarn代替npm做为包管理器 node 16.10后版本直接执行 `corepack enable` 即可

### 目录