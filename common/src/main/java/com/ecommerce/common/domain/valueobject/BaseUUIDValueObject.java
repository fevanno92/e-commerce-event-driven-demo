package com.ecommerce.common.domain.valueobject;

import java.util.UUID;

// Base class for all Value Object which are based on UUID. It provides common functionality such as equality checks and validation.
public abstract class BaseUUIDValueObject {
    private final UUID id;

    public BaseUUIDValueObject(UUID value) {
        this.id = value;
    }
    
    public UUID getId() {
        return id;
    }

    public boolean isDefined() {
        return id != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUUIDValueObject that = (BaseUUIDValueObject) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
