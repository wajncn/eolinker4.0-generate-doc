import base64

from flask import Flask, redirect

app = Flask(__name__)

license = """{
    "status": "true",
    "version": "5.2.2"
}"""

config_properties = """
# https://javanet123.com/archives/weimob
# (必填)文档系统登录的账号
doc.username=wangjin
# (必填)文档系统登录的密码
doc.password=
# (必填)文档系统项目id 打开项目后,浏览器地址栏可以看到项目的ID
doc.project_id=
# (非必填)文档系统分组的id 打开项目后,点击分组,浏览器地址栏能看到分组的ID. 如果该项没有配置,则在生成时会出现下拉框
doc.group_id=
# (非必填)是否动态根据分组id和请求路径修改接口 默认为false
doc.update=false
"""

plugin_info = """<?xml version="1.0" encoding="UTF-8"?>
<plugins>
    <plugin id="com.wangjin.Generate.WeimobDoc" url="https://file.javanet123.com/gen-doc-plugin-4.3.zip" version="4.3">
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
 1.解析controller到接口文档系统
Release v2.0
 1.fix bug
Release v3.2
 1.fix bug
 2.优化解析性能
Release 3.2.1版本
 1.支持解析分页参数
 2.取消默认动态修改文档功能,可通过配置文件来控制
Release 3.2.2版本
 1.正式改名为:Generate WeimobDoc
 2.支持选中方法同步文档,必须包含[@*Mapping]
Release 4.0版本
 1.简化配置文件,操作步骤.
 2.新增下拉框分组,无需配置分组id啦.
Release v4.1版本 
 1.选择了代码块支持动态更新文档
Release v4.2版本 
 1.支持解析枚举类
 2.文档支持查看按钮
Release v4.3版本 
 1.fix 已知问题
</pre>
]]>
    </change-notes>
    </plugin>
</plugins>
"""


@app.route('/', methods=['GET'])
def _index():
    return redirect("https://javanet123.com")


@app.route('/resp', methods=['GET'])
def resp():
    return plugin_info


@app.route('/resp/updatePlugins.xml', methods=['GET'])
def updatePlugins():
    return plugin_info


@app.route('/license', methods=['GET'])
def _license():
    return base64.b64encode(license.encode('utf-8')).decode("utf-8")


@app.route('/config_properties', methods=['GET'])
def _config_properties():
    return base64.b64encode(config_properties.encode('utf-8')).decode("utf-8")


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)
