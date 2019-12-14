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
    public boolean setNodeNext( Node node){
        return ( nextUpdater.compareAndSet(this, null, node) );
    }
}
@Slf4j
public class LockFreeLinkedListQueue {


    private AtomicInteger size ;
    private volatile Node head;
    //private volatile Node tail;
    private final static int FREE_DEAD_LOCK_MS = 50;

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> headUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "head");

    /*
    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> tailUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "tail");
*/
    public LockFreeLinkedListQueue(){
        size = new AtomicInteger(0);

        head=null;
        //tail=null;
    }

    public void enqueue(Object value){
        Node node = new Node(value);

        //log.debug("Enqueue startHead(head{},value{})", head!=null?head.value:null,node.value );

        Node oldHead = null, newHead = null;
        long startTime = System.currentTimeMillis();
        do{
            oldHead = this.head;
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
                        throw new IllegalStateException(String.format("Enqueue: failed to compete resource to insert {} element {}",s, value));
                    }
                }
                break;
            }

        }while( !(headUpdater.compareAndSet(this, oldHead, newHead)));

        size.incrementAndGet();

    }

    public Object dequeue(){
        Node oldHead = null;
        Node newHead = null;
        do{
            oldHead = this.head;
            if (oldHead==null) {
                //log.debug("Nothing to dequeue");
                return null;
            }
            newHead = oldHead.next;

        }while (!headUpdater.compareAndSet(this, oldHead, newHead));
        size.decrementAndGet();
        if(oldHead !=null)
            return oldHead.value;
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
