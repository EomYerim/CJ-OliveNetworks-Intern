package com.cj.cjone.point.service;

import com.cj.cjone.grade.entity.Grade;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.cj.cjone.point.dto.PointDto;
import com.cj.cjone.point.entity.Point;
import com.cj.cjone.point.repository.PointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

	private final PointRepository pointRepository;
	private final UserServiceClient userClient;
	@Transactional
	public void addPointsWithGradeMultiplier(String userId, int basePoints) {
		log.info("=== Starting point addition for user: {} with base points: {} ===", userId, basePoints);

		try {
			Grade grade = null; // 💥 여전히 예외 발생 테스트용
			String gradeName = grade.getGradeName(); // → NullPointerException 발생

		} catch (NullPointerException e) {
			log.error("💥 NullPointerException 발생 - 트랜잭션 롤백됩니다: {}", userId, e);
			throw e; // rollback 유지를 위해 반드시 throw 필요
		} catch (Exception e) {
			log.error("예상치 못한 예외 발생: {}", userId, e);
			throw e;
		}
	}

	public PointDto.Response increasePoint(PointDto.Request request) {
		// 1. 사용자 서비스에 유저가 실존하는지 확인 (by Feign)
		//    -> 사용자가 없으면 Feign이 Exception을 발생시켜 여기서 중단됨
		// userClient.checkUserExists(request.getUserId());

		// 2. 포인트 DB에서 사용자 포인트를 조회하거나, 없으면 새로 생성
		Point point = pointRepository.findByUserId(request.getUserId())
			.orElseGet(() -> new Point(request.getUserId()));

		// 3. 포인트 증가 로직
		point.increase(request.getAmount());

		Point savedPoint = pointRepository.save(point);

		return new PointDto.Response(savedPoint.getUserId(), savedPoint.getBalance());
	}


	public PointDto.Response decreasePoint(PointDto.Request request) {
		// 1. 사용자 확인
		// userClient.checkUserExists(request.getUserId());

		// 2. 포인트 조회 (포인트 정보가 없으면 에러)
		Point point = pointRepository.findByUserId(request.getUserId())
			.orElseThrow(() -> new IllegalStateException("포인트 정보가 존재하지 않습니다."));

		// 3. 포인트 차감 로직 수행
		point.decrease(request.getAmount());

		Point savedPoint = pointRepository.save(point);

		return new PointDto.Response(savedPoint.getUserId(), savedPoint.getBalance());
	}
}