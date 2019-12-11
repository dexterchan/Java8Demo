package lockfree;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class Node {
    volatile Object value;
    volatile Node next;

    private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

    Node(Object value){
        this.value=value;
        this.next=null;
    }
    public boolean setNodeNext(Node node){
        return (nextUpdater.compareAndSet(this, null, node) );
    }
}
@Slf4j
public class LockFreeLinkedListQueue {


    private AtomicInteger size ;
    private volatile Node head;
    private volatile Node tail;
    private final static int FREE_DEAD_LOCK_MS = 50;

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> headUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "head");

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> tailUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "tail");

    public LockFreeLinkedListQueue(){
        size = new AtomicInteger(0);
        head=null;
        tail=null;
    }

    public void enqueue(Object value){
        Node node = new Node(value);

        if (headUpdater.compareAndSet(this, null, node)){
            long time = System.currentTimeMillis();
            log.debug("Enqueue startHeadNull("+size.get()+"):"+value.toString());
            while(!tailUpdater.compareAndSet(this, null, node)){
                if (System.currentTimeMillis() - time > FREE_DEAD_LOCK_MS){
                    log.error(String.format("Wait too long head null %s (%d):",value.toString(),size.get()));
                    tail = null;
                }
            };
            log.debug("Enqueue stopHeadNUll:"+value.toString());
        }else{
            log.debug("Enqueue startHeadOK:"+value.toString());
            long time = System.currentTimeMillis();
            while (tail==null){
                if (System.currentTimeMillis() - time > FREE_DEAD_LOCK_MS){
                    log.error("Wait too long:");
                    System.exit(-1);
                }
            }

            while (!tail.setNodeNext(node)){
            }
            tail = node;
            log.debug("Enqueue stopHeadOK:"+value.toString());
        }

        size.incrementAndGet();
    }

    public Object dequeue(){
        Node head = headUpdater.getAndUpdate(this,node->
                size.get()>0 && node!=null ?node.next:null
        );
        tailUpdater.compareAndSet(this, head, null);
        if (head!=null) {
            log.debug("dequeue start ("+size.get()+"):" + head.value.toString());
        }else{
            log.debug("dequeue start ("+size.get()+":null");
        }


        size.decrementAndGet();

        if (head!=null) {
            log.debug("dequeue stop:" + head.value.toString());
        }else{
            log.debug("dequeue stop:null");
        }
        if(head !=null)
            return head.value;
        else
            return null;
    }

    public int size(){
        return size.get();
    }


    public List toList(){
        List list = new LinkedList();
        Node node = this.head;
        while (node != null){
            list.add(node.value);
            node = node.next;
        }
        return list;
    }

}
