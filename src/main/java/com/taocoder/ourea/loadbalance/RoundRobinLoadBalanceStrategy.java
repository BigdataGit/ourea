/*
 * Copyright (c) 2015 ketao1989.github.com. All Rights Reserved.
 */
package com.taocoder.ourea.loadbalance;

import com.taocoder.ourea.model.Invocation;
import com.taocoder.ourea.model.InvokeConn;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 根据方法级别进行轮询调用.不考虑权重
 * 
 * @author tao.ke Date: 16/3/3 Time: 下午3:39
 */
public class RoundRobinLoadBalanceStrategy extends AbstractLoadBalanceStrategy {

    private static final ConcurrentHashMap<String, AtomicInteger> current = new ConcurrentHashMap<String, AtomicInteger>();

    @Override
    protected InvokeConn doSelect(List<InvokeConn> invokeConns, Invocation invocation) {

        String key = invocation.getInterfaceName() + invocation.getMethodName();

        AtomicInteger cur = current.get(key);

        if (cur == null) {
            current.putIfAbsent(key, new AtomicInteger());
            cur = current.get(key);
        }

        if (cur.get() > Integer.MAX_VALUE - 5) {
            cur.set(0);
        }

        return invokeConns.get(cur.getAndIncrement() % invokeConns.size());

    }
}