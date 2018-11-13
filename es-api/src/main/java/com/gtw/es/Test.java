package com.gtw.es;

import com.gtw.es.model.User;
import com.gtw.es.repository.IElasticSearch;
import com.gtw.es.repository.RestClientElasticSearch;
import com.gtw.es.repository.TransportClientElasticSearch;
import com.gtw.es.util.ConvertUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        restClientElasticSearch();

//        transportClientElasticSearch();
    }

    /**
     * 使用transportClient进行es操作
     */
    private static void transportClientElasticSearch() throws Exception {
        User user = new User("Tom", "trying out Elasticsearch", new Date());
        IElasticSearch elasticSearch = new TransportClientElasticSearch("my_index", "my_type");

//        // 增
//        elasticSearch.save("2", ConvertUtils.obj2JSON(user));
//
//        // 改
//        User user1 = new User("Jack", "Replace", new Date());
////        elasticSearch.replace("2", ConvertUtils.obj2JSON(user));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "Tom");
//        map.put("message", "hahaha");
//        elasticSearch.update("2", map);
//
//        // 查
//        String result = elasticSearch.searchById("2");
//        if (result != null){
//            User resultUser = ConvertUtils.json2Obj(result, User.class);
//            System.out.println(resultUser);
//        }
//
        elasticSearch.termSearchForList(map);

    }

    /**
     * 使用restClient进行es操作
     */
    private static void restClientElasticSearch() throws IOException {
        User user = new User("Tom", "trying out Elasticsearch", new Date());
        IElasticSearch elasticSearch = new RestClientElasticSearch("my_index", "my_type");

        elasticSearch.save(user);

        String result = elasticSearch.searchById("AWcLbaOlqCf64eUhhmS0");
        if (result != null){
            User resultUser = ConvertUtils.json2Obj(result, User.class);
            System.out.println(resultUser);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "张三");
        elasticSearch.termSearchForList(map);
    }
}
