package com.cj.cjone.ai.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class AIRecommendationService {

    private static final int TIMEOUT_THRESHOLD = 5000; // 5초
    private static final int MAX_TIMEOUT = 10000; // 10초
    private final RestTemplate restTemplate;
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public AIRecommendationService() {
        this.restTemplate = new RestTemplate();
        // 타임아웃 설정
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(MAX_TIMEOUT);
        factory.setConnectTimeout(3000);
        restTemplate.setRequestFactory(factory);
    }

    public UserGrade getUserGrade(String userId) {
        long startTime = System.currentTimeMillis();
        int currentRequest = requestCount.incrementAndGet();
        String threadName = Thread.currentThread().getName();

        try {
            // 트래픽 증가 시뮬레이션 - 요청이 많아질수록 느려짐
            long simulatedDelay = Math.min(currentRequest * 100L, 12000L);

            log.info("Calling AI service for user grade analysis: {}", userId);

            // 실제 외부 AI 서비스 호출 시뮬레이션
            UserGrade grade = callExternalAIService(userId, simulatedDelay);

            long responseTime = System.currentTimeMillis() - startTime;

            if (responseTime > TIMEOUT_THRESHOLD) {
                log.warn("[{}] AI service response time exceeded {} seconds for user: {}",
                        threadName, TIMEOUT_THRESHOLD/1000, userId);
            }

            if (grade != null) {
                log.info("AI response received - Grade: {}, Multiplier: {}",
                        grade.getGradeName(), grade.getMultiplier());
            }

            return grade;

        } catch (SocketTimeoutException e) {
            log.error("[{}] AI service timeout after {} seconds for user: {}",
                    threadName, MAX_TIMEOUT/1000, userId);
            log.error("java.net.SocketTimeoutException: Read timed out");
            log.error("\tat java.net.SocketInputStream.socketRead0(Native Method)");
            log.error("\tat java.net.SocketInputStream.socketRead(SocketInputStream.java:116)");
            log.error("\tat com.cjone.service.AIRecommendationService.callAIService(AIRecommendationService.java:89)");

            return null; // 이게 NPE의 원인!

        } catch (Exception e) {
            log.error("AI service call failed for user: {}", userId, e);
            return null;
        }
    }

    private UserGrade callExternalAIService(String userId, long delay) throws SocketTimeoutException {
        try {
            // 지연 시뮬레이션
            Thread.sleep(delay);

            // 타임아웃 시뮬레이션 - 요청이 많아지면 타임아웃 발생
            if (delay > MAX_TIMEOUT) {
                throw new SocketTimeoutException("Read timed out");
            }

            // 일부 사용자는 AI 서비스에서 등급을 찾을 수 없음 (NPE 원인)
            if (userId.endsWith("234") || userId.endsWith("456")) {
                return null; // 신규 사용자나 데이터 없는 사용자
            }

            // 정상적인 등급 반환
            return determineGrade(userId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI service call interrupted", e);
        }
    }

    private UserGrade determineGrade(String userId) {
        // 간단한 등급 결정 로직
        int hash = userId.hashCode() % 4;
        switch (hash) {
            case 0: return new UserGrade("BRONZE", 1.2);
            case 1: return new UserGrade("SILVER", 1.5);
            case 2: return new UserGrade("GOLD", 2.0);
            case 3: return new UserGrade("DIAMOND", 2.5);
            default: return new UserGrade("BRONZE", 1.2);
        }
    }
}