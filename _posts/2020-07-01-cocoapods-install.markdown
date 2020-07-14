---
layout: post
title:  "CocoaPods 环境安装"
date:   2020-07-03 09:40:27 +0800
categories: ios
---

## 关于CocoaPods

`CocoaPods`是iOS开发、macOS开发中的包依赖管理工具，效果如Java中的Maven，nodejs的npm。

`CocoaPods`是一个开源的项目，源码是用ruby写的，源码地址在GitHub上。

无论是做iOS开发还是macOS开发，都不可避免的要使用到一些第三方库，优秀的第三方库能够提升我们的开发效率。如果不使用包依赖管理工具，我们需要手动管理第三方包，包括但不限于：

将这些第三方库的源码拷贝到项目中
第三方库代码有可能依赖一些系统framework，我们需要把第三方库依赖的framework导入到项目中
当第三方库有更新时，需要将更新过的代码拷贝到项目中
以上工作虽然简单，但是如果项目中的第三方库较多，需要耗费大量的时间和精力。`CocoaPods`可以将我们从这些繁琐的工作中解放出来。

- `CocoaPods` 与常规依赖库使用维护操作上的区别

  - 传统模式下, 当我们使用一些三方开源库、SDK(如QQ分享、微信分享)等时, 我们导入类库的方式是:  
      1. 下载源码, 拖拽到项目指定目录内; 如果有图片素材,`bundle`文件等, 也需要手动一同导入; 并且所处项目结构也需要自行管理
      2. 同属同一个`project`, 通过`import "XXX.h"`来使用类库
      3. 今后`SDK`或依赖库升级了, 那我们要先把旧版本删除掉, 导入新版本依赖库

  - 使用了`CocoaPods`后:  
      1. 在`Podfile`中通过增加`pod '类库名称', '~> 版本号'`
      2. 执行`pod install`完成依赖库的导入
      3. 升级依赖库版本, 只需要修改`Podfile`中对应依赖库后面的版本号重新执行`pod install`即可完成

- 目录结构对比 (左侧是传统模式, 右侧是是用来`CocoaPods`)  
    ![没有使用包管理器](/uploads/e7c1e23204d8604fd9475531299865c9/没有使用包管理器.png)
    ![使用Cocoapods后](/uploads/c77a5dbf7f6e8f94c6e2397e90730c18/使用Cocoapods后.png)

- 对比优势:

  > `CocoaPods`是将所有依赖的第三方库都放到了`Pods`项目中, 所有的源码管理工作从主项目转移到了`Pods`项目中。
`Pods`项目最终会编译成一个`libPods-项目名.a`的文件，主项目只需要依赖这个`.a`文件即可。(不同`Cocoapods`版本以及`Poffile`配置不同, 也可以转成多个`framework`)
省去了枯燥无聊的手工操作, 也免去了维护项目目录的烦恼, 全部的依赖库及使用版本清晰明了; 而且主项目中因为减少了依赖库部分, 也会更简洁

***

## 影响到整个工具链的安装及更新的主要原因

1. 墙

2. 系统权限

3. 版本依赖

4. 其他环境原因 (非常多, 具体原因可根据不同报错查找解决方案)

***

## 环境搭建

### 1. 准备好梯子(必须), 本次安装各工具版本介绍

  > |     工具链     |      版本     |    其他补充    |
  > |:------------- |:------------:|:-------------:|
  > | ruby          | 2.6.5        | rvm 管理版本   |
  > | RubyGems      | 3.0.8        | 源: `https://gems.ruby-china.com/` |
  > | Cocoapods     | 1.9.3        | Trunk         |

### 2. 检查本地环境:`ruby`和`RubyGems`

  `CocoaPods`对`ruby` 有版本要求, 目前`CocoaPods`已经升级到`1.9.x`版本, 建议同步升级`ruby`版本 (非强制), 至少不低于`2.3.x`

- 检查当前系统下`ruby`版本, `$ ruby -v`  

  > `ruby 2.3.7p456 (2018-03-28 revision 63024) [universal.x86_64-darwin18]`

- 检查当前系统下的`RubyGems`版本  `$ gem -v`

   > `2.5.2.3`

### 3. 升级 `ruby` 和 `RubyGems`

  非必要但也很关键, `MacOS`自带`ruby`和`RubyGems`, 默认版本较低, 如果`gem`的版本号过低，安装`CocoaPods`可能会失败

- 更新`RubyGems`版本, 更新后通过确认下版本

     ```bash
     sudo gem update -n /usr/local/bin --system
    ```

