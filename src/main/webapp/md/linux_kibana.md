# linux 安装kibana
## 官网下载
- 服务器:linux
- elasticsearch版本:elasticsearch-6.4.0
- kibana版本为:6.4.0
- 官网下载地址:https://www.elastic.co/cn/downloads/kibana
## 上传到linux服务器解压等命令操作
```
rz 
tar -zxvf kibana-6.4.0-linux-x86_64.tar.gz
```
## 修改kibana.yml配置文件，新增如下配置：
```
server.host: "0.0.0.0"
elasticsearch.url: "http://your ip:9200"
```
## 启动kibana
```
bin/kibana
```
