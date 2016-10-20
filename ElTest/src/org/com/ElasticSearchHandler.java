package org.com;

import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.util.packed.PackedDataInput;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

//核心搜索模拟类
public class ElasticSearchHandler {
	private Client client;
	
	public ElasticSearchHandler(){
		this("127.0.0.1");
	}
	
	public ElasticSearchHandler(String ipAddress){
		//集群连接超时设置
		Settings settings = Settings.settingsBuilder()
				.put("client.transport.sniff", true)
				. build();
		client = new TransportClient.Builder().settings(settings)
				.build().addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ipAddress,9300)));
	}
	
	//建立索引
	/*
	 * @param indexName  为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param indexType  Type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
     * @param jsondata     json格式的数据集合
	*/
	public void createIndexResponse(String indexname,String type,List<String> jsondata){
		//创建索引库 需要注意的是.setRefresh(true)这里一定要设置,
		//否则第一次建立索引查找不到数据
		IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
		for(int i = 0; i<jsondata.size(); ++i)
		{
			requestBuilder.setSource(jsondata.get(i)).execute().actionGet();
		}		
	}
	
	//创建索引
	 public IndexResponse createIndexResponse(String indexname, String type,String jsondata){
	        IndexResponse response = client.prepareIndex(indexname, type)
	            .setSource(jsondata)
	            .execute()
	            .actionGet();
	        return response;
	    }
	 
	 //执行搜索
	 public List<Medicine> searcher(QueryBuilder queryBuilder,String indexname,String type)
	 {
		 List<Medicine> list = new ArrayList<>();
		 SearchRequestBuilder  searchRequestBuilder = client.prepareSearch(indexname) //index name
			        .setTypes( type) //type name
			        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
			        .setQuery(queryBuilder)        // Query
			        //.setPostFilter(QueryBuilders.rangeQuery("eventCount").from(1).to(18))  // Filter
			        .setFrom(0).setSize(20).setExplain(true);
		 
		 //SearchHits hits = sbuilder.execute().actionGet().getHits();
		 //查询
         SearchResponse response = searchRequestBuilder.execute().actionGet();
         
         //System.out.println(response.toString());
         SearchHits searchHits = response.getHits();
         
		 System.out.println("查询记录数="+searchHits.getTotalHits());
		 
		 //SearchHit[] searchHits = hits.getHits();
//		 if(searchHits. > 0)
//		 {
			 for(SearchHit hit : searchHits)
			 {
				 Integer id = (Integer) hit.getSource().get("id");
				 String name = (String) hit.getSource().get("name");
				 String function = (String) hit.getSource().get("function");
				 list.add(new Medicine(id, name, function));
			 }
		 //}
		 return list;
	 }
	 
	 public static void main(String[] args)throws Exception
	 {
		 ElasticSearchHandler esHandler = new ElasticSearchHandler();
		 List<String> jsondata = DataFactory.getInitJsonData();
		 String indexname = "indexname";
		 String type = "typedemo";
		 
		 //esHandler.createIndexResponse(indexname, type, jsondata);
		 
		 //查询条件
		 QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("name = 感冒");
		 List<Medicine> result = esHandler.searcher(queryBuilder, indexname, type);
	        for(int i=0; i<result.size(); i++){
	            Medicine medicine = result.get(i);
	            System.out.println("(" + medicine.getId() + ")药品名称:" +medicine.getName() + "\t\t" + medicine.getFunction());
	        }
				 
	 }
}
