package com.campfiredev.growtogether.payment.dto;

public record PaymentOrder(

        Long id,
        String name,
        int totalPrice

) {
}
