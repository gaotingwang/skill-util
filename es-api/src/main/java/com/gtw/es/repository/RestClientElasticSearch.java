package com.gtw.es.repository;

import com.gtw.es.exception.MyEsException;
import com.gtw.es.model.Page;
import com.gtw.es.util.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class RestClientElasticSearch implements IElasticSearch {

    private Log logger = org.apache.commons.logging.LogFactory.getLog(RestClientElasticSearch.class);

    private String index;

    private String type;

    private RestHighLevelClient client;
    /**
     * 执行超时时长（ms）
     */
    private Long timeout = 10000L;

//    private static final RequestOptions COMMON_OPTIONS;
//    static {
//        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
//        COMMON_OPTIONS = builder.build();
//    }

    public RestClientElasticSearch(String index, String type) {
        this.index = index;
        this.type = type;
    }

    public RestClientElasticSearch(String index, String type, Long timeout) {
        this.index = index;
        this.type = type;
        this.timeout = timeout;
    }

    /**
     * 初始化es客户端
     * @return esClient
     */
    private RestHighLevelClient initEsClient(){
        return new RestHighLevelClient(
            // builder中可以传递多个HttpHost
            RestClient.builder(
                new HttpHost("localhost", 9200, "http"))
        );
    }

    public void createIndexType() {
//        Settings indexSettings = Settings.builder()
//                .put(SETTING_NUMBER_OF_SHARDS, 1)
//                .put(SETTING_NUMBER_OF_REPLICAS, 0)
//                .build();
//
//        String payload = XContentFactory.jsonBuilder()
//                .startObject()
//                .startObject("settings")
//                .value(indexSettings)
//                .endObject()
//                .startObject("mappings")
//                .startObject("doc")
//                .startObject("properties")
//                .startObject("time")
//                .field("type", "date")
//                .endObject()
//                .endObject()
//                .endObject()
//                .endObject()
//                .endObject().string();
//
//        HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
//
//        Response response = client.getLowLevelClient().performRequest("PUT", "my-index", emptyMap(), entity);
//        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//
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

        IndexRequest request = new IndexRequest(this.index, this.type);
        request.source(source, XContentType.JSON);
        request.timeout(TimeValue.timeValueMillis(this.timeout));

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        String id = response.getId();
        this.client.close();
        return id;
    }

    public String save(String id, String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        IndexRequest request = new IndexRequest(this.index, this.type, id);
        request.source(source, XContentType.JSON);
        request.create(true);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

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

        IndexRequest request = new IndexRequest(this.index, this.type, id);
        request.source(source, XContentType.JSON);
//        request.create(true); // 不再强行指定create状态为true, 若id不存在，创建；id存在，覆盖原有记录

        client.index(request, RequestOptions.DEFAULT);

        this.client.close();
    }

    public void update(String id, Map<String, Object> newValues) throws IOException {
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

        client.update(updateRequest, RequestOptions.DEFAULT);

        this.client.close();
    }

    public void deleteById(String id) throws IOException {
        this.client = this.initEsClient();
        DeleteRequest request = new DeleteRequest(this.index, this.type, id);

        client.delete(request, RequestOptions.DEFAULT);
        this.client.close();
    }

    public String searchById(String id) throws IOException {
        this.client = this.initEsClient();
        GetRequest request = new GetRequest(this.index, this.type, id);

        String result = null;
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if(response.isExists() && !response.isSourceEmpty()){
            result = response.getSourceAsString();
        }

        this.client.close();
        return result;
    }

    public List searchByIds(String... ids) throws IOException {
        this.client = this.initEsClient();
        MultiGetRequest request = new MultiGetRequest();
        for (String id : ids){
            request.add(index, type, id);
        }

        List<String> result = new ArrayList<String>();
        MultiGetResponse multiGetItemResponses = this.client.mget(request, RequestOptions.DEFAULT);
        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                result.add(response.getSourceAsString());
            }
        }

        this.client.close();
        return result;
    }

    public List matchSearchForList(Map<String, Object> mapQuery) throws IOException {
        this.client = this.initEsClient();

        BoolQueryBuilder queryBuilder= boolQuery();
        for (Map.Entry<String, Object> entry : mapQuery.entrySet()) {
            queryBuilder = queryBuilder.must(matchQuery(entry.getKey(), entry.getValue()));
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(queryBuilder)
                .timeout(TimeValue.timeValueMillis(timeout))
//                .from(0).size(5)
//                .explain(true)
//                .sort(new ScoreSortBuilder().order(SortOrder.DESC))
//                .sort(new FieldSortBuilder("_id").order(SortOrder.ASC))
                ;
        logger.info(searchSourceBuilder.toString());

        // 指定source中返回的字段
//        String[] includeFields = new String[] {"title", "user", "innerObject.*"};
//        String[] excludeFields = new String[] {"_type"};
//        searchSourceBuilder.fetchSource(includeFields, excludeFields);

        SearchRequest searchRequest = new SearchRequest(index)
                .types(type)
//                .searchType(SearchType.QUERY_THEN_FETCH)
                .source(searchSourceBuilder);

        logger.info(searchRequest.toString());

        List<String> result = new ArrayList<String>();
        SearchResponse searchResponse = this.client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            result.add(hit.getSourceAsString());
        }

        this.client.close();
        return result;
    }

    public Page termSearchForPage(Page page, String termName, String termValue) throws IOException {
        return null;
    }
}
