package lockfree;



/*
    The code
    is very simple,and the corresponding comments are written.I believe everyone should understand it.

        The CAS atomic operation classes provided by JDK are all located under java.util.concurrent.atomic.Here I use the AtomicReference Array class for arrays,and of course you can also use Atomic IntegerArray.Here we use two atomic classes as pointer head,tail,and use the length of mod queue to implement a circular array.

        Testing our code below:

        import java.util.stream.IntStream;

 */

import java.util.stream.IntStream;

public class LockFreeArrayQueueDemo {
    public static void main(String[] args) {
        LockFreeArrayQueue queue = new LockFreeArrayQueue(5);
        IntStream.rangeClosed(1, 10).parallel().forEach(
                i -> {
                    if (i % 2 == 0) {
                        queue.add(i);
                    } else {
                        queue.poll();
                    }
                }
        );
        queue.print();
    }
}
