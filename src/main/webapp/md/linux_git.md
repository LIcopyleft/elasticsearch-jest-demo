## 安装git
```
yum -y install git
```
## 配置SSHkey
```
ssh-keygen -t rsa -C "suxiongwei@xywy.com"
```
> ssh-keygen命令会提示您输入存储密钥对和密码的位置和文件名。提示输入位置和文件名时，可以按Enter键以使用默认值。
最好使用密码作为SSH密钥，但这不是必需的，您可以通过按Enter键跳过创建密码。

使用以下代码显示您的公钥
```
cat ~/.ssh/id_rsa.pub
```
## 部署密钥
拷贝id_rsa.pub的内容，在GitHub和GitLab的SSH Keys栏目添加key即可通过git访问仓库中的内容。