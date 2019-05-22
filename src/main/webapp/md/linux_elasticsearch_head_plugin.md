## linux服务器安装elasticsearch的head插件
1.elasticsearch版本:elasticsearch-6.4.0
1. 下载head插件:https://github.com/mobz/elasticsearch-head
2. 上传到/usr/local/
```
rz
```
3. 解压
```
unzip elasticsearch-head-master.zip
```
4. 安装node和Grunt环境
- 下载node:   https://npm.taobao.org/mirrors/node/latest-v4.x/node-v4.4.7-linux-x64.tar.gz
- 解压并配置环境变量vim /etc/profile
```
export NODE_HOME=/usr/local/node-v4.4.7-linux-x64
export PATH=$PATH:$NODE_HOME/bin
export NODE_PATH=$NODE_HOME/lib/node_modules
```
-  刷新环境变量：source /etc/profile
-  安装grunt命令行工具grunt-cli
```
npm install -g grunt-cli
```
- 安装grunt及其插件
```
npm install grunt --save-dev
```
- 验证安装是否成功:
```
grunt -version
```
5. 重命名为elasticsearch-head
6. 配置连接信息：进入elasticsearch-head/，运行vi Gruntfile.js来修复如下配置（ip 和 端口号）
```
	connect: {
			server: {
				options: {
					port: 9100,
					hostname: "0.0.0.0",
					base: '.',
					keepalive: true
				}
			}
		}
```
7. 保存后配置防火墙，开放9100端口，在控制台输入
```
firewall-cmd --zone=public --add-port=9100/tcp --permanent

firewall-cmd --reload
```
8. 修改elasticsearch服务器的elasticsearch.yml文件，使得head插件可以连接到es服务器，增加以下两个配置（跨域访问）
```
http.cors.enabled: true
http.cors.allow-origin: "*"
```
9. 后台启动grunt server 来启动head服务
```
nohup grunt server &exit
```
10. 如果想关闭head插件，使用Linux查找进程命令：
```
ps aux|grep head

结束进程：
kill 进程号
```

输出以下信息证明启动成功
```
Running "connect:server" (connect) task
Waiting forever...
Started connect web server on http://localhost:9100
```
