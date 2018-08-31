## 1、创建mapping
```java
    /**
     * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     * @param indices 索引名称；
     * @param mappingType 索引类型
     * @param analyzer 分词器类型
     * @throws Exception
     */
    public static void createMapping(String indices,String mappingType,String analyzer)throws Exception{
        Client client = createTransportClient();
        new XContentFactory();

        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indices)
                .startObject("properties")
                .startObject("id").field("type", "integer").field("store", "yes").endObject()
                .startObject("name").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("author").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("pubinfo").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("pubtime").field("type", "date").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("desc").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }
```
## 2、添加数据(要索引的内容)
```java
    /**
     * 创建Json字符串格式的索引
     */
    private static void creatJsonStringIndex() throws IOException {
        String json = "{" +
                "\"name\":\"中国矿业大学\"," +
                "\"author\":\"苏雄伟 \"," +
                "\"pubinfo\":\"人民邮电出版社 \"," +
                "\"pubtime\":\"2018-08-31\"," +
                "\"desc\":\"中国矿业大学（China University of Mining and Technology），简称“矿大”，坐落于有“五省通衢”之称的江苏省徐州市，是教育部直属的全国重点大学，教育部与江苏省人民政府、国家安全生产监督管理总局共建高校， [1]  是首批列入国家“211工程”、“985平台”、“111计划”和“卓越工程师教育计划”重点建设的高校、国家首批“双一流”世界一流学科建设高校、高水平行业特色大学优质资源共享联盟成员，也是国家大学生创新性实验计划、国家建设高水平大学公派研究生项目、海外高层次人才引进计划实施高校。 [2] \"" +
                "}";
        createIndex("book","book",json);
    }
```
## 3、创建索引
```java
    /**
	 * 创建索引
	 * @param index 索引名称
	 * @param type  索引type
	 * @param sourcecontent 要索引的内容
	 */
	public static void createIndex(String index,String type,String sourcecontent) throws IOException {
		Client client = createTransportClient();
		IndexResponse response = client.prepareIndex(index, type).setSource(sourcecontent).execute().actionGet();
		printIndexInfo(response);
	}
```
## 4、main方法测试
```java
/**
 * 1、生成索引：book、school
 * 2、修改creatJsonStringIndex中的参数，添加要索引的数据
 * 3、搜索测试
 */
public static void main(String[] args) throws Exception {
	    //创建mapping(指定不同的分词器)
        createMapping("book","book","standard");
        createMapping("school","school","ik");
        //添加数据(索引)
//        creatJsonStringIndex();
        //在book索引库(indices)中查询，book中的文本字段进行了[ik]分词
        matchQuery("book","name","徐州市铜山区的矿业大学");
//        //在book索引库(school)中查询，book中的文本字段进行了[standard]分词
        matchQuery("school","name","徐州市铜山区的矿业大学");
    }
```
