package com.allardworks.workinator3.core;

import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class NullableOptional<T> {
    public NullableOptional() {
        hasValue = false;
    }

    public NullableOptional(final T value) {
        hasValue = true;
        this.value = value;
    }

    public void setValue(final T value) {
        hasValue = true;
        this.value = value;
    }

    public void removeValue() {
        hasValue = false;
        this.value = null;
    }

    private boolean hasValue;
    private T value;

    public NullableOptional<T> ifPresent(Consumer<NullableOptional> method) {
        if (this.isHasValue()) {
            method.accept(this);
        }
        return this;
    }
}
