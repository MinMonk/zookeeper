package com.monk.study.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LeaderSelectorUtilDemo {

    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelector> leaderSelectors = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
            client.start();
            clients.add(client);

            LeaderSelector leaderSelector = new LeaderSelector(client,"/leaderLatch", new LeaderSelectorListener(){
                @Override
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

                }

                @Override
                public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                    System.out.println("当前leader是" + curatorFramework);
                    TimeUnit.SECONDS.sleep(3);
                }
            });

            leaderSelector.start();
            leaderSelectors.add(leaderSelector);
        }

        System.in.read();

        for(CuratorFramework framework : clients){
            framework.close();
        }

        for(LeaderSelector selector: leaderSelectors){
            selector.close();
        }
    }
}
