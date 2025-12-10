package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.entity.PromoCode;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.service.AuthService;
import org.majdifoxx.smartshop.service.PromoCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> createPromoCode(@RequestBody Map<String, String> request) {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only ADMIN can create promo codes"));
        }

        String code = request.get("code");
        if (code == null) {
            throw new BusinessRuleException("Promo code is required");
        }

        PromoCode created = promoCodeService.createPromoCode(code.trim());
        return ResponseEntity.ok(Map.of(
                "id", created.getId(),
                "code", created.getCode(),
                "used", created.getUsed(),
                "discount", "5%"
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAllPromoCodes() {
        if (!authService.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only ADMIN can view promo codes"));
        }

        // Simple: expose entity or map to DTO if you want
        // For your project it's fine to return entity list
        List<PromoCode> codes = promoCodeService.getAllPromoCodes(); // add this method if you want
        return ResponseEntity.ok(codes);
    }
}
