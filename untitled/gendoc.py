import base64

from flask import Flask, redirect

app = Flask(__name__)

license = """{
    "status": "true",
    "version": "5.2.2"
}"""

config_properties = """
# 文档系统登录的账号
doc.username=wangjin
# 文档系统登录的密码
doc.password=
# 文档系统项目id 打开项目后,浏览器地址栏可以看到项目的ID
doc.project_id=
# 文档系统分组的id 打开项目后,点击分组,浏览器地址栏能看到分组的ID
doc.group_id=
# 是否动态根据请求路径修改接口 默认为false
doc.update=false
"""


plugin_info = """<span style="font-family: SimHei;"><span style="font-size:14px;"><plugins>
    <plugin id="com.wangjin.Generate.WeimobDoc" url="https://file.javanet123.com/gen-doc-plugin-3.2.2-SNAPSHOT.zip" version="3.2.2"/>
</plugins></span></span>
"""

@app.route('/', methods=['GET'])
def _index():
    return redirect("https://javanet123.com")


@app.route('/resp', methods=['GET'])
def resp():
    return plugin_info


@app.route('/license', methods=['GET'])
def _license():
    return base64.b64encode(license.encode('utf-8')).decode("utf-8")


@app.route('/config_properties', methods=['GET'])
def _config_properties():
    return base64.b64encode(config_properties.encode('utf-8')).decode("utf-8")


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)
