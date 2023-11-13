---
layout: post
title:  "友善的接口文档【已废弃】"
date:   2020-08-18 8:18:18 +0800
categories: dev
---

RAML是RESTful API Modeling Language的全称，出生就是为了标记REST格式的接口。我们的团队也已经引进了好多年了，但一直没有标准化的文档，大家完全按自己的意愿进行书写。本文档对所书写的格式进一步的进行了规范。RAML继承于YAML，YAML要求的规范RAML也要遵守。

1. 文档对大小写敏感
2. 使用缩进表示层级关系
3. 缩进不允许使用TAB,只允许使用空格，我们统一要求缩进单位为2个空格
4. `#`表示注释，没有实际功能
5. raml支持三种数据格式
    1. 对象：`title: hello`
    2. 数组：`protocols: [ HTTP, HTTPS ]`

## 文档格式

### 文档头

``` yaml
#第一行内容标记当前文档的格式以及使用的版本，统一要求使用1.0版本。这个也是yaml的格式标准规范
#%RAML 1.0
title: stage billing api # 标题说明该文档的主要功能，比如我们的示例是社区的计费接口文档
description: 新版本社区的支付部门接口文档 # 可选参数。对于title的补充
version: v1.0 # 可选参数。说明该接口的版本号，我们的接口版本从1.0开始，
baseUri: https://api.gyyx.cn/pay # 可选参数。描述整个文档请求的URL基础域名和协议
mediaType: application/json # 可选参数。描述整个文档的返回参数格式
```

### 文档内容

``` yaml
# 所有内容以根请求节点开始
/game:
  /serverlist/{gameid}/: # 此为下一级节点
    description: 获取指定游戏的服务器列表 # 对于接口的说明，必须项
    get: # 请求的方法，可以为get,post,put,delete
      uriParameters: # 为URL中的参数提供说明
        gameid:
          type: integer
          example: 2
      headers: # 需要提交http header时的设置
        version:
          type: string
          example: v1.0
      queryParameters: # 查询的参数
        serverstatus: # 参数名
          type: string # 数据类型，支持的格式string,number,integer,date,boolean,file
          example: open # 示例值
      responses: # 应答结果节点
        200: # 状态码
          body: # 主体
            example: | # 返回结果的示例部分,如需要多行示例，可以用空格+|然后从下一行开始书写返回的示例
              {
                "success": true,
                "data": [{"code":1,"serverName":"北京古都","netTypeCode":1}]
              }
        404: # 可以设置多个状态码
          body:
            example: |
              {
                "success": false,
                "data": []
              }
```

## 模拟服务器

当实际项目开发时经常会遇到前端开发进度比服务器快，前端开发完接口无法调试的问题。接下来我们就可以使用postman导入该raml文档进行相应测试。

1. 首先打开postman，并点击左上角的Import。并选择刚刚生成的[demo.raml](/static/2020-08/demo.raml)文件，确认导入的提示信息正确，然后点击弹出层的确认导入按钮
    ![raml-1](/static/2020-08/raml-1.png)
2. 在导入成功页面上点击Develop标签准备开启Mock服务器，然后点击添加新的mock服务器
    ![raml-2](/static/2020-08/raml-2.png)
3. 开始创建mock服务器时，选择使用现在的collection。并选择刚刚导入的工作区文件。
    ![raml-3](/static/2020-08/raml-3.png)
4. 在导入成功后，会得到一个临时的测试用mock地址，接下来我们的前端开发就可以使用此域名进行接口联调了。为了验证导入成功，我们可以用postman做一下接口测试，首先修改工作区的baseUrl参数地址为刚刚创建成功的地址
    ![raml-4](/static/2020-08/raml-4.png)
5. 点击postman导入的刚刚的获取服务器列表方法，点击send进行测试
    ![raml-5](/static/2020-08/raml-5.png)
