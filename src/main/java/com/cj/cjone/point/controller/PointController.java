package com.cj.cjone.point.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cj.cjone.point.dto.PointDto;
import com.cj.cjone.point.service.PointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
@Slf4j
public class PointController {

	private final PointService pointService;

	// 포인트 적립 API
	@PostMapping("/increase")
	public ResponseEntity<PointDto.Response> increasePoint(@RequestBody PointDto.Request request) {
		PointDto.Response response = pointService.increasePoint(request);
		return ResponseEntity.ok(response);
	}

	// 포인트 차감 API
	@PostMapping("/decrease")
	public ResponseEntity<PointDto.Response> decreasePoint(@RequestBody PointDto.Request request) {
		PointDto.Response response = pointService.decreasePoint(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/ai/point/{userId}/{basePoints}")
	public void triggerErrorApi(@PathVariable String userId, @PathVariable int basePoints) {
		log.info("API endpoint hit: /ai/point/{}/{}", userId, basePoints);
		// 서비스 메소드를 호출합니다. 이 메소드는 예외를 발생시키고,
		// Spring Boot는 기본적으로 처리되지 않은 예외에 대해 500 Internal Server Error를 응답합니다.
		pointService.addPointsWithGradeMultiplier(userId, basePoints);
	}
}