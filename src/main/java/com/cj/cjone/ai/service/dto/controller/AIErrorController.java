package com.cj.cjone.ai.service.dto.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIErrorController {
    @GetMapping("/api/v1/points/accumulate-ai")
    public ResponseEntity<String> accumulateAiPoint() throws InterruptedException {
        // Istio의 타임아웃(0.5초)보다 길게 설정하여 의도적으로 타임아웃 유발
        Thread.sleep(1000);
        return ResponseEntity.ok("AI 포인트 적립 성공 (이 메시지는 보이면 안 됨)");
    }
}
