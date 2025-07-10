package edu.neu.oaas.service;

import edu.neu.oaas.mapper.UserBehaviorMapper;
import edu.neu.oaas.pojo.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    private final Map<Integer, Long> activeVisitors = new ConcurrentHashMap<>();
    private final AtomicInteger onlineUsers = new AtomicInteger(0);
    private final AtomicInteger totalVisitors = new AtomicInteger(0);

    @Value("${visitor.counter.file}")
    private String visitorCounterFile;

    @PostConstruct
    public void init() {
        // 从文件中读取总访客数
        try (BufferedReader reader = new BufferedReader(new FileReader(visitorCounterFile))) {
            String line = reader.readLine();
            if (line != null) {
                totalVisitors.set(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserBehavior(UserBehavior userBehavior) {
        userBehaviorMapper.insertUserBehavior(userBehavior);
        updateRealTimeStats(userBehavior);
    }

    public List<UserBehavior> getAllUserBehaviors() {
        return userBehaviorMapper.selectAllUserBehaviors();
    }

    public List<UserBehavior> searchUserBehaviors(String action, String timestamp) {
        return userBehaviorMapper.searchUserBehaviors(action, timestamp);
    }


    public int getRealTimeVisitors() {
        long currentTime = System.currentTimeMillis();
        activeVisitors.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > 300000); // 5分钟未活动
        return totalVisitors.get();
    }

    private void updateRealTimeStats(UserBehavior userBehavior) {
        long currentTime = System.currentTimeMillis();
        activeVisitors.put(userBehavior.getUserId(), currentTime);

        // 增加总访客数
        totalVisitors.incrementAndGet();
        saveTotalVisitors();

        // 假设 userId 唯一标识用户
        onlineUsers.incrementAndGet();

        // 清理不活跃的访客
        activeVisitors.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > 300000); // 5分钟未活动
    }

    private void saveTotalVisitors() {
        // 将总访客数保存到文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(visitorCounterFile))) {
            writer.write(Integer.toString(totalVisitors.get()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void incrementOnlineUsers() {
        onlineUsers.incrementAndGet();
    }

    public void decrementOnlineUsers() {
        onlineUsers.decrementAndGet();
    }

    public int getOnlineUsers() {
        return onlineUsers.get();
    }

    public void updateRealTimeVisitors() {
        int currentOnlineUsers = onlineUsers.get();
        System.out.println(currentOnlineUsers);
        totalVisitors.addAndGet(currentOnlineUsers);
        saveTotalVisitors();
    }
}
