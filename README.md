# elasticsearch rest api 学习记录

> 注：本文档中demo是基于[elasticsearch-jest-example](https://github.com/ameizi/elasticsearch-jest-example)
的个人的学习记录

> elasticsearch版本：1.4.1

## 学习记录
1. [Elasticsearch安装中文分词插件ik](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/ik.md)
2. [Elasticsearch创建mapping时指定analyzer分词器类型](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/analyzer.md)
3. [webmagic爬取数据到作为搜索数据来源](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/webmagic.md)


### 集群健康查看

* http://127.0.0.1:9200/_cat/health?v

```
epoch      timestamp cluster       status node.total node.data shards pri relo init unassign pending_tasks 
1441940569 11:02:49  elasticsearch yellow          1         1      7   7    0    0        7             0 
```

* http://127.0.0.1:9200/_cat/nodes?v

```
host ip            heap.percent ram.percent load node.role master name     
acer 169.254.9.202           32          52      d         *      Mys-Tech
```

### 列出所有的indices

* http://127.0.0.1:9200/_cat/indices?v

```
health status index              pri rep docs.count docs.deleted store.size pri.store.size 
yellow open   .marvel-2015.09.11   1   1       3233            0     10.5mb         10.5mb 
yellow open   .marvel-2015.09.10   1   1       1996            0      3.9mb          3.9mb 
yellow open   news                 5   1       3455            0     17.8mb         17.8mb 
```

### 创建索引

使用`PUT`请求创建一个countries的索引

```
curl -XPUT http://127.0.0.1:9200/countries?pretty
```

输出:

```
{
   "acknowledged": true
}
```

查看索引列表

```
curl -XGET http://127.0.0.1:9200/college/_mapping?pretty
```

输出:

```
health status index              pri rep docs.count docs.deleted store.size pri.store.size 
yellow open   countries              5   1          0            0       575b           575b 
yellow open   .marvel-2015.09.11   1   1       3436            0     11.4mb         11.4mb 
yellow open   .marvel-2015.09.10   1   1       1996            0      3.9mb          3.9mb 
yellow open   news                 5   1       3455            0     17.8mb         17.8mb 
```

### 查看mapping
```
curl -XGET http://127.0.0.1:9200/_cat/indices?v
```
输出:
```json
{
  "college" : {
    "mappings" : {
      "college" : {
        "properties" : {
          "city" : {
            "type" : "string",
            "store" : true,
            "analyzer" : "ik"
          },
          "desc" : {
            "type" : "string",
            "store" : true,
            "analyzer" : "ik"
          },
          "id" : {
            "type" : "integer",
            "store" : true
          },
          "name" : {
            "type" : "string",
            "store" : true,
            "analyzer" : "ik"
          }
        }
      }
    }
  }
}

```

