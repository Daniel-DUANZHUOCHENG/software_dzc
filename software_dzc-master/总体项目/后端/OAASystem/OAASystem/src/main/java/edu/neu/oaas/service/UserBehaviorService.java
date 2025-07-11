package edu.neu.oaas.service;

import edu.neu.oaas.mapper.UserBehaviorMapper;
import edu.neu.oaas.pojo.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(visitorCounterFile))) {
            String line = reader.readLine();
            if (line != null) {
                totalVisitors.set(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordBehavior(UserBehavior behavior) {
        userBehaviorMapper.insertUserBehavior(behavior);
        updateRealTimeStats(behavior);
    }

    public List<UserBehavior> getBehaviorsByUserId(Integer userId) {
        return userBehaviorMapper.getUserBehaviorsByUserId(userId);
    }

    public List<UserBehavior> searchBehaviors(String action, String timestamp) {
        return userBehaviorMapper.searchUserBehaviors(action, timestamp);
    }

    public List<UserBehavior> getAllUserBehaviors() {
        return userBehaviorMapper.selectAllUserBehaviors();
    }

    public void createUserBehavior(UserBehavior behavior) {
        userBehaviorMapper.insertUserBehavior(behavior);
        updateRealTimeStats(behavior);
    }

    public int getRealTimeVisitors() {
        long currentTime = System.currentTimeMillis();
        activeVisitors.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > 300000);
        return totalVisitors.get();
    }

    private void updateRealTimeStats(UserBehavior behavior) {
        long currentTime = System.currentTimeMillis();
        activeVisitors.put(behavior.getUserId(), currentTime);
        totalVisitors.incrementAndGet();
        saveTotalVisitors();
        onlineUsers.incrementAndGet();
        activeVisitors.entrySet().removeIf(entry -> (currentTime - entry.getValue()) > 300000);
    }

    private void saveTotalVisitors() {
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
        totalVisitors.addAndGet(currentOnlineUsers);
        saveTotalVisitors();
    }
}
