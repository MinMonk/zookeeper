package com.monk.study.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;

public class CuratorSessionLostDemo {

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
        client.start();

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                /**
                 * 当连接的状态为lost状态时，阻塞当前客户端一直到重新连接上
                 * 在重新连接上之后，执行doTask方法
                 */
                if(connectionState == ConnectionState.LOST){
                    try {
                        if(client.getZookeeperClient().blockUntilConnectedOrTimedOut()){
                            doTask();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        doTask();
    }


    public static void doTask(){
        /**
         * 临时节点在session丢失之后，数据节点是必然的丢失了，这个是不可改变的事实
         * 而这里的做法，实则是在连接超时or丢失连接时，代码会一直阻塞当前线程，在重新
         * 连接上之后，执行对应的任务方法，从而让用户无感而已
         * 所以需要将那些操作全部封装到task方法中，以便在重新连接上之后，能够恢复
         *
         */
    }
}
