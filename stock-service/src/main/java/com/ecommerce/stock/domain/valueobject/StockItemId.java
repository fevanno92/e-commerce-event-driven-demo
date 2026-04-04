package com.ecommerce.stock.domain.valueobject;

import java.util.UUID;

import com.ecommerce.common.domain.valueobject.BaseUUIDValueObject;

public class StockItemId extends BaseUUIDValueObject {

    public StockItemId(UUID id) {
        super(id);        
    }
    
}
