package com.gtw.es.repository;

import com.gtw.es.exception.MyEsException;
import com.gtw.es.model.Page;
import com.gtw.es.util.ConvertUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RestClientElasticSearch implements IElasticSearch {

    private String index;

    private String type;

    private RestHighLevelClient client;
    /**
     * 执行超时时长（ms）
     */
    private Long timeout = 10000L;

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
        request.create(true);
        request.timeout(TimeValue.timeValueMillis(this.timeout));

        IndexResponse response = client.index(request);

        String id = response.getId();
        this.client.close();
        return id;
    }

    public String save(String id, String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        IndexRequest request = new IndexRequest("index", "type", id);
        request.source(source, XContentType.JSON);
        request.create(true);

        IndexResponse response = client.index(request);

        this.client.close();
        return id;
    }

    public void update(String id, String source) throws IOException {
        if(source == null || "".equals(source.trim()) || !ConvertUtils.isJSON(source)){
            throw new MyEsException("非法JSON字符串，无法执行es保存");
        }

        this.client = this.initEsClient();

        IndexRequest request = new IndexRequest(this.index, this.type, id);
        request.source(source, XContentType.JSON);

        IndexResponse response = client.index(request);

        this.client.close();
    }

    public String update(byte[] source, String id) throws IOException {
        return null;
    }

    public List searchByIds(String... ids) throws IOException {
        return null;
    }

    public Page termSearchForPage(Page page, String termName, String termValue) throws IOException {
        return null;
    }

    public List termSearchForList(Map<String, Object> mapQuery) throws IOException {
        return null;
    }

    public void deleteEsById(String id) throws IOException {

    }

    public void createIndexType() {

    }
}
