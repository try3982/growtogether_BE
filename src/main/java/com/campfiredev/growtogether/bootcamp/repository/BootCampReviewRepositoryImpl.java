package com.campfiredev.growtogether.bootcamp.repository;

import com.campfiredev.growtogether.bootcamp.entity.BootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.QBootCampReview;
import com.campfiredev.growtogether.bootcamp.entity.QBootCampSkill;
import com.campfiredev.growtogether.bootcamp.strategy.WeightCalculateStrategy;
import com.campfiredev.growtogether.bootcamp.type.ProgramCourse;
import com.campfiredev.growtogether.skill.entity.QSkillEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BootCampReviewRepositoryImpl implements BootCampReviewRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BootCampReview> searchBootCamps(String bootCampName,String title,ProgramCourse programCourse, String skillName, Pageable pageable) {

        QBootCampReview bootCamp = QBootCampReview.bootCampReview;
        QBootCampSkill bootCampSkill = QBootCampSkill.bootCampSkill;
        QSkillEntity skill = QSkillEntity.skillEntity;

        BooleanBuilder builder = new BooleanBuilder();


        if(bootCampName != null && !bootCampName.isEmpty()){
            builder.and(bootCamp.bootCampName.containsIgnoreCase(bootCampName));
        }

        if(title != null && !title.isEmpty()){
            builder.and(bootCamp.title.containsIgnoreCase(title));
        }

        if(programCourse != null){
            builder.and(bootCamp.programCourse.eq(programCourse));
        }

        if(skillName != null && !skillName.isEmpty()){
            builder.and(bootCamp.bootCampId.in(
                    JPAExpressions
                            .select(bootCampSkill.bootCampReview.bootCampId)
                            .from(bootCampSkill)
                            .join(bootCampSkill.skill,skill)
                            .where(skill.skillName.containsIgnoreCase(skillName))
            ));
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifer(pageable,bootCamp);

        List<BootCampReview> results = queryFactory
                .selectFrom(bootCamp)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory.select(bootCamp.count())
                        .from(bootCamp)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);


        return new PageImpl<>(results,pageable,total);
    }


    private OrderSpecifier<?> getOrderSpecifer(Pageable pageable , QBootCampReview bootCamp){

        for(Sort.Order order : pageable.getSort()){

            if("likeCount".equals(order.getProperty())){
                return bootCamp.likeCount.desc();
            }
        }

        return bootCamp.createdAt.desc();
    }

    @Override
    public List<BootCampReview> findTopRankedReviews(WeightCalculateStrategy strategy, int limit) {

        QBootCampReview review = QBootCampReview.bootCampReview;

        NumberExpression<Double> weightScore = strategy.calculateWeightExpression(review);

        return queryFactory
                .selectFrom(review)
                .orderBy(weightScore.desc())
                .limit(limit)
                .fetch();
    }
}
