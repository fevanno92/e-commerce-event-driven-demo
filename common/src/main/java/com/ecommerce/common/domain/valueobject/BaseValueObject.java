package com.ecommerce.common.domain.valueobject;

// Base class for all Value Objects with a single value
public abstract class BaseValueObject<T> {
    private final T value;

    public BaseValueObject(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public boolean isValid() {
        return value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseValueObject<?> that = (BaseValueObject<?>) o;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
