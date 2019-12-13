package io.exp.metric;


import com.codahale.metrics.Timer;


@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
