/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.service;

import com.es.api.esdb.common.Defs;
import com.es.api.esdb.model.DataModel;
import com.es.api.esdb.model.DataSearchRequest;
import com.es.api.esdb.model.DataSearchResponse;
import com.es.api.esdb.model.EnrollmentRequest;
import com.es.api.esdb.model.EnrollmentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author anwar
 */
@Service
public class ElasticService {

    private static final Logger LOGGER = LogManager.getLogger(ElasticService.class);
    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public ElasticService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public EnrollmentResponse createData(EnrollmentRequest enrollRequest) {

        if (enrollRequest == null || enrollRequest.getDataModel() == null) {
            new EnrollmentResponse(false, "Request is empty");
        }
        
        if(enrollRequest.getDataModel().getQuestion() == null || enrollRequest.getDataModel().getQuestion().isEmpty()){
            new EnrollmentResponse(false, "Question field is empty");
        }
        
        if(enrollRequest.getDataModel().getAnswer() == null || enrollRequest.getDataModel().getAnswer().isEmpty()){
            new EnrollmentResponse(false, "Answer field is empty");
        }

        try {
            IndexRequest indexRequest = new IndexRequest(Defs.INDEX_NAME);
            indexRequest.id(UUID.randomUUID().toString());
            indexRequest.source(this.objectMapper.writeValueAsString(enrollRequest.getDataModel()), XContentType.JSON);
            IndexResponse response = this.client.index(indexRequest, RequestOptions.DEFAULT);
            
            
            return new EnrollmentResponse(true, "Data enrolled successfully");
            
        } catch (Throwable t) {
            LOGGER.error("Error storing question:{}", t.getMessage(), t);
            t.printStackTrace();
            return new EnrollmentResponse(false, "Failed to enroll data, reason : " + t.getMessage());
        }

    }

    public DataSearchResponse search(DataSearchRequest request) {
        List<DataModel> dataList = new ArrayList<DataModel>();
        if (request == null || request.getQuestion() == null || request.getQuestion().isEmpty()) {
            return new DataSearchResponse(dataList, 0L);
        }
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*" + request.getQuestion() + "*");
        
        boolQueryBuilder.should(queryBuilder);
        
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("question", request.getQuestion())
                                                .fuzziness(Fuzziness.AUTO);
        boolQueryBuilder.should(fuzzyQueryBuilder);
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);
        String[] searchIndices = {Defs.INDEX_NAME};
        SearchRequest searchRequest = new SearchRequest(searchIndices, sourceBuilder);
        Long totalCount = 0L;
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if (response != null && response.getHits() != null && response.getHits().getHits() != null && response.getHits().getHits().length > 0) {
                for (SearchHit hit : response.getHits().getHits()) {
                    DataModel dataModel = objectMapper.readValue(hit.getSourceAsString(), DataModel.class);
                    dataList.add(dataModel);
                }

                totalCount = response.getHits().getTotalHits().value;

                if (totalCount >= 10000) {
                    CountRequest countRequest = new CountRequest(searchIndices);
                    countRequest.query(sourceBuilder.query());
                    CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
                    if (countResponse != null) {
                        totalCount = countResponse.getCount();
                    }
                }

            }
        } catch (Throwable t) {
            LOGGER.error("Error searching order:{}", t.getMessage(), t);
            t.printStackTrace();
            //return new DataSearchResponse();
        }

        return new DataSearchResponse(dataList, totalCount);
    }
}
