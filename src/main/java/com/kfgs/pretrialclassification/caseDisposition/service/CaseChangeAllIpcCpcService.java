package com.kfgs.pretrialclassification.caseDisposition.service;

import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;

import java.util.List;

public interface CaseChangeAllIpcCpcService {
    public QueryResponseResult changOneRow(FenleiBaohuResult result);

    QueryResponseResult deleteOneRow(FenleiBaohuResult result);

    QueryResponseResult caseFinishAll(List<FenleiBaohuResult> list);
}