- 安装 `rvm`, 通过`rvm`维护`ruby`版本

     ```bash
     // 安装rvm
     curl -L get.rvm.io | bash -s stable
     // 让环境立即生效
     source ~/.rvm/scripts/rvm
     // 检查确认rvm是否生效
     rvm -v
     ```  

     ![安装rvm](/uploads/40c14c64060147524175e81d45762b65/3.1-安装rvm.png)

- 更新`ruby`

     ```bash
     // 列出当前可安装的全部版本 (rvm list是查看当前已安装版本)
     rvm list known
     // 安装指定版本, 这里我指定了2.6.5这个版本, 并没有安装最新的2.7.x
     rvm install 2.6.5
     // 设置默认版本
     rvm use 2.1.4 --default
     ```

- 更换`RubyGems`源 (必须, 否则就是持续的失败, 因为国内被墙)  

  - RubyGems默认的源 `https://rubygems.org/`  
        国内使用的源有很多, 这里我推荐 `https://gems.ruby-china.com/`
  - 更换`RubyGems`源

      ```bash
      // 移除原默认的源
      gem sources -r http://rubygems.org/
      // 添加新源, 切记末尾的/不能缺少
      gem sources -a https://gems.ruby-china.com/
      // 检查下是否已经有且只有 https://gems.ruby-china.com/源
      gem sources
      ```

      ![安装rvm](/uploads/eeb0ea9b03561a39c62ef2f0bf581adb/3.2-检查RubyGems源.png)

### 4. 安装 `CocoaPods`  

- 提前安装好 `Xcode`, 安装 `CocoaPods`

   ```bash
   sudo gem install -n /usr/local/bin cocoapods
   ```

- 检查版本, 确认安装成功

   ```bash
   pod --version
   ```

### 5. 验证并使用 `CocoaPods`

`CocoaPods` 1.8.4以前的版本, 在`步骤4`的最后执行`pod setup`漫长等待成功后, 就可以正常使用了, 但在1.8.4版本开始做了重大调整

