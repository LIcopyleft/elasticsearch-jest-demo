## Linux服务器安装Elasticsearch
## 安装版本:6.4.0
## 解压安装Elasticsearch
1. 下载[elasticsearch](https://www.elastic.co/downloads/elasticsearch)
2. 上传到Linux服务器(上传到了/usr/local下)
    ```
    rz
    ```
3. 解压到当前目录
    ```
    tar -zxvf elasticsearch-6.4.0.tar.gz
    ```
## 修改配置文件
1. 进入/usr/local/elasticsearch/config目录，使用vi编辑器
```
vi elasticsearch.yml
```

2. 具体配置及服务器启动参考
https://blog.csdn.net/y472360651/article/details/78670240?locationnum=8&fps=1

## 遇到以下错误的排查
### 问题一:
```
can not run elasticsearch as root
```
解决方案:https://blog.csdn.net/mengfei86/article/details/51210093

### 问题二：服务器上es成功启动，但是通过浏览器ip+端口的方式无法访问
排查方式：
1. elasticsearch.yml中配置network.host的内容改本机的ip
2. 防火墙的问题
可能自己服务器的防火墙阻止了访问，于是做了一个尝试：
```
service firewalld stop
```
关闭了防火墙，然后发现这个时候从网页访问就没问题了。<br/>
还是打算开启防火墙，只开放指定端口9200，可以采用如下命令：
```
firewall-cmd --zone=public --add-port=9200/tcp -permanent
```
然后又想了一下，只打算对指定IP开放端口，可以采取如下命令：
```
firewall-cmd --permanent --add-rich-rule 'rule family=ipv4 source address=192.168.0.1/2 port port=80 protocol=tcp accept'
```


