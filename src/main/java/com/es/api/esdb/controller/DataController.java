/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.controller;

import com.es.api.esdb.model.EnrollmentRequest;
import com.es.api.esdb.model.EnrollmentResponse;
import com.es.api.esdb.model.DataSearchRequest;
import com.es.api.esdb.model.DataSearchResponse;
import com.es.api.esdb.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author anwar
 */
@RestController
@RequestMapping("api")
public class DataController {
    
    @Autowired
    private ElasticService esService;
    
    @PutMapping("/create")
    public EnrollmentResponse create(@RequestBody EnrollmentRequest request){
        return esService.createData(request);
    }
    
    @PostMapping("/search")
    public DataSearchResponse search(@RequestBody DataSearchRequest request){
        return esService.search(request);
    }
    
}
