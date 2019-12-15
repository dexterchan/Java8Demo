package lockfree;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

//Node: Reference - https://en.wikipedia.org/wiki/Non-blocking_linked_list

class Node {
    volatile Object value;
    volatile Node next;
    volatile Boolean logicalDelete;

    private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

    private static final AtomicReferenceFieldUpdater<Node, Boolean> logicalDeleter =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Boolean.class, "logicalDelete");

    Node(Object value){
        this.value=value;
        this.next=null;
        this.logicalDelete=false;
    }
    public boolean setNodeNext( Node node){
        return ( nextUpdater.compareAndSet(this, null, node) );
    }
    public boolean setNodeLogicalDelete(){
        return logicalDeleter.compareAndSet(this, false, true);
    }
    public Node getNext(){
        return nextUpdater.get(this);
    }
}
@Slf4j
public class LockFreeLinkedListQueue {


    private AtomicInteger size ;
    private AtomicInteger numberOfPurge;
    private volatile Node head;

    private final static int FREE_DEAD_LOCK_MS = 500;

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> headUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "head");

    public LockFreeLinkedListQueue(){
        size = new AtomicInteger(0);
        numberOfPurge = new AtomicInteger(0);
        head=null;
        //tail=null;
    }

    public void enqueue(Object value){
        Node node = new Node(value);

        //log.debug("Enqueue startHead(head{},value{})", head!=null?head.value:null,node.value );

        Node oldHead = null, newHead = null;
        long startTime = System.currentTimeMillis();
        do{
            oldHead = headUpdater.get(this);
            if (oldHead == null){
                newHead = node;
                if ((System.currentTimeMillis() - startTime) > FREE_DEAD_LOCK_MS) {
                    log.debug("failed to insert first element {}", value);
                    throw new IllegalStateException(String.format("Enqueue: failed to compete resource to insert first element {}", value));
                }
            }else{
                Node tailNode = oldHead;
                while (!tailNode.setNodeNext(node) ){
                    tailNode = tailNode.next;
                    if ((System.currentTimeMillis() - startTime) > FREE_DEAD_LOCK_MS) {
                        int s = size.get();
                        log.debug("failed to compete resource to insert {} element {}",s, value);
                        throw new IllegalStateException(String.format("Enqueue: failed to compete resource to insert %d element %d",s, value));
                    }
                }
                break;
            }

        }while( !(headUpdater.compareAndSet(this, oldHead, newHead)));

        size.incrementAndGet();

    }

    public Object dequeue(){
        Node dNode = null;
        Node newHead = null;

        //2 phases approach to dequeue

        //First phase logical delete
        do{
            dNode = headUpdater.get(this);
            while(dNode!=null && dNode.logicalDelete){
                dNode = dNode.next;
            }

        }while(dNode!=null && !dNode.setNodeLogicalDelete());
        if (dNode == null){
            return null;
        }
        size.decrementAndGet();

        //Second phase , real delete
        Node oldHead = null;
        int localPurge = 0;
        do{
            localPurge = 0;
            oldHead = headUpdater.get(this);
            newHead = oldHead;
            while (newHead!=null && newHead.getNext() != null && newHead.logicalDelete){
                newHead = newHead.getNext();
                localPurge++;
            }
        }while (!headUpdater.compareAndSet(this, oldHead, newHead));
        this.numberOfPurge.addAndGet(localPurge);

        return dNode.value;
    }

    public int size(){
        return size.get();
    }

    public int getNumberOfPurge(){
        return this.numberOfPurge.get();
    }

    public List toList(){
        List list = new LinkedList();
        Node node = this.head;
        while (node != null){
            if (!node.logicalDelete) {
                list.add(node.value);
            }
            node = node.next;
        }
        return list;
    }

}
