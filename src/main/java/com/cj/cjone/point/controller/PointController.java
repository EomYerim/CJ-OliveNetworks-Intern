package com.cj.cjone.point.controller;

import com.cj.cjone.ai.service.dto.AiPointRequest;
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

	@PostMapping("/ai")
	public ResponseEntity<Void> triggerErrorApi(@RequestBody AiPointRequest request) {
		log.info("API endpoint hit: /ai with body = {}", request);
		pointService.addPointsWithGradeMultiplier(request.getUserId(), request.getBasePoints());
		return ResponseEntity.ok().build();
	}
}