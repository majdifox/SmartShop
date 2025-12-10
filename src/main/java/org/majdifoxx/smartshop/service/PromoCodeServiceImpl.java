package org.majdifoxx.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.entity.PromoCode;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.repository.PromoCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    @Override
    public PromoCode createPromoCode(String code) {
        if (code == null || !code.matches("^PROMO-[A-Z0-9]{4}$")) {
            throw new BusinessRuleException("Invalid promo code format. Must be PROMO-XXXX");
        }
        if (promoCodeRepository.existsByCode(code)) {
            throw new BusinessRuleException("Promo code already exists: " + code);
        }

        PromoCode promo = PromoCode.builder()
                .code(code)
                .used(false)
                .build();

        return promoCodeRepository.save(promo);
    }

    @Override
    public void validateForUse(String code) {
        if (code == null || !code.matches("^PROMO-[A-Z0-9]{4}$")) {
            throw new BusinessRuleException("Invalid promo code format: " + code);
        }

        PromoCode promo = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessRuleException("Invalid promo code: " + code));

        if (Boolean.TRUE.equals(promo.getUsed())) {
            throw new BusinessRuleException("Promo code already used: " + code);
        }
    }

    @Override
    public void markAsUsed(String code, Order order) {
        PromoCode promo = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessRuleException("Promo code not found: " + code));

        promo.setUsed(true);
        promo.setUsedAt(LocalDateTime.now());
        promo.setOrder(order);

        promoCodeRepository.save(promo);
    }

    @Override
    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

}

