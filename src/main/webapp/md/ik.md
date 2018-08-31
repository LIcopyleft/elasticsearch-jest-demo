## 1、elasticsearch版本：1.4.1
## 2、分词插件github网址：[elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)
## 3、选择适合自己的版本
--------

IK version | ES version
-----------|-----------
master | 6.x -> master
6.3.0| 6.3.0
6.2.4| 6.2.4
6.1.3| 6.1.3
5.6.8| 5.6.8
5.5.3| 5.5.3
5.4.3| 5.4.3
5.3.3| 5.3.3
5.2.2| 5.2.2
5.1.2| 5.1.2
1.10.6 | 2.4.6
1.9.5 | 2.3.5
1.8.1 | 2.2.1
1.7.0 | 2.1.1
1.5.0 | 2.0.0
1.2.6 | 1.0.0
1.2.5 | 0.90.x
1.1.3 | 0.20.x
1.0.0 | 0.16.2 -> 0.19.0

1.4.1版本对应的分词版本为：1.2.6，然后在[releases](https://github.com/medcl/elasticsearch-analysis-ik/releases)中下载对应的版本，并解压。

## 4、编译生成分词jar包：elasticsearch-analysis-ik-1.2.6.jar

## 5、将打包得到的jar文件elasticsearch-analysis-ik-1.2.6.jar复制到ES安装目录的lib目录下。

## 6、将解压目录文件中config/ik文件夹复制到ES安装目录config文件夹下

## 7、在ES的配置文件config/elasticsearch.yml中增加ik的配置，在最后增加：
```yml
index:
  analysis:                   
    analyzer:      
      ik:
          alias: [ik_analyzer]
          type: org.elasticsearch.index.analysis.IkAnalyzerProvider
      ik_max_word:
          type: ik
          use_smart: false
      ik_smart:
          type: ik
          use_smart: true

```
## 8、配置完成，重启elasticsearch服务器

## 9、测试
- 输入网址：http://localhost:9200/news/_analyze?pretty&analyzer=ik&text=中国矿业大学
- 返回结果：
```json
{
  "tokens" : [ {
    "token" : "中国矿业",
    "start_offset" : 0,
    "end_offset" : 4,
    "type" : "CN_WORD",
    "position" : 1
  }, {
    "token" : "中国",
    "start_offset" : 0,
    "end_offset" : 2,
    "type" : "CN_WORD",
    "position" : 2
  }, {
    "token" : "矿业大学",
    "start_offset" : 2,
    "end_offset" : 6,
    "type" : "CN_WORD",
    "position" : 3
  }, {
    "token" : "矿业",
    "start_offset" : 2,
    "end_offset" : 4,
    "type" : "CN_WORD",
    "position" : 4
  }, {
    "token" : "矿",
    "start_offset" : 2,
    "end_offset" : 3,
    "type" : "CN_WORD",
    "position" : 5
  }, {
    "token" : "业大",
    "start_offset" : 3,
    "end_offset" : 5,
    "type" : "CN_WORD",
    "position" : 6
  }, {
    "token" : "大学",
    "start_offset" : 4,
    "end_offset" : 6,
    "type" : "CN_WORD",
    "position" : 7
  } ]
}

```
- 备注：news为我已经在es服务器中生成的索引名称，这里如果不加索引名称会出错。比如：
输入网址：http://localhost:9200/_analyze?pretty&analyzer=ik&text=中国矿业大学
- 返回结果：
```json
{
  "error" : "ElasticsearchIllegalArgumentException[failed to find analyzer [ik]]",
  "status" : 400
}
```