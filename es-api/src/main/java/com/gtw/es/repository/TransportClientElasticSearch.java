package com.gtw.es.repository;

import com.gtw.es.exception.MyEsException;
import com.gtw.es.model.Page;
import com.gtw.es.util.ConvertUtils;
import org.apache.commons.logging.Log;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class TransportClientElasticSearch implements IElasticSearch {

    private Log logger = org.apache.commons.logging.LogFactory.getLog(TransportClientElasticSearch.class);

    private TransportClient client;

    private String index;

    private String type;

    /**
     * 执行超时时长（ms）
     */
    private Long timeout = 10000L;

    public TransportClientElasticSearch(String index, String type) {
        this.index = index;
        this.type = type;
    }

    public TransportClientElasticSearch(String index, String type, Long timeout) {
        this.index = index;
        this.type = type;
        this.timeout = timeout;
    }

    /**
     * 初始化es客户端
     * @return esClient
     */
    private TransportClient initEsClient(){
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            logger.error("初始化esClient错误", e);
        }

        return client;
    }

    public void createIndexType() {
//        Settings settings = Settings.builder() //
//                .put(SETTING_NUMBER_OF_SHARDS, 1)
//                .put(SETTING_NUMBER_OF_REPLICAS, 0)
//                .build();
//
//        String mappings = XContentFactory.jsonBuilder()  //
//                .startObject()
//                .startObject("doc")
//                .startObject("properties")
//                .startObject("time")
//                .field("type", "date")
//                .endObject()
//                .endObject()
//                .endObject()
//                .endObject()
//                .string();
//
//        CreateIndexResponse response = transportClient.admin().indices()  //
//                .prepareCreate("my-index")
//                .setSettings(indexSettings)
//                .addMapping("doc", docMapping, XContentType.JSON)
//                .get();
//
//        if (response.isAcknowledged() == false) {
//            //
//        }
    }

    public String save(Object obj) throws IOException {
        if(obj == null){
            throw new MyEsException("对象为空，不能执行es保存操作");
        }

        String source = ConvertUtils.obj2JSON(obj);
        return this.save(source);
    }

    public String save(String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        IndexResponse response = client.prepareIndex(this.index, this.type)
                .setSource(source, XContentType.JSON)
                .get();

        String id = response.getId();
        this.client.close();
        return id;
    }

    public String save(String id, String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        IndexResponse response = client.prepareIndex(this.index, this.type, id)
                .setSource(source, XContentType.JSON)
                .setCreate(true)
                .get();

//        // Index name
//        String _index = response.getIndex();
//        // Type name
//        String _type = response.getType();
//        // Document ID (generated or not)
//        String _id = response.getId();
//        // Version (if it's the first time you index this document, you will get: 1)
//        long _version = response.getVersion();
//        // status has stored current instance statement.
//        RestStatus status = response.status();

        this.client.close();
        return id;
    }

    public void replace(String id, String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        client.prepareIndex(this.index, this.type, id)
                .setSource(source, XContentType.JSON)
//                .setCreate(true) // 不再强行指定create状态为true, 若id不存在，创建；id存在，覆盖原有记录
                .get();

        this.client.close();
    }

    public void update(String id, Map<String, Object> newValues) throws Exception {
        if(newValues == null || newValues.isEmpty()){
            throw new MyEsException("无有效值，es无法进行修改");
        }

        this.client = this.initEsClient();

        UpdateRequest updateRequest = new UpdateRequest(this.index, this.type, id);
        XContentBuilder jsonBuilder = jsonBuilder().startObject();
        for (Map.Entry<String, Object> entry : newValues.entrySet()) {
            jsonBuilder.field(entry.getKey(), entry.getValue());
        }
        jsonBuilder.endObject();
        updateRequest.doc(jsonBuilder);

        client.update(updateRequest).get();

        this.client.close();
    }

    public void deleteById(String id) throws IOException {
        this.client = this.initEsClient();
        client.prepareDelete(index, type, id).get();
        this.client.close();
    }

    public String searchById(String id) {
        this.client = this.initEsClient();

        String result = null;
        GetResponse response = client.prepareGet(index, type, id).get();
        if(response.isExists() && !response.isSourceEmpty()){
            result = response.getSourceAsString();
        }

        this.client.close();
        return result;
    }

    public List searchByIds(String... ids) throws IOException {
        this.client = this.initEsClient();
        MultiGetRequestBuilder request = client.prepareMultiGet();
        for (String id : ids){
            request.add(index, type, id);
        }

        List<String> result = new ArrayList<String>();
        MultiGetResponse multiGetItemResponses = request.get();
        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                result.add(response.getSourceAsString());
            }
        }

        this.client.close();
        return result;
    }

    public Page termSearchForPage(Page page, String termName, String termValue) throws IOException {
        return null;
    }

    public List termSearchForList(Map<String, Object> mapQuery) throws IOException {
        this.client = this.initEsClient();

        BoolQueryBuilder bq= boolQuery();
        for (Map.Entry<String, Object> entry : mapQuery.entrySet()) {
            bq = bq.must(termQuery(entry.getKey(), entry.getValue()));
        }
        SearchRequestBuilder requestBuilder = client.prepareSearch(index)
                .setTypes(type)
                .setTimeout(TimeValue.timeValueMillis(timeout))
                .setSearchType(SearchType.QUERY_THEN_FETCH)
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18)) // filter
                .setQuery(bq)
//                .setFrom(0).setSize(60).setExplain(true)
                ;

        logger.info(requestBuilder.toString());

        List<String> result = new ArrayList<String>();
        SearchResponse searchResponse = requestBuilder.get();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            result.add(hit.getSourceAsString());
        }

        this.client.close();
        return result;
    }
}
