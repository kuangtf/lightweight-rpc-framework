package com.ktf.rpc.server.ThreadUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂，实现自定义线程命名
 *
 * @author github.com/kuangtf
 * @date 2021/10/14 17:22
 */
public class RpcThreadFactory implements ThreadFactory {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final String namePrefix;

    /**
     * 定义线程组名称，在排查堆栈问题时，非常有帮助
     * @param whatFeatureOfGroup 自定义的线程标识
     */
    public RpcThreadFactory(String whatFeatureOfGroup) {
        namePrefix = "RpcThreadFactory's " + whatFeatureOfGroup + "-Worker-";
    }

    @Override
    public Thread newThread(Runnable task) {

        String name = namePrefix + nextId.getAndIncrement();

        return new Thread(null, task, name, 0);
    }
}
