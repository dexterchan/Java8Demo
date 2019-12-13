package io.exp.metric;

import com.codahale.metrics.Timer;

public interface TimerInterface<T> {

    public T timeit(SupplierWithException<T> callFunc);


}
