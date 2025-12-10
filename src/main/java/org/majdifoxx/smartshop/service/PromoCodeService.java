package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.entity.PromoCode;

import java.util.List;

public interface PromoCodeService {
    PromoCode createPromoCode(String code);
    void validateForUse(String code);
    void markAsUsed(String code, Order order);
    List<PromoCode> getAllPromoCodes();

}
