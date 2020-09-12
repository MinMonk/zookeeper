package com.monk.study.client.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 第三方jar包ZkClient的方式
 */
public class ZkClientDemo {

    public static final Logger logger = LoggerFactory.getLogger(ZkClientDemo.class);

    public static void main(String[] args) throws IOException {
        ZkClient client = new ZkClient("localhost:2181", 3000, 1000, new SerializableSerializer());

        // 创建临时节点
        client.createEphemeral("/data", "1".getBytes());


        client.subscribeDataChanges("/data", new IZkDataListener() {
            /**
             * 数据改变事件（该事件可以多次监听，而不像zk的默认客户端一样，事件只有一次性）
             * @param dataPath  监听的数据节点
             * @param data  改变后的值
             * @throws Exception
             */
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                logger.info("数据发生了改变, dataPath:{}, data:{}", dataPath, data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                logger.info("删除了数据, dataPath:{}", dataPath);
            }
        });

        client.writeData("/data","234");
        //client.delete("/data");

        System.in.read();
    }
}
