package lockfree;

import com.google.common.collect.Sets;
import io.exp.metric.TimeMyRun;
import io.exp.metric.TimerInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
@Slf4j
public class LockFreeLinkedListQueueTest {
    static int numberofelement=10000;

    TimerInterface<Boolean> enqueueTimerInterface;

    @Before
    public void init(){
        enqueueTimerInterface = new TimeMyRun<>("Enqueue Timer");
    }

    @Test
    public void checkLockedFreeLinkedListDeuqueAfterEnqueue() {
        LockFreeLinkedListQueue lockFreeLinkedListQueue = new LockFreeLinkedListQueue();

        IntStream.range(0,numberofelement).parallel().forEach(
                i->{
                    enqueueTimerInterface.timeit(()->{
                        lockFreeLinkedListQueue.enqueue(i);
                        return true;
                    });
                }
        );

        List<Integer> integerList = lockFreeLinkedListQueue.toList();
        assertEquals(integerList.size(), numberofelement);
        Set<Integer> integerSet = Sets.newConcurrentHashSet();
        integerList.forEach(i->integerSet.add(i));
        assertEquals(numberofelement, integerSet.size() );
        assertEquals(lockFreeLinkedListQueue.size(), numberofelement);

        List remain = lockFreeLinkedListQueue.toList();
        assertEquals(remain.size(), numberofelement);
        /*
        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.range(0, numberofelement).forEach(
                i->{
                    if (i%2==0){
                        int value = (Integer)lockFreeLinkedListQueue.dequeue();
                        executorService.execute(()->{
                            removeIntegerSet.add(value);
                        });
                    }
                }
        );
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(6000, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        assertEquals(removeIntegerSet.size(), numberofelement/2);
        assertEquals(lockFreeLinkedListQueue.size(), numberofelement/2);

        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        IntStream.range(0, lockFreeLinkedListQueue.size()).forEach(
                i->{
                    int value = (Integer)lockFreeLinkedListQueue.dequeue();
                    executorService2.execute(()->{
                        removeIntegerSet.add(value);
                    });
                }
        );
        executorService2.shutdown();
        try {
            if (!executorService2.awaitTermination(6000, TimeUnit.SECONDS)) {
                executorService2.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService2.shutdownNow();
        }
        assertEquals(removeIntegerSet.size(), numberofelement);
        assertEquals(lockFreeLinkedListQueue.size(), 0);
        */

    }
    @Test
    public void checkLockedFreeLinkedListDeuqueMixEnqueue() {
        numberofelement=10;
        LockFreeLinkedListQueue lockFreeLinkedListQueue = new LockFreeLinkedListQueue();
        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();
        Set<Integer> failedIncrement = Sets.newConcurrentHashSet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger nullvalue = new AtomicInteger(0);
        IntStream.range(0, numberofelement).parallel().forEach(
                i -> {
                    if (i%2 == 0) {
                        try {
                            lockFreeLinkedListQueue.enqueue(i);
                        }catch(Exception ex){
                            log.error(ex.getMessage());
                            failedIncrement.add(i);
                        }
                    }

                    else{
                        try {
                            Object obj = (Integer) lockFreeLinkedListQueue.dequeue();
                            if (obj != null) {
                                Integer value = (Integer) obj;
                                executorService.execute(() -> {
                                    removeIntegerSet.add(value);
                                });
                            } else {
                                nullvalue.incrementAndGet();
                            }
                        }catch(Exception ex){
                            log.error(ex.getMessage());
                        }

                    }
                }
        );
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(6000, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        List remain = lockFreeLinkedListQueue.toList();
        remain.forEach(obj->{
                    Integer i = (Integer)obj;
                    if (removeIntegerSet.contains(i)){
                        fail("Found "+i+ " in remove set");
                    }
                }
        );
        List remainList = lockFreeLinkedListQueue.toList();
        log.info ("Removed Items:{}",removeIntegerSet.size());
        log.info ("Remain Items:{}", remainList.size());
        log.info("Failed add:{}", failedIncrement.size());

    }
}