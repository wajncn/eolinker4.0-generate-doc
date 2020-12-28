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
# 文档系统项目id 具体打开项目参考url的参数
doc.project_id=
# 文档系统分组的id 打开项目后,点击下分组,浏览器地址栏能看到分组的ID
doc.group_id=
"""


@app.route('/', methods=['GET'])
def _index():
    return redirect("https://javanet123.com")


@app.route('/license', methods=['GET'])
def _license():
    return base64.b64encode(license.encode('utf-8')).decode("utf-8")


@app.route('/config_properties', methods=['GET'])
def _config_properties():
    return base64.b64encode(config_properties.encode('utf-8')).decode("utf-8")


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)
