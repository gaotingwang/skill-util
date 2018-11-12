package com.gtw.es;

import com.gtw.es.model.User;
import com.gtw.es.repository.IElasticSearch;
import com.gtw.es.repository.RestClientElasticSearch;

import java.io.IOException;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws IOException {
        User user = new User("Tom", "trying out Elasticsearch", new Date());
        IElasticSearch elasticSearch = new RestClientElasticSearch("user_index", "user_type");
        elasticSearch.save(user);

    }
}
