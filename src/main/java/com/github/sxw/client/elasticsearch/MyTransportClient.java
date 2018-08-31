package com.github.sxw.client.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sxw.model.Article;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.*;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry.Option;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * 使用原生的Transport Client
 * http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/query-dsl-queries.html
 * @author welcome
 *
 */
public class MyTransportClient {
	
	/**
	 * 创建TransportClient
	 * @return
	 */
	private static Client createTransportClient() {
		//创建settings
		Settings settings = ImmutableSettings.settingsBuilder()
			.put("cluster.name", "elasticsearch")//设置集群名称
			.build();
		Client client = null;
		try {
			client = new org.elasticsearch.client.transport.TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}


	
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


	/**
	 * 打印索引信息
	 * @param response
	 */
	private static void printIndexInfo(IndexResponse response) {
		System.out.println("****************index ***********************");
		// Index name
		String _index = response.getIndex();
		// Type name
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will get: 1)
		long _version = response.getVersion();
		System.out.println(_index+","+_type+","+_id+","+_version);
	}

    public static void main(String[] args) throws Exception {
	    //创建mapping(指定不同的分词器)
//        createMapping("college","college","ik");
//        createMapping("major","major","ik");
        //添加数据(索引)
//        creatJsonStringIndex("college","college");
        //在college索引库(indices)中查询，college中的文本字段进行了[ik]分词
        matchQuery("college","name","徐州市铜山区的矿业大学");
//        //在book索引库(school)中查询，book中的文本字段进行了[standard]分词
//        matchQuery("major","name","徐州市铜山区的矿业大学");

        //删除索引
//        deleteIndex("book","book","AWWNxgSKw0zUULgqYAIA");
    }


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
                .startObject("city").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("desc").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }

    /**
     * 创建mapping(feid("indexAnalyzer","ik")该字段分词IK索引 ；feid("searchAnalyzer","ik")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     * @param indices 索引名称；
     * @param mappingType 索引类型
     * @param analyzer 分词器类型
     * @throws Exception
     */
    public static void createArticleMapping(String indices,String mappingType,String analyzer)throws Exception{
        Client client = createTransportClient();
        new XContentFactory();

        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indices)
                .startObject("properties")
                .startObject("id").field("type", "integer").field("store", "yes").endObject()
                .startObject("title").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("content").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("source").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("author").field("type", "string").field("store", "yes").field("indexAnalyzer", analyzer).field("searchAnalyzer", analyzer).endObject()
                .startObject("url").field("type", "string").field("store", "yes").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }

    /**
     * 仅仅只删除索引
     * @param index
     * @param type
     * @param id
     */
    private static void deleteIndex(String index, String type, String id){
        Client client = createTransportClient();
        DeleteResponse response = client.prepareDelete(index, type, id)
                .execute()
                .actionGet();
        boolean isFound = response.isFound();
        System.out.println("索引是否 存在:"+isFound); // 发现doc已删除则返回true
        System.out.println("****************index ***********************");
        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = response.getVersion();
        System.out.println(_index+","+_type+","+_id+","+_version);

        //优化索引
        OptimizeRequest optimizeRequest = new OptimizeRequest(index);
        OptimizeResponse optimizeResponse = client.admin().indices().optimize(optimizeRequest).actionGet();
        System.out.println(optimizeResponse.getTotalShards()+","+optimizeResponse.getSuccessfulShards()+","+optimizeResponse.getFailedShards());

        //刷新索引
        FlushRequest flushRequest = new FlushRequest(index);
        flushRequest.force(true);
        FlushResponse flushResponse = client.admin().indices().flush(flushRequest).actionGet();
        System.out.println(flushResponse.getTotalShards()+","+flushResponse.getSuccessfulShards()+","+flushResponse.getFailedShards());

    }

    /**
     * 删除查询到的文档
     * @param index
     * @param name
     * @param value
     */
    private static void scrollSearchDelete(String index,String name,String value){
        Client client = createTransportClient();
        QueryBuilder qb = termQuery(name, value);
        SearchResponse scrollResp = client.prepareSearch(index)
                .setSearchType(SearchType.SCAN)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        while (true) {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                bulkRequest.add(client.prepareDelete(hit.getIndex(),hit.getType(),hit.getId()));
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            BulkItemResponse[] bulkItemResponse = bulkResponse.getItems();
            for (int i = 0; i <bulkItemResponse.length ; i++) {
                System.out.println(bulkItemResponse[i].getItemId()+":"+bulkItemResponse[i].getIndex()+":"+bulkItemResponse[i].getFailureMessage());
            }
        }
    }


    /**
     * 删除所有索引
     * @param indices
     */
    private static void deleteIndices(String indices){
        Client client = createTransportClient();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indices);
        DeleteIndexResponse response = client.admin().indices().delete(deleteIndexRequest)
                .actionGet();
        if(response.isAcknowledged()){
            System.out.println("删除成功!");
//			FlushRequest flushRequest = new FlushRequest(indices);
//			flushRequest.force(true);
//			FlushResponse flushResponse = client.admin().indices().flush(flushRequest).actionGet();
//			System.out.println(flushResponse.getTotalShards()+","+flushResponse.getSuccessfulShards()+","+flushResponse.getFailedShards());
        }
    }

    /**
     * 获取索引信息
     * @param index
     * @param type
     * @param id
     */
    private static void getIndex(String index, String type, String id){
        Client client = createTransportClient();
        GetResponse response = client.prepareGet(index, type, id)
                .execute()
                .actionGet();
        boolean exists = response.isExists();
        System.out.println(exists);// 判断索引是否存在
        String sourceString = response.getSourceAsString();
        System.out.println(sourceString);// 获取索引,并且打印出索引内容
        System.out.println("****************index ***********************");
        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = response.getVersion();
        System.out.println(_index+","+_type+","+_id+","+_version);
    }

    /**
     * Query Search
     * @param index
     * @param type
     * @param term
     * @param queryString
     */
    private static void querySearch(String index, String type,String term,String queryString){
        Client client = createTransportClient();
        SearchResponse response = client.prepareSearch(index)
                .setTypes(type)
                // 设置查询类型
                // 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
                // 2.SearchType.SCAN = 扫描查询,无序
                // 3.SearchType.COUNT = 不设置的话,这个为默认值,还有的自己去试试吧
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                // 设置查询关键词
                .setQuery(termQuery(term, queryString))             // Query
                //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
                .addHighlightedField(term)
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                // 最后就是返回搜索响应信息
                .execute()
                .actionGet();
        SearchHits searchHits = response.getHits();
        System.out.println("-----------------在["+term+"]中搜索关键字["+queryString+"]---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get(term);
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }

    /**
     * Query Search
     * @param index
     * @param type
     * @param term
     * @param queryString
     */
    private static void scrollSearch(String index, String type,String term,String queryString){
        Client client = createTransportClient();
        SearchResponse scrollResp = client.prepareSearch(index)
                .setScroll(new TimeValue(60000))
                .setTypes(type)
                // 设置查询类型
                // 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
                // 2.SearchType.SCAN = 扫描查询,无序
                // 3.SearchType.COUNT = 不设置的话,这个为默认值,还有的自己去试试吧
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                // 设置查询关键词
                .setQuery(termQuery(term, queryString))             // Query
                //.setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
                .addHighlightedField(term)
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll
        while (true) {
            for (SearchHit searchHit : scrollResp.getHits()) {
                //获取高亮的字段
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                HighlightField highlightField = highlightFields.get(term);
                System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
                Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
                Set<String> keySet = sourceAsMap.keySet();
                for (String string : keySet) {
                    System.out.println(string+":"+sourceAsMap.get(string));
                }
                System.out.println();
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
    }


    /**
     *
     * @param queryString
     */
    private static void multiSearch(String queryString){
        Client client = createTransportClient();
        SearchRequestBuilder srb1 = client.prepareSearch()
                .setQuery(QueryBuilders.queryStringQuery(queryString));

        SearchRequestBuilder srb2 = client.prepareSearch()
                .setQuery(QueryBuilders.matchQuery("desc", queryString));

        MultiSearchResponse sr = client.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .execute().actionGet();

        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            nbHits += response.getHits().getTotalHits();
            System.out.println("本次查询共匹配到:"+nbHits+"记录");
            SearchHits searchHits = response.getHits();
            System.out.println("-----------------搜索关键字为:["+queryString+"]---------------------");
            System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit searchHit : hits) {
                Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
                Set<String> keySet = sourceAsMap.keySet();
                for (String string : keySet) {
                    System.out.println(string+":"+sourceAsMap.get(string));
                }
                System.out.println();
            }
        }
    }

    /**
     *
     * @param indices
     * @param field
     * @param queryString
     */
    private static void count(String indices,String field,String queryString){
        Client client = createTransportClient();
        CountResponse response = client.prepareCount(indices)
                .setQuery(termQuery(field, queryString))
                .execute()
                .actionGet();
        long count = response.getCount();
        System.out.println("在文档"+indices+"中搜索字段"+field+"查询关键字:"+queryString+"共匹配到"+count+"条记录!");
    }

    private static void suggest(){
        Client client = createTransportClient();

//		CompletionSuggestionBuilder completionSuggestion = new CompletionSuggestionBuilder("suggestions");
//		completionSuggestion.field("text");
//		completionSuggestion.text("园");
//		completionSuggestion.size(10);
//		
//		SuggestRequestBuilder suggestRequestBuilder = client.prepareSuggest("article");
//		suggestRequestBuilder.addSuggestion(completionSuggestion);
//		SuggestResponse suggestResponse = suggestRequestBuilder.execute().actionGet();
//		
//		Suggestion<? extends Entry<? extends Option>> suggestion = suggestResponse.getSuggest().getSuggestion("suggestions");
//		for(Entry<? extends Option> entry:suggestion){
//			for (Option option : entry) {
//				System.out.println(option.getText().string());
//			}
//		}

        TermSuggestionBuilder termSuggestionBuilder = new TermSuggestionBuilder("suggest");
        termSuggestionBuilder.text("编程");
        termSuggestionBuilder.field("desc");
        TermSuggestion termSuggestion = client.prepareSuggest("book")
                .addSuggestion(termSuggestionBuilder)
                .execute()
                .actionGet()
                .getSuggest()
                .getSuggestion("suggest");
        Suggestion<? extends Entry<? extends Option>> suggestion = termSuggestion;
        for(Entry<? extends Option> entry:suggestion){
            for (Option option : entry) {
                System.out.println(option.getText().string());
            }
        }

    }


    private static void regexpQuery() {
        Client client = createTransportClient();
        RegexpQueryBuilder regexpQuery = QueryBuilders.regexpQuery("content", "健健|康康|圆圆|平平|安安|女神");
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(regexpQuery)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------regexpQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void indicesQuery() {
        Client client = createTransportClient();
        IndicesQueryBuilder indicesQuery = QueryBuilders.indicesQuery(termQuery("content", "性虐"), "news","article","book");
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(indicesQuery)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------indicesQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void wildcardQuery() {
        Client client = createTransportClient();
        WildcardQueryBuilder wildcardQuery = QueryBuilders.wildcardQuery("content", "S?");
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(wildcardQuery)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------wildcardQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void termsQuery() {
        Client client = createTransportClient();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("content", "家暴","澄清","帮助","女神","性虐");
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(termsQueryBuilder)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------termsQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void rangeQuery() {
        Client client = createTransportClient();
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("id")
                .from(5)
                .to(25)
                .includeLower(true)
                .includeUpper(false);//rangeQuery 查询id在5到24之间的内容
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(rangeQuery)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------rangeQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void queryString() {
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(QueryBuilders.queryStringQuery("女神 高圆圆 淤青 伤痕 床头"))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                .setHighlighterFragmentSize(250)//设置高亮内容长度
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------queryString---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void prefixQuery() {
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(QueryBuilders.prefixQuery("content","母亲"))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------prefixQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void matchAllQuery() {
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch("book")
                .setQuery(QueryBuilders.matchAllQuery())
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------matchAllQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    private static void fuzzyQuery() {
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(QueryBuilders.fuzzyQuery("content","床头"))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------fuzzyQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }

    @SuppressWarnings("deprecation")
    private static void fuzzyLikeQuery() {
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch("article")
                .setQuery(QueryBuilders.fuzzyLikeThisQuery("content").likeText("我 要 到 床头 去"))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("content")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------fuzzyLikeQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("content");
            System.out.println("高亮字段:"+highlightField.getName()+"\n高亮部分内容:"+highlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    /**
     * boolean query
     */
    private static void booleanQuery() {
        Client client = createTransportClient();
        QueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(termQuery("desc", "结构"))
                .must(termQuery("name", "深入"))
                .mustNot(termQuery("desc", "性虐"))
                .should(termQuery("desc", "GoWeb"));
        SearchResponse searchResponse = client.prepareSearch("book")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addHighlightedField("desc")
                .addHighlightedField("name")
                .setHighlighterPreTags("<em>")
                .setHighlighterPostTags("</em>")
                // 设置查询数据的位置,分页用
                .setFrom(0)
                // 设置查询结果集的最大条数
                .setSize(60)
                // 设置是否按查询匹配度排序
                .setExplain(true)
                .setQuery(queryBuilder)
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("-----------------boolQuery---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            //获取高亮的字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField deschighlightField = highlightFields.get("desc");
            System.out.println("高亮字段:"+deschighlightField.getName()+"\n高亮部分内容:"+deschighlightField.getFragments()[0].string());
            HighlightField namehighlightField = highlightFields.get("name");
            System.out.println("高亮字段:"+namehighlightField.getName()+"\n高亮部分内容:"+namehighlightField.getFragments()[0].string());
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    /**
     * 分词后进行查询
     * @param indices
     * @param field
     * @param queryString
     */
    private static void matchQuery(String indices,String field,String queryString){
        Client client = createTransportClient();
        SearchResponse searchResponse = client.prepareSearch(indices)
                .setQuery(QueryBuilders.matchQuery(field, queryString))
                .execute()
                .actionGet();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("---------------matchquery--在["+field+"]中搜索关键字["+queryString+"]---------------------");
        System.out.println("共匹配到:"+searchHits.getTotalHits()+"条记录!");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            Map<String, Object> sourceAsMap = searchHit.sourceAsMap();
            Set<String> keySet = sourceAsMap.keySet();
            for (String string : keySet) {
                System.out.println(string+":"+sourceAsMap.get(string));
            }
            System.out.println();
        }
    }


    /**
     * 创建Json字符串格式的索引
     */
    private static void creatJsonStringIndex(String index,String type) throws IOException {
        String json = "{" +
                "\"name\":\"中国矿业大学\"," +
                "\"city\":\"徐州 \"," +
                "\"desc\":\"中国矿业大学（China University of Mining and Technology），简称“矿大”，坐落于有“五省通衢”之称的江苏省徐州市，是教育部直属的全国重点大学，教育部与江苏省人民政府、国家安全生产监督管理总局共建高校， [1]  是首批列入国家“211工程”、“985平台”、“111计划”和“卓越工程师教育计划”重点建设的高校、国家首批“双一流”世界一流学科建设高校、高水平行业特色大学优质资源共享联盟成员，也是国家大学生创新性实验计划、国家建设高水平大学公派研究生项目、海外高层次人才引进计划实施高校。 [2] \"" +
                "}";
        createIndex(index,type,json);
    }

    /**
     * 创建Map类型的索引
     */
    private static void creatMapIndex() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name","Go Web编程");
        json.put("author","谢孟军 ");
        json.put("pubinfo","电子工业出版社");
        json.put("pubtime","2013-6");
        json.put("desc","《Go Web编程》介绍如何使用Go语言编写Web，包含了Go语言的入门、Web相关的一些知识、Go中如何处理Web的各方面设计（表单、session、cookie等）、数据库以及如何编写GoWeb应用等相关知识。通过《Go Web编程》的学习能够让读者了解Go的运行机制，如何用Go编写Web应用，以及Go的应用程序的部署和维护等，让读者对整个的Go的开发了如指掌。");
        Client client = createTransportClient();
        IndexResponse response = client.prepareIndex("book", "book").setSource(json).execute().actionGet();
        printIndexInfo(response);
    }

    /**
     * 序列化Bean的方式创建索引
     * @throws JsonProcessingException
     */
    private static void creatBeanIndex() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Article article = new Article(1,"高圆圆身上淤青 遭家暴还是玩SM遭性虐？","近日，有媒体拍到高圆圆身上的淤青，腿上还有两块伤疤，引起不少人猜测是遭受家暴。" +
                "对于遭到家暴的传闻，高圆圆首次作出澄清，称这是因为照顾母亲而留下的伤痕，她跟赵又廷关系好得很。" +
                "照顾母亲竟然会留下伤痕？究竟是怎么照顾的。" +
                "高圆圆称，“我妈当时住院，她翻身是需要旁人帮助的，我要到床头去，抱起她的上臂，然后她的脚一蹬，这样才能翻过来。" +
                "但我们两个的力气都不够，每次一用力的时候，我的大腿就会刚好撞在那个床框上，所以大腿上就撞出那两块淤青了。" +
                "事情真的这么简单吗？即使稍微一撞，也不至于淤青吧！" +
                "看到那个伤疤以及淤青的皮肤，不得不让人怀疑高圆圆是遭受家暴。" +
                "当然，还有另外一个原因，就是玩SM遭性虐。" +
                "当然，这么变态的事情，相信女神不会做的。" +
                "是照顾母亲留下的伤痕也好，遭受家暴也好，希望女神高圆圆以后都能平平安安健健康康吧！", "http://www.vdfly.com/star/20141119/37968.html", "青春娱乐网", "匿名");
        String json = mapper.writeValueAsString(article);
        createIndex("article","article",json);
    }


    /**
     * 使用Elasticsearch XContentBuilder 创建索引
     * @throws Exception
     */
    private static void useXContentBuilderCreatIndex() throws Exception {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("name","Go Web编程")
                .field("author","谢孟军 ")
                .field("pubinfo","电子工业出版社")
                .field("pubtime","2013-6")
                .field("desc","《Go Web编程》介绍如何使用Go语言编写Web，包含了Go语言的入门、Web相关的一些知识、Go中如何处理Web的各方面设计（表单、session、cookie等）、数据库以及如何编写GoWeb应用等相关知识。通过《Go Web编程》的学习能够让读者了解Go的运行机制，如何用Go编写Web应用，以及Go的应用程序的部署和维护等，让读者对整个的Go的开发了如指掌。")
                .endObject();
        String jsonstring = builder.string();
        createIndex("book","book",jsonstring);
    }
	
}
