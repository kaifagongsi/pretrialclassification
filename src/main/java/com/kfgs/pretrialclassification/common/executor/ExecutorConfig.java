package com.kfgs.pretrialclassification.common.executor;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync// 启用异步任务
public class ExecutorConfig {

    /**
     * 默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务，
     * 当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中；
     * 当队列满了，就继续创建线程，当线程数量大于等于maxPoolSize后，开始使用拒绝策略拒绝
     */

    /** 核心线程数(默认线程数) */
    @NacosValue(value = "${async.executor.thread.core_pool_size:30}",autoRefreshed = true)
    private int corePoolSize;

    /** 最大线程数 */
    @NacosValue(value = "${async.executor.thread.max_pool_size:50}",autoRefreshed = true)
    private int maxPoolSize;

    /** 允许线程空闲时间(单位：默认为秒) */
    @NacosValue(value = "${async.executor.thread.keep_alive_time:30}",autoRefreshed = true)
    private int keepAliveTime;

    /** 缓冲队列大小 */
    @NacosValue(value = "${async.executor.thread.queue_capacity:30}",autoRefreshed = true)
    private  int queueCapacity;

    /** 线程池名前缀 */
    @NacosValue(value = "${async.executor.thread.thread_name_prefix:bhzx_}",autoRefreshed = true)
    private String threadNamePrefix;

    @Bean(name = "pretroalclassificationAsyncExecutor") // bean的名称，默认为首字母小写的方法名
    public ThreadPoolTaskExecutor asyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);
        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程(提交任务的线程)处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}
