package lockfree;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import io.exp.metric.TimeMyRun;
import io.exp.metric.TimerInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
@Slf4j
public class LockFreeLinkedListQueueTest {
    static int numberofelement=1000;

    TimerInterface<Boolean> enqueueTimerInterface;
    TimerInterface<Integer> dequeueTimerInterface;

    @Before
    public void init(){
        enqueueTimerInterface = new TimeMyRun<>("Enqueue Timer");
        dequeueTimerInterface = new TimeMyRun<>("Dequeue Timer");
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
        List remain = lockFreeLinkedListQueue.toList();
        assertEquals(remain.size(), numberofelement);
        AtomicInteger dequeueRejectionNumber = new AtomicInteger(0);
        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.range(0, numberofelement).forEach(
                i->{
                    if (i%2==0){
                        try {
                            int value = dequeueTimerInterface.timeit(
                                    () -> {
                                        return Optional.ofNullable(lockFreeLinkedListQueue.dequeue()).map(
                                                (v) -> (Integer) v
                                        ).orElseThrow(() -> new NullPointerException("Nothing got dequeued"));

                                    }
                            );
                            executorService.execute(() -> {
                                removeIntegerSet.add(value);
                            });
                        }catch (NullPointerException be){
                            dequeueRejectionNumber.incrementAndGet();
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
        assertEquals(dequeueRejectionNumber.get(), 0);
        assertEquals(removeIntegerSet.size(), numberofelement/2);
        remain = lockFreeLinkedListQueue.toList();
        assertEquals(remain.size(), numberofelement/2);


        dequeueRejectionNumber.set(0);

        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        IntStream.range(0, lockFreeLinkedListQueue.size()).forEach(
                i->{
                    try {
                        int value = dequeueTimerInterface.timeit(
                                () -> {
                                    return Optional.ofNullable(lockFreeLinkedListQueue.dequeue()).map(
                                            (v) -> (Integer) v
                                    ).orElseThrow(() -> new NullPointerException("Nothing got dequeued"));

                                }
                        );
                        executorService2.execute(() -> {
                            removeIntegerSet.add(value);
                        });
                    }catch (NullPointerException be){
                        dequeueRejectionNumber.incrementAndGet();
                    }
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
        assertEquals(dequeueRejectionNumber.get(), 0);
        assertEquals(removeIntegerSet.size(), numberofelement);
        assertEquals(lockFreeLinkedListQueue.size(), 0);


    }
    @Test
    public void checkLockedFreeLinkedListDeuqueMixEnqueue() {
        int numberofelement=100000;
        LockFreeLinkedListQueue lockFreeLinkedListQueue = new LockFreeLinkedListQueue();
        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();
        List<Integer> removeIntegerList = Lists.newCopyOnWriteArrayList();
        Set<Integer> failedIncrement = Sets.newConcurrentHashSet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger nullvalue = new AtomicInteger(0);

        IntStream.range(0, numberofelement).forEach(
                i->{
                    executorService.execute(
                            ()->{
                                if (i%2 == 0) {
                                    try {
                                        enqueueTimerInterface.timeit(()-> {
                                            lockFreeLinkedListQueue.enqueue(i);
                                            return true;
                                        });
                                    }catch(Exception ex){
                                        log.error(ex.getMessage());
                                        failedIncrement.add(i);
                                    }
                                }
                                else{
                                    try {
                                        Integer value =
                                                dequeueTimerInterface.timeit(()->
                                                        Optional.ofNullable(lockFreeLinkedListQueue.dequeue()).map(
                                                                (v)->(Integer)v
                                                        ).orElseThrow( ()->new NullPointerException("Nothing got dequeued"))
                                                );

                                            removeIntegerSet.add(value);
                                            removeIntegerList.add((value));

                                    }catch(NullPointerException ne){
                                        nullvalue.incrementAndGet();
                                    }
                                    catch(Exception ex){
                                        log.error(ex.getMessage());
                                    }

                                }
                            }
                    );
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
        log.info ("Remain linked list size:{}", lockFreeLinkedListQueue.size());
        log.info ("failed dequeue:{}", nullvalue.get());
        log.info("Failed add:{}", failedIncrement.size());
        log.info("Number of purge: {}", lockFreeLinkedListQueue.getNumberOfPurge());
        assertEquals(removeIntegerSet.size(), removeIntegerList.size());
        assertEquals(nullvalue.get(), lockFreeLinkedListQueue.size());
        assertEquals(lockFreeLinkedListQueue.size(), remainList.size());
        assertEquals(numberofelement/2,remainList.size() + failedIncrement.size() + removeIntegerSet.size() );

    }

    @Test
    public void baselineLinkedBlockingQueue() {
        int numberofelement=100000;
        Queue<Integer> linkedBlockingQueue = Queues.newLinkedBlockingQueue();
        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();
        List<Integer> removeIntegerList = Lists.newCopyOnWriteArrayList();
        Set<Integer> failedIncrement = Sets.newConcurrentHashSet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger nullvalue = new AtomicInteger(0);

        IntStream.range(0, numberofelement).forEach(
                i->{
                    executorService.execute(()->{
                        if (i%2 == 0) {
                            try {
                                enqueueTimerInterface.timeit(()-> {
                                    linkedBlockingQueue.add(i);
                                    return true;
                                });
                            }catch(Exception ex){
                                log.error(ex.getMessage());
                                failedIncrement.add(i);
                            }
                        }
                        else{
                            try {
                                Integer value =
                                        dequeueTimerInterface.timeit(()->
                                                Optional.ofNullable(linkedBlockingQueue.poll()).map(
                                                        (v)->(Integer)v
                                                ).orElseThrow( ()->new NullPointerException("Nothing got dequeued"))
                                        );
                                //executorService.execute(() -> {
                                    removeIntegerSet.add(value);
                                    removeIntegerList.add((value));
                                //});
                            }catch(NullPointerException ne){
                                nullvalue.incrementAndGet();
                            }
                            catch(Exception ex){
                                log.error(ex.getMessage());
                            }

                        }
                    });
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
        List remain = linkedBlockingQueue.stream().collect(Collectors.toList());
        remain.forEach(obj->{
                    Integer i = (Integer)obj;
                    if (removeIntegerSet.contains(i)){
                        fail("Found "+i+ " in remove set");
                    }
                }
        );
        List remainList = linkedBlockingQueue.stream().collect(Collectors.toList());
        log.info ("Removed Items:{}",removeIntegerSet.size());
        log.info ("Remain Items:{}", remainList.size());
        log.info ("Remain linked list size:{}", linkedBlockingQueue.size());
        log.info ("failed dequeue:{}", nullvalue.get());
        log.info("Failed add:{}", failedIncrement.size());

        assertEquals(removeIntegerSet.size(), removeIntegerList.size());

        assertEquals(linkedBlockingQueue.size(), remainList.size());
        assertEquals(numberofelement/2,remainList.size() + failedIncrement.size() + removeIntegerSet.size() );

    }

}