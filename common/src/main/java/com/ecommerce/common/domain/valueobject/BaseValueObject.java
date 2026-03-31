package com.ecommerce.common.domain.valueobject;

// Base class for all value objects with a single value in the e-commerce application
public class BaseValueObject<T> {
    private final T value;

    public BaseValueObject(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
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
