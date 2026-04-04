package com.ecommerce.stock.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class StockReservationItemId extends BaseUUIDValueObject {

    public StockReservationItemId(UUID id) {
        super(id);
    }
    
}