官方说明: [CDN as Default](http://blog.cocoapods.org/CocoaPods-1.8.0-beta/), 也可参照链接中视频使用介绍.

> 1. 通过 `Xcode` 新建并进入到项目路径
> 2. 执行 `$ pod init` , 新建 `PodFile` 文件
> 3. 编辑 `Podfile`后 , 执行 `$ pod install` 后等待完成依赖库的集成  

![验证Cocoapods](/uploads/9c03e797ff89cbc7ab98c174676576d5/3.3-验证Cocoapods.png)

- `trunk` 与 `master`的区别:  

  - `mater` 方式  
  
    指定的源为 `https://github.com/CocoaPods/Specs.git`  
    在安装环境的最后关键环节是`pod setup`, 会把 `CocoaPods` 仓库的整个 `master` 分支拉取到本地

  - `trunk`方式  

    指定的源为 `https://cdn.cocoapods.org/`  
    在安装环境的最后环节免去了漫长的`pod setup`过程

> 对比提升:  
> 安装环节: 提速最直观的就是在Cocoapods环境配置环节, 在有梯子的情况下, 至少也是需要半个多小时的等待过程  
> 使用环节: 因为不同的源, 所以在pod install环节的速度也有提升

## `CocoaPods` 基本使用

- 新建 `Xcode`项目, `CocoaPods` 服务于项目, 如果没有项目工程文件, 无法直接使用
- 进入工程目录,  `cd` 到`.xcodeproj` 项目文件同级目录, 执行`$ pod init` 后生成 `Podfile` 文件  
    ![Podfile所在路径](/uploads/491799cae149a54629188ad6e6b819f8/4.2-Podfile所在路径.png)
- 通过 `Vim` 或其他任意文本编辑工具打开 `Podfile` 文件 (如果使用 `VSCode` 注意保存)  

  - 去掉 `# platform :ios, '9.0'` 前面的`#`, 打开注释 , 同时设置与 `Xcode` 项目相同的最低版本号
  - 添加依赖库  
    ![添加依赖库](/uploads/8238d73495c23ad3bb0bc2c8c9c55dee/4.3-添加依赖库.png)
  - 安装依赖库, 执行 `$ pod install --verbose --no-repo-update`, 等待安装结束, 便完成了依赖库的安装  
    ![安装依赖库](/uploads/6289d552b3f474c2b06d123a7f9b27a5/4.4-安装依赖库.png)
  
## 典型问题

### 1. `ERROR: While executing gem ... (Errno::EPERM) ... Operation not permitted`

```bash
CIandCD-iOS:~ admin$ sudo gem update --system
Updating rubygems-update
Fetching: rubygems-update-3.1.4.gem (100%)
Successfully installed rubygems-update-3.1.4
Parsing documentation for rubygems-update-3.1.4
Installing ri documentation for rubygems-update-3.1.4
Installing darkfish documentation for rubygems-update-3.1.4
Done installing documentation for rubygems-update after 63 seconds
Parsing documentation for rubygems-update-3.1.4
Done installing documentation for rubygems-update after 0 seconds
Installing RubyGems 3.1.4
ERROR:  While executing gem ... (Errno::EPERM)
    Operation not permitted @ rb_sysopen - /System/Library/Frameworks/Ruby.framework/Versions/2.3/usr/bin/gem
```

从 [OS X El Capitan v10.11](https://developer.apple.com/library/archive/releasenotes/MacOSX/WhatsNewInOSX/Articles/MacOSX10_11.html) 开始加入了 `Rootless` 机制, 对权限进行了控制, 默认我们是无法对 `usr/bin/` 这个目录进行写权限的

> System Integrity Protection
>>A new security policy that applies to every running process, including privileged code and code that runs out of the sandbox. The policy extends additional protections to components on disk and at run-time, only allowing system binaries to be modified by the system installer and software updates. Code injection and runtime attachments to system binaries are no longer permitted.

在很多安装、更新环节都会遇到`ERROR:  While executing gem ... (Errno::EPERM)...Operation not permitted ...`的错误

解决方案:

- 修改目录权限

  - SIP 让 /usr/bin 只读了, 但是 /usr/local 是可读可写的, 将安装目录修改了.

  - 关闭SIP(这种方法非常不推荐, 相当于自己关了一层防火墙.)  

     ```bash
     csrutil disable
     reboot
     ```

这也是为什么在前面的环节, 我都是用来 `-n` 参数指定到了 `usr/local/bin`路径下

### 2. `[!] Unable to find a pod with name, author, summary, or description matching`

执行 `$ pod install` 或 `$ pod search xxx` 等命令报错:  

```bash
[!] Unable to find a pod with name, author, summary, or description matching
```

 多试几次 如果是 `master` 方式, 删除索引后再试

```bash
 rm -rf ~/Library/Caches/CocoaPods/search_index.json
 ```

## 彻底的卸载 `CocoaPods`

> 一般情况下需要卸载 `Cocoapods` 只需要执行 `$ sudo gem uninstall cocoapods` 即可  
但通常情况下我们很少会需要卸载 `CocoaPods` , 往往是在排查问题无效需要重新部署环境时才会需要这个环节  
下面是我在处理 `CocoaPods` 问题时尝试过最有效的清理方式

### 1. 移除pod组件

```bash
shenyj@ShenYj-MBP ~ % which pod
/usr/local/bin/pod
shenyj@ShenYj-MBP ~ % sudo rm -rf /usr/local/bin/pod
```

### 2. 移除 RubyGems 中的 Cocoapods程序包

> `$ sudo gem uninstall cocoapods`

### 3. 查看本地安装过的Cocoapods相关内容, 并彻底清除

> `$ gem list --local | grep cocoapods`

```bash
shenyj@ShenYj-MBP ~ % gem list --local | grep cocoapods
cocoapods-core (1.9.1)
cocoapods-deintegrate (1.0.4)
cocoapods-downloader (1.3.0)
cocoapods-plugins (1.0.0)
cocoapods-search (1.0.0)
cocoapods-stats (1.1.0)
cocoapods-trunk (1.4.1)
cocoapods-try (1.2.0)
```

### 4. 然后逐个删除

```bash
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-core
Successfully uninstalled cocoapods-core-1.9.1
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-deintegrate
Successfully uninstalled cocoapods-deintegrate-1.0.4
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-downloader
Successfully uninstalled cocoapods-downloader-1.3.0
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-plugins
Successfully uninstalled cocoapods-plugins-1.0.0
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-search
Successfully uninstalled cocoapods-search-1.0.0
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-stats
Successfully uninstalled cocoapods-stats-1.1.0
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-trunk
Successfully uninstalled cocoapods-trunk-1.4.1
shenyj@ShenYj-MBP ~ % sudo gem uninstall cocoapods-try
Successfully uninstalled cocoapods-try-1.2.0
```

