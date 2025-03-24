package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.*;
import com.campfiredev.growtogether.bootcamp.strategy.WeightCalculateStrategy;
import com.campfiredev.growtogether.bootcamp.strategy.WeightStrategy;
import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import com.campfiredev.growtogether.member.entity.QMemberEntity;
import com.campfiredev.growtogether.skill.entity.QSkillEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BootCampReviewRepositoryImpl implements BootCampReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BootCampReview> searchBootCamps(String bootCampName,String title,ProgramCourse programCourse, String skillName,Sort sort) {

        QBootCampReview bootCamp = QBootCampReview.bootCampReview;
        QBootCampSkill bootCampSkill = QBootCampSkill.bootCampSkill;
        QSkillEntity skill = QSkillEntity.skillEntity;
        QMemberEntity member = QMemberEntity.memberEntity;
        QBootCampComment comment = QBootCampComment.bootCampComment;

        BooleanBuilder builder = new BooleanBuilder();


        if (bootCampName != null && !bootCampName.isEmpty()) {
            builder.and(bootCamp.bootCampName.containsIgnoreCase(bootCampName));
        }

        if (title != null && !title.isEmpty()) {
            builder.and(bootCamp.title.containsIgnoreCase(title));
        }

        if (programCourse != null) {
            programCourse = ProgramCourse.valueOf(programCourse.name().toUpperCase());
            builder.and(bootCamp.programCourse.eq(programCourse));
        }

        if (skillName != null && !skillName.isEmpty()) {
            builder.and(bootCamp.bootCampId.in(
                    JPAExpressions
                            .select(bootCampSkill.bootCampReview.bootCampId)
                            .from(bootCampSkill)
                            .join(bootCampSkill.skill, skill)
                            .where(skill.skillName.containsIgnoreCase(skillName))
            ));
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifer(sort, bootCamp);

        //  BootCampReview + CommentCount 조회
        List<Tuple> results = queryFactory
                .select(bootCamp, JPAExpressions
                        .select(comment.count())
                        .from(comment)
                        .where(comment.bootCampReview.bootCampId.eq(bootCamp.bootCampId))
                )
                .from(bootCamp)
                .leftJoin(bootCamp.member, member).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        // Tuple을 BootCampReview로 변환
        List<BootCampReview> reviews = results.stream()
                .map(tuple -> {
                    BootCampReview review = tuple.get(bootCamp);
                    review.setCommentCount(tuple.get(1, Long.class).intValue());
                    return review;
                })
                .collect(Collectors.toList());

        List<Long> reviewIds = reviews.stream()
                .map(BootCampReview::getBootCampId)
                .collect(Collectors.toList());

        //BootCampSkill 가져오기
        List<BootCampSkill> skills = queryFactory
                .selectFrom(bootCampSkill)
                .join(bootCampSkill.skill, skill).fetchJoin()
                .where(bootCampSkill.bootCampReview.bootCampId.in(reviewIds))
                .fetch();

        //BootCampSkills 매핑
        Map<Long, List<BootCampSkill>> skillMap = skills.stream()
                .collect(Collectors.groupingBy(skillEntity -> skillEntity.getBootCampReview().getBootCampId()));

        for (BootCampReview r : reviews) {
            r.setBootCampSkills(skillMap.getOrDefault(r.getBootCampId(), new ArrayList<>()));
        }

        long total = Optional.ofNullable(
                queryFactory.select(bootCamp.count())
                        .from(bootCamp)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return reviews;
    }


    private OrderSpecifier<?> getOrderSpecifer(Sort sort , QBootCampReview bootCamp){

        WeightCalculateStrategy strategy = new WeightStrategy();

        NumberExpression<Double> weightScore = strategy.calculateWeightExpression(bootCamp);

        for (Sort.Order order : sort) {
            if ("HOT".equalsIgnoreCase(order.getProperty())) {
                return weightScore.desc();
            }
        }
        return bootCamp.createdAt.desc(); // 기본 정렬
    }

    @Override
    public List<BootCampReview> findTopRankedReviews(WeightCalculateStrategy strategy, int limit) {
        QBootCampReview review = QBootCampReview.bootCampReview;
        QBootCampComment comment = QBootCampComment.bootCampComment;
        QBootCampSkill bootCampSkill = QBootCampSkill.bootCampSkill;
        QSkillEntity skill = QSkillEntity.skillEntity;
        QMemberEntity member = QMemberEntity.memberEntity;

        NumberExpression<Double> weightScore = strategy.calculateWeightExpression(review);

        // BootCampReview 리스트 먼저 조회
        List<BootCampReview> reviews = queryFactory
                .selectFrom(review)
                .leftJoin(review.member, member).fetchJoin()
                .orderBy(weightScore.desc())
                .limit(limit)
                .fetch();

        if (reviews.isEmpty()) {
            return reviews;
        }

        List<Long> reviewIds = reviews.stream()
                .map(BootCampReview::getBootCampId)
                .collect(Collectors.toList());


        List<BootCampSkill> skills = queryFactory
                .selectFrom(bootCampSkill)
                .join(bootCampSkill.skill, skill).fetchJoin()
                .where(bootCampSkill.bootCampReview.bootCampId.in(reviewIds))
                .fetch();


        List<BootCampComment> comments = queryFactory
                .selectFrom(comment)
                .where(comment.bootCampReview.bootCampId.in(reviewIds))
                .fetch();

        Map<Long, List<BootCampSkill>> skillMap = skills.stream()
                .collect(Collectors.groupingBy(skillEntity -> skillEntity.getBootCampReview().getBootCampId()));

        Map<Long, List<BootCampComment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(commentEntity -> commentEntity.getBootCampReview().getBootCampId()));

        for (BootCampReview r : reviews) {
            r.setBootCampSkills(skillMap.getOrDefault(r.getBootCampId(), new ArrayList<>()));
            r.setComments(commentMap.getOrDefault(r.getBootCampId(), new ArrayList<>()));
        }

        return reviews;
    }
}
