## Linux服务器安装Java
1. 下载[jdk-8u181-linux-x64.tar.gz](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. 上传到Linux服务器
    ```
    rz
    ```
3. 解压到当前目录
    ```
    tar -zxvf jdk-8u181-linux-x64.tar.gz
    ```
4. 配置环境变量
    ```
    vi /etc/profile
    ```
    在末尾添加如下代码：
    ```
    export JAVA_HOME=/usr/local/jdk1.8.0_181
    export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
    export PATH=$PATH:$JAVA_HOME/bin
    ```
5. 保存并退出编辑器，使用如下命令使环境变量生效
    ```
    source /etc/profile
    ```
6. 验证安装是否成功
    ```
    java -version
    ```
    返回以下信息证明安装成功
    ```
    java version "1.8.0_181"
    Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
    Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
    ```