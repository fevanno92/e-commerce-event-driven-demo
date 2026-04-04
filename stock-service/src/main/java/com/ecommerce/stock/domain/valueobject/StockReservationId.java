package com.ecommerce.stock.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class StockReservationId extends BaseUUIDValueObject {

    public StockReservationId(UUID id) {
        super(id);        
    }
    
}
