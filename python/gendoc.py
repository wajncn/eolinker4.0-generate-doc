import base64

from flask import Flask, redirect, Response, request

app = Flask(__name__)

doc_config_properties = """
# v5.2.1
# https://javanet123.com/archives/weimob
# (必填)文档系统登录的账号
doc.username=wangjin
# (必填)文档系统登录的密码
doc.password=
# (必填)文档系统项目id 打开项目后,浏览器地址栏可以看到项目的ID
doc.project_id=

# 以下为个性化设置
# (非必填)文档系统分组的id 打开项目后,点击分组,浏览器地址栏能看到分组的ID. 如果该项没有配置,则在生成时会出现分组下拉框
doc.group_id=
# (非必填)是否动态根据分组id和请求路径修改接口 默认为false
#        该项配置只对通过文件生成文档有效. 手动通过选择代码块生成会默认更新文档
doc.update=false
# (非必填)忽略解析的return对象,多个以英文逗号分割 如:RequestResult,JSONResult
doc.ignore_result=
"""

plugin_info = """<?xml version="1.0" encoding="UTF-8"?>
<plugins>
    <plugin id="com.wangjin.Generate.WeimobDoc" url="https://file.javanet123.com/gen-doc-plugin-5.2.zip" version="5.2">
    <idea-version since-build="145.258"/>
    <id>com.wangjin.Generate.WeimobDoc</id>
    <name>Generate EolinkerDoc</name>
    <vendor email="wajncn@gmail.com" url="https://javanet123.com/">王进</vendor>
    <description>
            <![CDATA[
解析Controller到接口微盟文档系统<br>
    Click Tools -> "Generate EolinkerDoc"<br>
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
 1.正式改名为:Generate EolinkerDoc
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
Release v5.2.1版本
 1.fix: 已知问题
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


@app.route('/config_properties', methods=['GET'])
def _config_properties():
    return base64.b64encode(doc_config_properties.encode('utf-8')).decode("utf-8")

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)
