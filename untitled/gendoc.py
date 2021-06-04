import base64

from flask import Flask, redirect, Response, request

app = Flask(__name__)

license = """{
    "status": "true",
    "version": "5.2"
}"""

doc_config_properties = """
# https://javanet123.com/archives/weimob
# (必填)文档系统登录的账号
doc.username=wangjin
# (必填)文档系统登录的密码
doc.password=
# (必填)文档系统项目id 打开项目后,浏览器地址栏可以看到项目的ID
doc.project_id=
# (非必填)文档系统分组的id 打开项目后,点击分组,浏览器地址栏能看到分组的ID. 如果该项没有配置,则在生成时会出现分组下拉框
doc.group_id=
# (非必填)是否动态根据分组id和请求路径修改接口 默认为false
#        该项配置只对通过文件生成文档有效. 手动通过选择代码块生成会默认更新文档
doc.update=false
"""

plugin_info = """<?xml version="1.0" encoding="UTF-8"?>
<plugins>
    <plugin id="com.wangjin.Generate.WeimobDoc" url="https://file.javanet123.com/gen-doc-plugin-5.2.zip" version="5.2">
    <idea-version since-build="145.258"/>
    <id>com.wangjin.Generate.WeimobDoc</id>
    <name>Generate WeimobDoc</name>
    <vendor email="wajncn@gmail.com" url="https://javanet123.com/">王进</vendor>
    <description>
            <![CDATA[
解析Controller到接口微盟文档系统<br>
    Click Tools -> "Generate WeimobDoc"<br>
            该工具由第一交付中心第四部门提供<br>
    <br>
<a href="https://javanet123.com/archives/weimob">Need Help?<a/><br>
]]>
   </description>
   <change-notes>
            <![CDATA[
<pre>
Release v1.0
 1.feat: 解析Controller到接口文档系统
Release v1.1
 1.fix: 已知问题
Release v2.0
 1.fix: 已知问题
 2.refactor: 优化解析性能
Release v3.0版本
 1.feat: 支持解析分页参数
 2.feat: 取消默认动态修改文档功能,可通过配置文件来控制
Release v3.1版本
 1.正式改名为:Generate WeimobDoc
 2.feat: 支持选中方法块更新文档,必须包含[@*Mapping]
Release v4.0版本
 1.feat: 简化配置文件,操作步骤
 2.feat: 新增分组下拉框.如果配置了分组id,则不会出现分组下拉框
Release v4.1版本
 1.feat: 支持选择多个方法块
 2.feat: 选择代码块,系统会默认更新文档
Release v4.2版本 
 1.feat: 支持解析枚举类
 2.feat: 文档支持查看按钮
Release v4.3版本
 1.fix: 已知问题
Release v5.0版本
 1.refactor: 使用多线程解析文档,提高性能
 2.fix: 已知问题
 3.feat: 支持新框架
Release v5.1版本
 1.fix: windows系统解析异常问题
Release v5.2版本
 1.fix: 对部分非标准性写法进行容错
</pre>
]]>
    </change-notes>
    </plugin>
    
    
    <plugin id="javanet123.ide-eval-resetter"
		url="https://file.javanet123.com/ide-eval-resetter-2.1.14-d2fedb86.zip" version="2.1.14">
		<idea-version since-build="145.258" />
		<name>IDEA Reset</name>
		<vendor url="https://javanet123.com/">javanet123.com</vendor>
		<rating>5</rating>
		<description>
			<![CDATA[
From the zhile.io<br>
I can reset your IDE eval information.<br>
<em>Click "Help" menu and select "Eval Reset"</em><br><br>
<p>
    <a href="https://javanet123.com/archives/jetbrains" target="_blank">Need Help?</a>
</p>
]]>
		</description>
		<change-notes>
			<![CDATA[
<pre> 
Release v2.1.14
  1. fix minor exceptions
Release v2.1.13
  1. fix error notification
Release v2.1.12
  1. fix disable plugins
Release v2.1.11
  1. fix for block list: https://plugins.jetbrains.com/files/brokenPlugins.json
Release v2.1.10
  1. update welcome menu for 2020.3.1
Release v2.1.9
  1. fixed for "rider for unreal engine"
Release v2.1.8
  1. fixed the issue of resetting market plugins for genuine users
Release v2.1.7
  1. add help page link
Release v2.1.6
  1. fix the pop-up of license window
Release v2.1.5
  1. fix memory leak
Release v2.1.4
  1. fix reference
Release v2.1.3
  1. add version in UI
Release v2.1.2
  1. fix third party plugins switch
Release v2.1.1
  1. add ide plugin marketplace mechanism
Release v2.1.0
  1. add option "Auto reset before per restart"
Release v2.0.4
  1. fix plugins reset
  2. reset more gracefully
Release v2.0.3
  1. more friendly "Reload" icon
Release v2.0.2
  1. sync prefs manually
Release v2.0.1
  1. add option: Reset Automatically
Release v2.0.0
  1. add ui
  2. more stable and accurate
Release v1.0.5
  1. update for 2020.2.x
Release v1.0.4
  1. reset completely
Release v1.0.3
  1. bug fix
Release v1.0.2
  1. compatibility fix
</pre>
]]>
		</change-notes>
	</plugin>
</plugins>
"""


@app.route('/', methods=['GET'])
def _index():
    return redirect("https://javanet123.com")


@app.route('/resp/updatePlugins.xml', methods=['GET'])
def _plug():
    if "IDEA" not in request.headers.get("user-agent"):
        return redirect("https://javanet123.com/archives/jetbrains")
    r = Response(response=plugin_info, status=200, mimetype="application/xml")
    r.headers["Content-Type"] = "text/xml; charset=utf-8"
    return r


@app.route('/resp', methods=['GET'])
def _resp():
    if "IDEA" in request.headers.get("user-agent"):
        return _plug()
    else:
        return redirect("https://javanet123.com/archives/jetbrains")


@app.route('/license', methods=['GET'])
def _license():
    return base64.b64encode(license.encode('utf-8')).decode("utf-8")


@app.route('/config_properties', methods=['GET'])
def _config_properties():
    return base64.b64encode(doc_config_properties.encode('utf-8')).decode("utf-8")


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)
