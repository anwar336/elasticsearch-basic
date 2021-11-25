package com.es.api.esdb;

import com.es.api.esdb.model.DataModel;
import com.es.api.esdb.model.DataSearchRequest;
import com.es.api.esdb.model.DataSearchResponse;
import com.es.api.esdb.model.EnrollmentRequest;
import com.es.api.esdb.service.ElasticService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {ElasticsearchRestClientAutoConfiguration.class})
class EsdbApplicationTests {

    @Autowired
    ElasticService esService;

    @Test
    void create() {
        EnrollmentRequest request = new EnrollmentRequest();

        DataModel dm = new DataModel();
        dm.setAnswer("dhaka");
        dm.setQuestion("where does fahim live?");
        dm.setHtml("<div>dhaka</div>");
        request.setDataModel(dm);
        //esService.createData(request);
    }

    @Test
    //@Ignore
    void search() {
        DataSearchRequest request = new DataSearchRequest();
        request.setQuestion("fahtttim");
        DataSearchResponse resp = esService.search(request);
        System.out.println("total :: " + resp.getDataList().size());
    }

}
