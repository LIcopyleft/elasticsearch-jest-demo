## ELK Stack 认知
ELK Stack 由最早期的最核心的 Elasticsearch（以下部分简称ES），集合 Logstash、Kibana、beats 等发展而来，形成 ELK Stack 体系。如下图所示。<br/>
![image](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/img/es.jpg)
## Elasticsearch 认知
Elasticsearch 为开源的、分布式、基于 Restful API、支持 PB 甚至更高数量级的搜索引擎工具。
相对于 MySQL，给出如下的对应关系表会更好理解<br/>
![image](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/img/es1.jpg)<br/>
从上表中可以看出：
1. MySQL 中的数据库（DataBase），等价于 ES 中的索引（Index）。
2. MySQL 中一个数据库下面有 N 张表（Table），等价于1个索引 Index 下面有 N 多类型（Type）。
3. MySQL 中一个数据库表（Table）下的数据由多行（Row）多列（column，属性）组成，等价于1个 Type 由多个文档（Document）和多 Field 组成
4. MySQL 中定义表结构、设定字段类型等价于 ES 中的 Mapping。举例说明，在一个关系型数据库里面，Schema 定义了表、每个表的字段，还有表和字段之间的关系。与之对应的，在 ES 中，Mapping定义索引下的 Type 的====字段处理规则====，即索引如何建立、索引类型、是否保存原始索引 JSON 文档、是否压缩原始 JSON 文档、是否需要分词处理、如何进行分词处理等。
5. MySQL 中的增 insert、删 delete、改 update、查 search 操作等价于 ES 中的增 PUT/POST、删 Delete、改 _update、查 GET。其中的修改指定条件的更新 update 等价于 ES 中的 update_by_query，指定条件的删除等价于 ES 中的 delete_by_query
6. MySQL 中的 group by、avg、sum 等函数类似于 ES 中的 Aggregations 的部分特性。
7. MySQL 中的去重 distinct 类似 ES 中的 cardinality 操作。
8. MySQL 中的数据迁移等价于 ES 中的 reindex 操作。 

以上，通过类比，能加快理解 Elasticsearch 的速度。

如下是传统的关系型数据库（如Oracle、MySQL）、非关系型的数据库（如 Mongo）所做不到的：

1.传统的关系型数据库虽然能支持类型“like 待检索词”模糊语句匹配，但无法进行全文检索（分词检索）。

这里的全文检索，举例如下。
"text"："公路局正在治理解放大道路面积水问题"，对于这段待检索的文字，经过细粒度分词后能得出如下的分词结果：
```
公路局、公路、路局、路、局正、正在、正、治理、治、理解、理、解放、解、放大、大道、大、道路、道、路面、路、面积、面、积水、积、水、问题
```
如果进行全文检索，是针对以上分词后的结果逐个进行匹配，并由得分的高低快速的返回匹配结果。

这点，传统数据库几乎不可能做到。

2.非关系型数据库 Mongo 虽能进行简单的全文检索，但对中文支持的不好、数据量大性能会有问题，这点是在实际应用中总结出的。

## Logstash 认知
可以把 Logstash 理解成流入、流出 Elasticsearch 的传送带。

支持：不同类型的数据或实施数据流经过 Logstash 写入 ES 或者从 ES 中读出写入文件或对应的实施数据流。<br/>
包括但不限于：
- 本地或远程文件；
- Kafka 实时数据流——核心插件有 logstashinputkafka/logstashoutputkafka；
- MySQL、Oracle 等关系型数据库——核心插件有 logstashinputjdbc/logstashouputjdbc；
- Mongo 非关系型数据库——核心插件有 logstashinputmongo/logstashoutputmongo；
- Redis 数据流；
- ……

## Kibana 认知
Kibana 是 ES 大数据的图形化展示工具。集成了 DSL 命令行查看、数据处理插件、继承了 x-pack（收费）安全管理插件等。

## Beats 认知
Beats 是一个开源的用来构建轻量级数据汇集的平台，可用于将各种类型的数据发送至 Elasticsearch 与 Logstash。

Beats目前有官方支持的多个子产品，如下：
- Packetbeat：用于监控局域网内服务器之间的网络流量信息；
- Filebeat：收集服务器上的日志信息——它是用来替代 Logstash Forwarder 的下一代 Logstash 收集器，是为了更快速稳定轻量低耗地进行收集工作，它可以很方便地与 Logstash 还有直接与 Elasticsearch 进行对接。
- 新推出的 Metricbeat，可以定期获取外部系统的监控指标信息。
- 除了以上三个核心产品外，还有：Winlogbeat(Windows事件日志轻量级工具)、Auditbeat(审计数据的轻量级工具)、Heartbeat(用于时间监控的轻量级工具)。 除此以外，你还可以非常方便的基于 libbeat 框架来构建你属于自己的专属 Beat。

## 小结
通过以上的介绍，我们对 ELK Stack 中的核心成员：Elasticsearch、Logstash、Kibana、Beats 是什么以及能干什么有了相对一致的认知。<br/>
以上内容来源于[死磕 Elasticsearch 方法论：普通程序员高效精进的 10 大狠招！](https://mp.weixin.qq.com/s/stC_xMP1n3aQ-0ZNAc3eQA)，自己稍作整理，便于查看。