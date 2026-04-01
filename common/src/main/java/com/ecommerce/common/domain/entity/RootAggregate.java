package com.ecommerce.common.domain.entity;

// Marker class to indicate that an entity is a root aggregate in the domain model
public abstract class RootAggregate<ID> extends BaseEntity<ID> {

    public RootAggregate(ID id) {
        super(id);        
    }

    public RootAggregate() {
        super();
    }
    
}
