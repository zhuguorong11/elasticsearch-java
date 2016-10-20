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

//��������ģ����
public class ElasticSearchHandler {
	private Client client;
	
	public ElasticSearchHandler(){
		this("127.0.0.1");
	}
	
	public ElasticSearchHandler(String ipAddress){
		//��Ⱥ���ӳ�ʱ����
		Settings settings = Settings.settingsBuilder()
				.put("client.transport.sniff", true)
				. build();
		client = new TransportClient.Builder().settings(settings)
				.build().addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ipAddress,9300)));
	}
	
	//��������
	/*
	 * @param indexName  Ϊ����������һ��es��Ⱥ�п����ж�������⡣ ���Ʊ���ΪСд
     * @param indexType  TypeΪ�������ͣ�����������ͬ�������²�ͬ���͵����ݵģ�һ���������¿����ж���������͡�
     * @param jsondata     json��ʽ�����ݼ���
	*/
	public void createIndexResponse(String indexname,String type,List<String> jsondata){
		//���������� ��Ҫע�����.setRefresh(true)����һ��Ҫ����,
		//�����һ�ν����������Ҳ�������
		IndexRequestBuilder requestBuilder = client.prepareIndex(indexname, type).setRefresh(true);
		for(int i = 0; i<jsondata.size(); ++i)
		{
			requestBuilder.setSource(jsondata.get(i)).execute().actionGet();
		}		
	}
	
	//��������
	 public IndexResponse createIndexResponse(String indexname, String type,String jsondata){
	        IndexResponse response = client.prepareIndex(indexname, type)
	            .setSource(jsondata)
	            .execute()
	            .actionGet();
	        return response;
	    }
	 
	 //ִ������
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
		 //��ѯ
         SearchResponse response = searchRequestBuilder.execute().actionGet();
         
         //System.out.println(response.toString());
         SearchHits searchHits = response.getHits();
         
		 System.out.println("��ѯ��¼��="+searchHits.getTotalHits());
		 
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
		 
		 //��ѯ����
		 QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("name = ��ð");
		 List<Medicine> result = esHandler.searcher(queryBuilder, indexname, type);
	        for(int i=0; i<result.size(); i++){
	            Medicine medicine = result.get(i);
	            System.out.println("(" + medicine.getId() + ")ҩƷ����:" +medicine.getName() + "\t\t" + medicine.getFunction());
	        }
				 
	 }
}
