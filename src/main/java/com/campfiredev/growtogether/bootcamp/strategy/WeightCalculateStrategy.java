package com.campfiredev.growtogether.bootcamp.strategy;

import com.campfiredev.growtogether.bootcamp.entity.QBootCampReview;
import com.querydsl.core.types.dsl.NumberExpression;

public interface WeightCalculateStrategy {

    NumberExpression<Double> calculateWeightExpression(QBootCampReview review);
}


