# 一键生成文档到Doc系统,无需手写.

## 本项目遵循Apache License

## eolinker4.0项目地址为: https://github.com/wajncn/eolinker4.0

## 最新版本

> `v1.0.0`

## 功能描述

> 1.支持`多选Controller`文件进行接口文档同步. <br/>
> 2.支持`无限嵌套`,`无限继承`解析.(`自引用`不会死循环,`递归深度为2`). <br/>
> 3.支持`动态更新`文档,默认为关闭状态,需要手动开启. <br/>
> 4.支持`选择代码块`进行部分接口文档同步. <br/>
> 5.支持`选择分组`生成文档.
----

## 安装方式

> 在Idea插件商店中搜索 `Generate EolinkerDoc`插件进行安装。

## 使用说明

> 首次使用程序会在项目根目录产生一个配置文件:[`doc.properties`]
> 配置完成后选择`Controller`(可多选)后点击`Tools`--->`Generate EolinkerDoc`
> 可选择方法进行单独生成文档(但必须包含`*Mapping`)



```properties
# https://github.com/wajncn/eolinker4.0-generate-doc

# (必填)文档系统的网关地址
doc.url=
# (必填)文档系统登录的账号
doc.username=
# (必填)文档系统登录的密码
doc.password=
# (必填)文档系统项目id 打开项目后,浏览器地址栏可以看到项目的ID
doc.project_id=

# 以下为个性化设置
# (非必填)文档系统分组的id 打开项目后,点击分组,浏览器地址栏能看到分组的ID. 如果该项没有配置,则在生成时会出现分组下拉框
doc.group_id=
# (非必填)是否动态根据分组id和请求路径修改接口 默认为false
#        该项配置只对通过文件生成文档有效. 手动通过选择代码块生成会默认更新文档
doc.update=
# (非必填)忽略解析的return对象,多个以英文逗号分割 如:RequestResult,JSONResult
doc.ignore_result=
```
如有`BUG`或`建议`,请提交issues或pr