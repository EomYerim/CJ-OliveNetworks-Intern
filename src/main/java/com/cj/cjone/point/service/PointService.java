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
	@Transactional // 트랜잭션은 유지하여 예외 발생 시 DB 변경사항이 롤백되도록 합니다.
	public void addPointsWithGradeMultiplier(String userId, int basePoints) {
		log.info("=== Starting point addition for user: {} with base points: {} ===", userId, basePoints);

		try {
			// 의도적으로 NullPointerException을 발생시키는 부분
			Grade grade = null; // 💥 의도적으로 null로 설정

			// 아래 라인에서 즉시 NullPointerException이 발생하여 catch 블록으로 이동합니다.
			String gradeName = grade.getGradeName();

			// --- 아래 코드는 NullPointerException 때문에 절대 실행되지 않습니다. ---
			// double multiplier = getMultiplierByGrade(gradeName);
			// int finalPoints = (int) (basePoints * multiplier);
			//
			// log.info("Calculated final points: {} (base: {} × multiplier: {})",
			//       finalPoints, basePoints, multiplier);
			//
			// log.info("This success logic is unreachable.");


		} catch (NullPointerException e) {
			// @Retryable이 없으므로 재시도하지 않습니다.
			// 에러를 명확하게 기록하고 예외를 다시 던져 트랜잭션이 롤백되도록 합니다.
			log.error("💥 NullPointerException occurred! Failing immediately for user: {}. Transaction will be rolled back.", userId, e);

			// 예외를 다시 던져야 @Transactional이 감지하고 롤백을 수행합니다.
			throw e;

		} catch (Exception e) {
			log.error("An unexpected error occurred during point addition for user: {}", userId, e);
			// 다른 예외 발생 시에도 롤백을 위해 다시 던집니다.
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