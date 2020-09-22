---
layout: post
title:  "修复OCLint在Jenkins代码检查环节失败"
date:   2020-09-07 12:18:00 +0800
categories: ios
---


## 问题描述

我们的项目持续集成与交付采用的方案是`Jenkins`, `iOS`项目采用`Object-C`作为开发语言

整个持续集成与交付过程分为: `初始化项目 → 单元测试 → 代码检查 → 打包 → 部署`这几个环节

升级`Xcode 11`后, 在持续集成`SonarQube`环节, 执行`OCLint`分析环节(第二步执行`oclint-json-compilation-database`时)抛出异常, 返回错误码`6`, 导致持续集成失败

通过本地终端调试, 一般遇到的错误有:

- 一个或多个`X error generated`

- `oclint:error:cannot open report outputfile oclint.xml`

- 偶尔生成了`PMD`文件, 里面发现会带有苹果`SDK`的信息, 如`/Applications/Xcode.app/Contents/Developer/Platforms/...`路径下

## 问题分析

首先将持续集成中代码检查这一环节按照工具链拆分成以下步骤:

  1. 执行`xcodebuild`和`tee`指令采集`xcodebuild.log`文件  
  2. 通过`xcpretty`对`xcodebuild.log`文件做格式化处理  
  3. 通过`OCLint`的`json-compilation-database`工具将`xcodebuild.log`文件编译生成[JSON Compilation Database Format](http://clang.llvm.org/docs/JSONCompilationDatabase.html)格式的`compile_commands.json`文件  
  4. 执行`OCLint`的`oclint-json-compilation-database`工具从`compile_commands.json`文件中提取必要信息, 生成`jenkins`识别的`PMD`格式文件  
  5. 执行`SonarQube`处理

目前是在执行`步骤4`的时候出现了错误, 怀疑是`步骤3`或`步骤4`在`OCLint`某一个环节出错, 最终查看官方`issues`, 可以确认为`OCLint 0.13.0`中`LLVM`版本过低, 需要升级`OCLint`

- [issue547](https://github.com/oclint/oclint/issues/547)  
- [upgrade llvm 9.0 to resolve xcode11 build's issue](https://github.com/oclint/oclint/pull/548)  

`homebrew`当前最高可用版本为`0.13`, 正是目前所使用的版本:

- `$ oclint --version`

  ```bash
  LLVM (http://llvm.org/):
    LLVM version 5.0.0svn-r313528
    Optimized build.
    Default target: x86_64-apple-darwin19.4.0
    Host CPU: skylake

    OCLint (http://oclint.org/):
    OCLint version 0.13.
    Built Sep 18 2017 (08:58:40).
    ```

- 在这之后, 其实还有`0.13.1`、`0.14`和`0.15`版本, `0.14`和`0.15`目前只能通过编译安装

## 终端科学上网, 编译安装过程简介

### 一 通过`homebrew`安装`cmake`和`Ninja`两个编译工具

`$ brew install cmake ninja`

### 二 本地编译安装`OCLint 0.15.0`

1. [Clone OCLint SourceCode](https://github.com/oclint/oclint/releases)
2. `cd` 到`oclint-scripts`路径下
3. 执行`./make`, 成功之后会出现`build`文件夹, `oclint-release`就是编译成功的`OCLint`工具
4. 设置`OCLint`工具的环境变量:

    - 添加环境

        ```bash
        OCLint_PATH=换成你存放的实际路径/oclint/build/oclint-release
        export PATH=$OCLint_PATH/bin:$PATH
        ```

    - 更新环境

        执行`source ~/.zshrc`  
        这里我用的是`zsh`如果你使用系统的终端则执行 `source .bash_profile`

    - 把`bin`中的全部文件复制到`/usr/local/bin/`和`/usr/local/lib`中

        ```bash
        cp 换成你存放的实际路径/oclint-0.15/build/oclint-release/bin/oclint* /usr/local/bin/
        sudo cp -rp 换成你存放的实际路径/oclint-0.15/build/oclint-release/lib/* /usr/local/lib/
        cp -rp 换成你存放的实际路径/oclint-0.15/build/oclint-release/include/* /usr/local/include/
        ```

    `OCLint` 官方指导手册中最后环节是直接复制文件夹到指定路径下的  
    通过`homebrew`安装的`0.13.0`版本, 我提前看了下环境, 部分是链接过去的

    - 在`usr/local/lib/`路径下可以看到`oclint`和`clang`文件夹

    - 通过右键菜单选中`显示原身`, 会跳转到实际路径是`/usr/local/Cellar/oclint/0.13`

    我第一次升级`OCLint 0.15.0`失败, 很有可能是这三个步骤中的`文件夹名称`, `/`和`*`某一步的时候缺少了或是错了, 也可能是没有终端科学上网, 在编译期间下载某些组件时出错, 所以这里需要注意一下

5. 验证`OCLint`安装成功, 执行`$ oclint --version`会显示:

    ```bash
    OCLint (http://oclint.org/):
    OCLint version 0.15.
    Built Sep  2 2020 (12:16:58).
    ```

    在`OCLint 0.13.0`下还会输出`LLVM 5.0.0`等信息, 这里是正常的, 在`OCLint 0.15.0`中已经升级到了`9.0.0`

## 验证问题解决

升级`OCLin 0.15.0`成功后, 本地终端和`jenkins`分别成功完成了`OCLint`代码分析, 最终确认问题得到解决.

## OCLint官方资料

- [OCLint Installation *](http://docs.oclint.org/en/stable/intro/installation.html)
- [Building OCLint](http://docs.oclint.org/en/stable/intro/build.html)
- [OCLint Manual](http://docs.oclint.org/en/stable/manual/oclint.html)
- [oclint-json-compilation-database Manual](http://docs.oclint.org/en/stable/manual/oclint-json-compilation-database.html)
