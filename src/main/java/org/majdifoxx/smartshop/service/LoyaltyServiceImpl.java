package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.entity.Client;
import org.majdifoxx.smartshop.enums.CustomerTier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    @Override
    public CustomerTier calculateTier(Client client) {
        int totalOrders = client.getTotalOrders();
        BigDecimal totalSpent = client.getTotalSpent();

        // PLATINUM: ≥20 commandes OU ≥15,000 DH
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        }

        // GOLD: ≥10 commandes OU ≥5,000 DH
        if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        }

        // SILVER: ≥3 commandes OU ≥1,000 DH
        if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }

        // BASIC: default (0 commande)
        return CustomerTier.BASIC;
    }

    @Override
    public BigDecimal calculateDiscount(CustomerTier tier, BigDecimal subtotalHT) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (tier) {
            case SILVER:
                // SILVER: 5% si sous-total ≥ 500
                if (subtotalHT.compareTo(new BigDecimal("500")) >= 0) {
                    discount = subtotalHT.multiply(new BigDecimal("0.05"));
                }
                break;

            case GOLD:
                // GOLD: 10% si sous-total ≥ 800
                if (subtotalHT.compareTo(new BigDecimal("800")) >= 0) {
                    discount = subtotalHT.multiply(new BigDecimal("0.10"));
                }
                break;

            case PLATINUM:
                // PLATINUM: 15% si sous-total ≥ 1200
                if (subtotalHT.compareTo(new BigDecimal("1200")) >= 0) {
                    discount = subtotalHT.multiply(new BigDecimal("0.15"));
                }
                break;

            case BASIC:
            default:
                discount = BigDecimal.ZERO;
                break;
        }

        // PDF: "Arrondis: tous les montants à 2 décimales"
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
