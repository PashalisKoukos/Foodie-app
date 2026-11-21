package gr.dit.hua.Foodie.core.service.model;

import java.math.BigDecimal;

public record OrderItemView(
        Long id,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}