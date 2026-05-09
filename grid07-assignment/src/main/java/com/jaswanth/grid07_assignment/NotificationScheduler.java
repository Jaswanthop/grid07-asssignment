package com.jaswanth.grid07_assignment;

import com.jaswanth.grid07_assignment.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class NotificationScheduler {

    private final RedisService redisService;

    public NotificationScheduler(RedisService redisService) {
        this.redisService = redisService;
    }

    @Scheduled(fixedRate = 300000)
    public void sweepPendingNotifications() {
        Set<String> keys = redisService.getPendingNotificationKeys();

        Logger log= LoggerFactory.getLogger(NotificationScheduler.class);
        if (keys == null || keys.isEmpty()) {
            log.info("No pending notifications found");
            return;
        }

        for (String key : keys) {

            String userId = key.split(":")[1];

            List<String> notifications = redisService
                    .popAllPendingNotifications(Long.parseLong(userId));

            if (notifications == null || notifications.isEmpty()) continue;

            int count = notifications.size();
            String first = notifications.get(0);

            if (count == 1) {
                log.info("Summarized Push Notification: {}", first);
            } else {
                log.info("Summarized Push Notification: {} and [{}] others interacted with your posts",
                        first, count - 1);
            }
        }
    }
}
