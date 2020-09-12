package com.monk.study.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.RetryNTimes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * curator工具类LeaderLatch使用
 */
public class LeaderLatchUtilDemo {

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderLatch> leaderLatches = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
            client.start();
            clients.add(client);

            LeaderLatch leaderLatch = new LeaderLatch(client,"/leaderLatch", "client#" + i);
            leaderLatch.start();
            leaderLatches.add(leaderLatch);
        }

        TimeUnit.SECONDS.sleep(5);

        for(LeaderLatch leader : leaderLatches){
            if(leader.hasLeadership()){
                System.out.println("当前leader是" + leader.getId());
            }
        }

        System.in.read();

        for(CuratorFramework framework : clients){
            framework.close();
        }
    }
}
