package com.campfiredev.growtogether;

import static org.assertj.core.api.Assertions.assertThat;

import com.campfiredev.growtogether.member.entity.MemberEntity;
import com.campfiredev.growtogether.member.repository.MemberRepository;
import com.campfiredev.growtogether.point.service.PointService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Rollback(false)
class PointServiceConcurrencyTest {

  @Autowired
  private PointService pointService;

  @Autowired
  private MemberRepository memberRepository;


  @Test
  void lockTest() throws InterruptedException {
    int numberOfThreads = 10;
    int amountToUse = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    for (int i = 0; i < numberOfThreads; i++) {
      executorService.submit(() -> {
        try {
          pointService.usePoint(3L, amountToUse);
        } catch (Exception e) {
          System.out.println("예외 발생: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    MemberEntity updatedMember = memberRepository.findById(3L).orElseThrow();

    System.out.println("최종 포인트: " + updatedMember.getPoints());

  }


}
