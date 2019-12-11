package lockfree;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;


/**
 * Implementing unlocked bounded queues with arrays
 */

public class LockFreeArrayQueue {

    private AtomicReferenceArray atomicReferenceArray;
    //Represents empty, no elements
    private static final Integer EMPTY = null;
    //Head pointer, tail pointer
    AtomicInteger head, tail;


    public LockFreeArrayQueue(int size) {
        atomicReferenceArray = new AtomicReferenceArray(new Integer[size + 1]);
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
    }

    /**
     * Join the team
     *
     * @param element
     * @return
     */
    public boolean add(Integer element) {
        int index;
        do{
             index = (tail.get() + 1) % atomicReferenceArray.length();
             if (index == head.get() % atomicReferenceArray.length()) {
                 System.out.println("The current queue is full," + element + "Unable to join the team.!");
                 return false;
             }
        }while (!atomicReferenceArray.compareAndSet(index, EMPTY, element));
        tail.incrementAndGet(); //Moving tail pointer
        System.out.println("Team success add!" + element);
        return true;
    }

    /**
     * Team out
     *
     * @return
     */
    public Integer poll() {
        int index;
        Integer ele;

        do{
            if (head.get() == tail.get()) {
                System.out.println("The current queue is empty");
                return null;
            }
            index = (head.get() + 1) % atomicReferenceArray.length();
            ele = (Integer) atomicReferenceArray.get(index);
            //It's possible that other threads are also queuing.
        }while (ele==null || !atomicReferenceArray.compareAndSet(index, ele, EMPTY));
        head.incrementAndGet();
        System.out.println("Team success poll!" + ele);
        return ele;
    }

    public void print() {
        StringBuffer buffer = new StringBuffer("[");
        for (int i = 0; i < atomicReferenceArray.length(); i++) {
            if (i == head.get() || atomicReferenceArray.get(i) == null) {
                continue;
            }
            buffer.append(atomicReferenceArray.get(i) + ",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("]");
        System.out.println("Queue content:" + buffer.toString());

    }

}
