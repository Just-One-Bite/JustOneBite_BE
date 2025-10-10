package com.delivery.justonebite.shop.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ShopAvgProjection {

    UUID getShopId();
    BigDecimal getAverageRating();

}
