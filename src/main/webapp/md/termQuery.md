## termQuery
### 问题引出
构造数据
```java
    private static void createIndexWithContent() throws Exception {
		JestClient jestClient = JestExample.getJestClient();
		Product product = new Product(1,"XHDK-A-1293-#fJ3",10);
		Product product1 = new Product(2,"KDKE-B-9947-#kL5",20);
		Product product2 = new Product(3,"JODL-X-1937-#pV7",30);
		Index index1 = new Index.Builder(product).index("my_store").type("product").build();
		Index index2 = new Index.Builder(product1).index("my_store").type("product").build();
		Index index3 = new Index.Builder(product2).index("my_store").type("product").build();
		JestResult jestResult1 = jestClient.execute(index1);
		System.out.println(jestResult1.getJsonString());
		JestResult jestResult2 = jestClient.execute(index2);
		System.out.println(jestResult2.getJsonString());
		JestResult jestResult3 = jestClient.execute(index3);
		System.out.println(jestResult3.getJsonString());
	}
```
#### 成功的查询
boolean查询
```java
    public static SearchResult booleanQuery() throws IOException {
		/**
		 * 使用queryBuilder限定条件来实现where查询
		 */
		JestClient jestClient = JestExample.getJestClient();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		QueryBuilder queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("price", "20"));
		searchSourceBuilder.query(queryBuilder);

		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex("my_store")
				.build();
		SearchResult result = jestClient.execute(search);
		return result;
	}
```
执行结果:
```
{"took":5,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":1,"max_score":1.0,"hits":[{"_index":"my_store","_type":"product","_id":"2","_score":1.0,"_source":{"id":2,"productId":"KDKE-B-9947-#kL5","price":20}}]}}
```
成功的返回了数据
#### 失败的查询
在将查询term修改为查询productId时:
```java
	QueryBuilder queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("productId", "XHDK-A-1293-#fJ3"));
```
后，执行结果为:
```
{"took":2,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":0,"max_score":null,"hits":[]}}
```
没有找到数据，但是数据实际上在es服务器中是存在的
### 分析
主要对elasticsearch的termQuery进行分析即可找到以上代码查询不到值的原因

> 我们没有得到任何结果值！为什么呢？问题不在于 term 查询；而在于数据被索引的方式。如果我们使用 analyze API，我们可以看到 productId 被分解成短小的表征：

```
http://localhost:9200/my_store/_analyze?field=productID&text=XHDK-A-1293-#fJ3
```
返回:
```json
    {
	"tokens": [{
		"token": "xhdk-a-1293-",
		"start_offset": 0,
		"end_offset": 12,
		"type": "LETTER",
		"position": 1
	}, {
		"token": "xhdk",
		"start_offset": 0,
		"end_offset": 4,
		"type": "ENGLISH",
		"position": 2
	}, {
		"token": "a",
		"start_offset": 5,
		"end_offset": 6,
		"type": "ENGLISH",
		"position": 3
	}, {
		"token": "1293",
		"start_offset": 7,
		"end_offset": 11,
		"type": "ARABIC",
		"position": 4
	}]
}
```
这里有一些要点：
- 我们得到了四个分开的标记，而不是一个完整的标记来表示 UPC。
- 所有的字符都被转为了小写。
- 我们失去了连字符和 # 符号。

所以当我们用 XHDK-A-1293-#fJ3 来查找时，**得不到任何结果**，因为这个标记不在我们的倒排索引中。相反，那里有上面列出的四个标记。

### 解决方案:
为了避免这种情况发生，我们需要通过设置这个字段为 not_analyzed 来告诉 Elasticsearch 它包含一个准确值。

1. 必须首先删除索引，因为我们不能修改已经存在的映射。
2. 删除后，我们可以用自定义的映射来创建它。
3. 这里我们明确表示不希望 productID 被分析。

重新生成mapping，测试代码
productID 字段没有经过分析，term过滤器也没有执行分析，所以这条查询找到了准确匹配的值，如期返回值。

### 总结
termQuery这个过滤器旨在处理数字，布尔值，日期，和文本。<br/>
过滤器不会执行计分和计算相关性。分值由 match_all 查询产生，所有文档一视同仁，所有每个结果的分值都是 1<br/>
对于准确值，你需要使用过滤器。过滤器的重要性在于它们非常的快。它们不计算相关性（避过所有计分阶段）而且很容易被缓存。



