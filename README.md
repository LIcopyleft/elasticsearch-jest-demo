# elasticsearch rest api 学习记录

> 注：本文档中demo是基于[elasticsearch-jest-example](https://github.com/ameizi/elasticsearch-jest-example)
的个人的学习记录

> elasticsearch版本：1.4.1

SpringBoot版本的参考项目：[springboot-elasticsearch](https://github.com/suxiongwei/springboot-elasticsearch)

## 学习记录
1. [Elasticsearch安装中文分词插件ik](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/ik.md)
2. [Elasticsearch创建mapping时指定analyzer分词器类型](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/analyzer.md)
3. [WebMagic爬取数据到作为搜索数据来源](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/webmagic.md)
4. [[转载] 死磕 Elasticsearch 方法论：普通程序员高效精进的 10 大狠招！](https://mp.weixin.qq.com/s/stC_xMP1n3aQ-0ZNAc3eQA)
5. [ELK Stack 认知](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/ELKStack.md)
6. [Elasticsearch相关基础概念](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/es_base.md)
7. [全文检索](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/FullTextSearch.md)
8. [ElasticSearch和kibana版本对应关系](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/KibanaForEsVersion.md)
9. [termQuery](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/termQuery.md)
10. [[转载]搜索引擎-倒排索引基础知识](https://blog.csdn.net/hguisu/article/details/7962350)
11. [Linux服务器安装Java](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/linux_java.md)
12. [Linux服务器安装Elasticsearch](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/linux_elasticsearch.md)
13. [linux 安装Git及简单配置](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/linux_git.md)
14. [linux服务器安装elasticsearch的head插件](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/linux_elasticsearch_head_plugin.md)
15. [linux 安装kibana](https://github.com/suxiongwei/elasticsearch-jest-demo/tree/master/src/main/webapp/md/linux_kibana.md)


## 学习的博客社区
1. [铭毅天下](https://blog.csdn.net/laoyang360/article/category/6239824)
2. [中科院硕士_姚攀](https://me.csdn.net/napoay)

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
### Jest Client对Elasticsearch的操作
#### 创建索引(index)
```java
    public static String createIndex(String indices) throws IOException {
		JestClient jestClient = JestExample.getJestClient();
		//判断索引是否存在
		TypeExist indexExist = new TypeExist.Builder(indices).build();
		JestResult result = jestClient.execute(indexExist);
		System.out.println("index exist result " + result.getJsonString());
		Object indexFound = result.getValue("found");

		if (indexFound != null && indexFound.toString().equals("false")) {
			//index 不存在,创建 index
			System.out.println("index found == false");
			JestResult createIndexresult = jestClient.execute(new CreateIndex.Builder(indices).build());
			System.out.println("create index:"+createIndexresult.isSucceeded());
			if(createIndexresult.isSucceeded()) {
				return "ok";
			}else{
				return "create index fail";
			}
		}else{
			return "ok";
		}
	}
```
#### 创建映射(mapping)
```java
    public static String createMapping(String indices,String mappingType,String analyzer) throws IOException {
		JestClient jestClient = JestExample.getJestClient();
		String message = createIndex(indices);
		if(!message.equals("ok")){
			return "create index fail";
		}
		//判断mapping是否存在
		TypeExist typeExist = new TypeExist.Builder(indices).addType(mappingType).build();
		JestResult mappingResult = jestClient.execute(typeExist);
		Object mappingFound = mappingResult.getValue("found");
		if (mappingFound != null && mappingFound.toString().equals("false")) {
			//索引和mapping不存在可以添加
			System.out.println("mapping found == false");
			XContentBuilder builder = XContentFactory.jsonBuilder()
					.startObject()
					.startObject(indices)
					.startObject("properties")
					.startObject("id").field("type", "integer").field("store", "yes").endObject()
					.startObject("name").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
					.startObject("time").field("type", "date").field("store", "yes").endObject()
					.endObject()
					.endObject()
					.endObject();

			String mappingString = builder.string();
			//构造PutMapping
			PutMapping putMapping = new PutMapping.Builder(indices, mappingType, mappingString).build();
			JestResult maapingResult = jestClient.execute(putMapping);
			return maapingResult.getJsonString();
		}else {
			return "mapping existing";
		}
	}
```
#### 判断索引目录是否存在
```java
    /**
	 * 判断索引目录是否存在
	 * @throws Exception
	 */
	private static JestResult indicesExists(String index) throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		IndicesExists indicesExists = new IndicesExists.Builder(index).build();
		JestResult result = jestClient.execute(indicesExists);
		return result;
	}
```
存在的话返回:
```
{"ok" : true, "found" : true}
```
#### 关闭索引
```java
    /**
	 * 关闭索引
	 * @throws Exception
	 */
	private static JestResult closeIndex(String index) throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		CloseIndex closeIndex = new CloseIndex.Builder(index).build();
		JestResult result = jestClient.execute(closeIndex);
		return result;
	}
```
关闭成功返回:
```
{"acknowledged":true}
```
访问被关闭的索引:
```json
    {
      "error" : "IndexClosedException[[news] closed]",
      "status" : 403
    }
```
#### 打开索引
```java
    /**
	 * 打开索引
	 * @throws Exception
	 */
	private static JestResult openIndex(String index) throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		OpenIndex openIndex = new OpenIndex.Builder(index).build();
		JestResult result = jestClient.execute(openIndex);
		return result;
	}
```
#### 查看节点信息
```java
    /**
	 * 查看节点信息
	 * @throws Exception
	 */
	private static JestResult nodesInfo() throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		NodesInfo nodesInfo = new NodesInfo.Builder().build();
		JestResult result = jestClient.execute(nodesInfo);
		return result;
	}
```
#### 查看集群健康信息
```java
    /**
	 * 查看集群健康信息
	 * @throws Exception
	 */
	private static JestResult health() throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		Health health = new Health.Builder().build();
		JestResult result = jestClient.execute(health);
		return result;
	}
```
返回结果:
```json
{
	"cluster_name": "elasticsearch",
	"status": "yellow",
	"timed_out": false,
	"number_of_nodes": 1,
	"number_of_data_nodes": 1,
	"active_primary_shards": 25,
	"active_shards": 25,
	"relocating_shards": 0,
	"initializing_shards": 0,
	"unassigned_shards": 25
}
```
#### 查看节点状态
```java
    /**
	 * 节点状态
	 * @throws Exception
	 */
	private static JestResult nodesStats() throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		NodesStats nodesStats = new NodesStats.Builder().build();
		JestResult result = jestClient.execute(nodesStats);
		return result;
	}
```
