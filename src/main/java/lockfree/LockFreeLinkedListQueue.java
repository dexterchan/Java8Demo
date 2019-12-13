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
                    log.debug("failed to insert first block lock {}", value);
                    System.exit(-1);
                }
            }else{
                Node tailNode = oldHead;
                while (!tailNode.setNodeNext(node)){
                    tailNode = tailNode.next;
                    if ((System.currentTimeMillis() - startTime) > FREE_DEAD_LOCK_MS) {
                        log.debug("head{} failed to acquire lock , {}", oldHead.value, value);
                        System.exit(-1);
                    }
                }
                break;
            }

        }while( !(headUpdater.compareAndSet(this, oldHead, newHead)));

        size.incrementAndGet();

    }

    public Object dequeue(){


        Node _head=null;


        if (_head!=null) {
            log.debug("dequeue stop:" + _head.value.toString());
        }
        if(_head !=null)
            return _head.value;
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
