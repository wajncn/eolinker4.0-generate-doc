# 一键生成文档到Doc系统,无需手写.
## 最新版本
> `v5.2`
> 插件使用`PHP`编写，毕竟`PHP`是世界上最好的编程语言！
## 功能描述

>1.支持`多选Controller`文件进行接口文档同步. <br/>
>2.支持`无限嵌套`,`无限继承`解析.(`自引用`不会死循环,`递归深度为2`). <br/>
>3.支持`动态更新`文档,默认为关闭状态,需要手动开启. <br/>
>4.支持`选择代码块`进行部分接口文档同步. <br/>
>5.支持`选择分组`生成文档.
----
## 安装方式
### 1. 安装方式一(强烈推荐,会有新版本提示)
>在`Settings/Preferences... -> Plugins  -> Manage Plugin Repos...` 内手动添加第三方插件仓库地址：`https://javanet123.com/gendoc/resp`
>搜索：`Generate WeimobDoc`插件进行安装。如果搜索不到请注意是否做好了上一步？网络是否通畅？
>插件会提示安装成功。

### 2. 安装方式二
>点击这个[链接(V5.2)](https://file.javanet123.com/gen-doc-plugin-5.2.zip)下载插件的zip包
>通常可以直接把zip包拖进IDE的窗口来进行插件的安装。如果无法拖动安装，你可以在`Settings/Preferences... -> Plugins` 里手动安装插件（`Install Plugin From Disk...`）
>插件会提示安装成功。

----

![image.png](https://javanet123.com/upload/2021/01/image-37eca205d89d435b912e3b122a338935.png)

## 使用说明
>首次使用程序会在项目根目录产生一个配置文件:[`doc.properties`]
>配置完成后选择`Controller`(可多选)后点击`Tools`--->`Generate WeimobDoc`
>可选择方法进行单独生成文档(但必须包含`*Mapping`)

----

如有`BUG`或`建议`,请在评论区`留言`.