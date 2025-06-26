package com.heim.api.move.application.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.heim.api.move.application.dto.MoveRequest;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MoveCacheService {
    private final IMap<Long, MoveRequest> tripCache;
    private final Map<Long, Map<Long, Integer>> notificationCounters = new ConcurrentHashMap<>();

    public MoveCacheService(HazelcastInstance hazelcastInstance){
        this.tripCache = hazelcastInstance.getMap("tripRequests");
    }

    public void storeTrip(Long userId, MoveRequest request) {
        tripCache.put(userId, request);
    }

    public MoveRequest getTrip(Long userId) {
        return tripCache.get(userId);
    }

    public void removeTrip(Long userId) {
        tripCache.remove(userId);
    }

    public int getNotificationCount(Long moveId, Long driverId) {
        return notificationCounters.getOrDefault(moveId, Collections.emptyMap())
                .getOrDefault(driverId, 0);
    }

    public void incrementNotificationCount(Long moveId, Long driverId) {
        notificationCounters.computeIfAbsent(moveId, k -> new ConcurrentHashMap<>())
                .merge(driverId, 1, Integer::sum);
    }


}
