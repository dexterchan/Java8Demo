package lockfree;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LinkedListQueueTest {
    static int numberofelement=6;
    @Test
    public void sequentialRunOK() {

        LinkedListQueue<Integer> linkedListQueue = new LinkedListQueue<>();
        IntStream.range(0,numberofelement).forEach(
                i->{
                    linkedListQueue.enqueue(i);
                }
        );

        List<Integer> integerList = linkedListQueue.toList();
        assertEquals(integerList.size(), numberofelement);
        Set<Integer> integerSet = Sets.newConcurrentHashSet();
        integerList.forEach(i->integerSet.add(i));
        assertEquals(integerSet.size(), numberofelement);

        Set<Integer> removeIntegerSet = Sets.newConcurrentHashSet();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.range(0, numberofelement).forEach(
                i->{
                    if (i%2==0){
                        int value = linkedListQueue.dequeue();
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
        assertEquals(linkedListQueue.size(), numberofelement/2);

        System.out.println(linkedListQueue.size());
        ExecutorService  executorService2 = Executors.newFixedThreadPool(10);
        IntStream.range(0, linkedListQueue.size()).forEach(
                i->{
                        Integer value = linkedListQueue.dequeue();

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
        assertEquals(linkedListQueue.size(), 0);


    }

    @Test
    public void paraelrun_failure(){
        LinkedListQueue<Integer> linkedListQueue = new LinkedListQueue<>();

        IntStream.range(0,numberofelement).parallel().forEach(
                i->{
                    linkedListQueue.enqueue(i);

                }
        );

        List<Integer> integerList = linkedListQueue.toList();
        assertNotEquals(integerList.size(), numberofelement);
        Set<Integer> integerSet = new HashSet<>();
        integerList.forEach(i->integerSet.add(i));
        assertNotEquals(numberofelement, integerSet.size() );
        assertEquals(linkedListQueue.size(), numberofelement);
    }
}