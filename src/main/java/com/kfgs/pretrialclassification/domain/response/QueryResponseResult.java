package com.kfgs.pretrialclassification.domain.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * <H3>shanxi</H3>
 * <p></p>
 *
 * @author : 你的名字
 * @date : 2019-11-28 20:23
 */
@Data
@ToString
@NoArgsConstructor
public class QueryResponseResult extends ResponseResult implements Serializable {

    QueryResult queryResult;

    public QueryResponseResult(ResultCode resultCode, QueryResult queryResult) {
        super(resultCode);
        this.queryResult = queryResult;
    }
}
