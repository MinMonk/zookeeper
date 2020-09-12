package com.monk.study.client.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * 原生的Zookeeper客户端调用demo
 */
public class DefaultZookeeperClientDemo {

    public static void main(String[] args) throws Exception {
        ZooKeeper client = new ZooKeeper("localhost:2181", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("创建连接的时候");
            }
        });

        /**
         * 1. 给/data目录注册数据改变的watcher，也可以连接为监听事件，但是该事件是一次性的，生效一次后就无效了
         * 2. 第二个参数，还可以设置为true/false，false的时候表示不给目录注册监听事件，true的时候使用new zookeeper时的默认watcher
         */
        /*client.getData("/data", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (Event.EventType.NodeDataChanged.equals(event.getType())) {
                    System.out.println("数据改变的时候");
                }
            }
        }, new Stat());*/


        client.getData("/data", false,new AsyncCallback.DataCallback(){

            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("call back");
            }
        }, null);

        // 创建节点
        client.create("/data1", "1".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);

        // 修改数据
        client.setData("/data", "test".getBytes(), -1);

        System.in.read();

    }
}
