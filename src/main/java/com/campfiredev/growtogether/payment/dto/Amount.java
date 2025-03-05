package com.campfiredev.growtogether.payment.dto;

public record Amount(

        Integer total,
        Integer tax_free,
        Integer vat,
        Integer point,
        Integer discount,
        Integer green_deposit

) {
}
