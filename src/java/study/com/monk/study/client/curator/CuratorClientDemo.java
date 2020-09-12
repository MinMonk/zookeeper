package com.monk.study.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuratorClientDemo {

    public static final Logger logger = LoggerFactory.getLogger(CuratorClientDemo.class);

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
        client.start();

        String path = "/data";
        Stat stat = client.checkExists().forPath(path);
        logger.info("stat:[{}]", stat);
        if(null == stat){
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path, "curator".getBytes());
        }

        NodeCache nodeCache = new NodeCache(client, path);
        /**
         * true：在一开启就去请求最新的数据放到缓存cache中，这个时候cache中和zookeeper中的数据是一致的就不会触发nodeChanged方法
         * false：在一开启的时候不会去请求将最新的数据放到缓存cache中，故这个时候的cache为空，自然就不会和zookeeper中最新的数据
         * 一致，故在启动的时候就出发了nodechanged方法
         */
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                logger.info("node changed method...");
            }
        });
        /*
        // 下面这种方法使用的原生的watch机制，那么就会存在事件一次性的问题
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                logger.info("wathch:{}", event);
            }
        }).forPath(path);*/


        System.in.read();
        client.close();
    }
}
