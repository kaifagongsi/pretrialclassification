package com.kfgs.pretrialclassification.common.function;


import com.kfgs.pretrialclassification.common.exception.RedisConnectException;

@FunctionalInterface
public interface JedisExecutor<T, R> {
    R excute(T t) throws RedisConnectException;
}
