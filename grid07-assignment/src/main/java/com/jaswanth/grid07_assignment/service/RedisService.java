package com.jaswanth.grid07_assignment.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // CooldownCap
    public boolean isBotOnCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", 10, TimeUnit.MINUTES);
        return Boolean.FALSE.equals(wasSet);
    }

    //Horizontalcap
    public boolean isHorizontalCapReached(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long count = redisTemplate.opsForValue().increment(key);
        if (count > 100) {
            redisTemplate.opsForValue().decrement(key);
            return true;
        }
        return false;
    }

    public void decrementHorizontalCap(Long postId) {
        String key = "post:" + postId + ":bot_count";
        redisTemplate.opsForValue().decrement(key);
    }

    // viralityscore
    public void incrementViralityScore(Long postId, int points) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, points);
    }

    //notificationthrottler
    public boolean isNotificationOnCooldown(Long userId) {
        String key = "notif_cooldown:user_" + userId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", 15, TimeUnit.MINUTES);
        return Boolean.FALSE.equals(wasSet);
    }

    public void pushPendingNotification(Long userId, String message) {
        String key = "user:" + userId + ":pending_notifs";
        redisTemplate.opsForList().rightPush(key, message);
    }

    public List<String> popAllPendingNotifications(Long userId) {
        String key = "user:" + userId + ":pending_notifs";
        List<String> notifications = redisTemplate.opsForList()
                .range(key, 0, -1);
        redisTemplate.delete(key);
        return notifications;
    }

    public Set<String> getPendingNotificationKeys() {
        return redisTemplate.keys("user:*:pending_notifs");
    }
}
