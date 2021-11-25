/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 *
 * @author anwar
 */
@Data
public class DataSearchResponse implements Serializable{
    private List<DataModel> dataList;
    private Long totalRecord;
    public DataSearchResponse(List<DataModel> dataList, Long totalRecord) {
        this.dataList = dataList;
        this.totalRecord = totalRecord;
    }
}
