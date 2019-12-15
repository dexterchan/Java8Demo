package io.exp.metric;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeMyRun<T> implements  TimerInterface<T>{
    private static Timer myTimer = new Timer(new UniformReservoir());
    private final String myName;
    public TimeMyRun(String myName){
        this.myName = myName;
        MetricRegistry registry = new MetricRegistry();
        registry.register(myName, myTimer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }


    @Override
    public T timeit(SupplierWithException<T> callFunc) {
        return withTimer(myTimer, myName, callFunc);
    }

    public static <T> T withTimer(Timer timer, String name, SupplierWithException<T> func) {

        Timer.Context ctx = timer.time();
        try {
            T result = func.get();
            return result;

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Wrapped exception - " + name + ": " + e.getMessage(), e);

        } finally {
            ctx.stop();
        }
    }
}
