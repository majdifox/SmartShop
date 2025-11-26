package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.entity.Client;
import org.majdifoxx.smartshop.enums.CustomerTier;

import java.math.BigDecimal;

public interface LoyaltyService {
    CustomerTier calculateTier(Client client);
    BigDecimal calculateDiscount(CustomerTier tier, BigDecimal subtotalHT);
}
