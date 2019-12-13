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


    private AtomicInteger size, commitSize ;
    private volatile Node head;
    private volatile Node tail;
    private final static int FREE_DEAD_LOCK_MS = 50;

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> headUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "head");

    private static final AtomicReferenceFieldUpdater<LockFreeLinkedListQueue, Node> tailUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListQueue.class, Node.class, "tail");

    public LockFreeLinkedListQueue(){
        size = new AtomicInteger(0);
        commitSize = new AtomicInteger(0);
        head=null;
        tail=null;
    }

    public void enqueue(Object value){
        Node node = new Node(value);

        if(size.getAndIncrement()==0){
            log.debug("Enqueue startHeadNull(head{},tail{}, value{})", head!=null?head.value:null, tail!=null?tail.value:null,node.value);
            while (!headUpdater.compareAndSet(this, null, node)){
                log.debug("Waiting to enqueue {}", node.value);
            }


            while (!tailUpdater.compareAndSet(this, null, head)){

            };
            log.debug("Enqueue stopHeadNull(head{},tail{},value{})", head!=null?head.value:null, tail!=null?tail.value:null, node.value);
        }else {

            log.debug("Enqueue startHeadOK(head{},tail{},value{})", head!=null?head.value:null, tail!=null?tail.value:null,node.value);
            long time = System.currentTimeMillis();
            while (tail==null || head==null){
                if (System.currentTimeMillis() - time > FREE_DEAD_LOCK_MS){
                    log.error("Wait too long for increment:{}",node.value);
                    size.decrementAndGet();
                    throw new IllegalStateException("Enqueue failed to reach consistent state:"+ value);
                }
            }

            while (!tail.setNodeNext(node)){
            }
            tail = node;
            log.debug("Enqueue stopHeadOK(head{},tail{},value{})", head!=null?head.value:null, tail!=null?tail.value:null, node.value);
        }
        commitSize.incrementAndGet();

    }

    public Object dequeue(){

        if (commitSize.get()==0){
            return null;
        }
        Node _head=null;
        Node _tail=null;
        int s = size.getAndDecrement();
        if (s>0){
            long time = System.currentTimeMillis();
            do{
                _head = head;
                _tail = tail;
                log.debug("dequeue start ({}):{}",s, _head.value.toString());
                if (_head==null || _tail==null){
                    if (System.currentTimeMillis() - time > FREE_DEAD_LOCK_MS){
                        log.error("Wait too long to dequeue");
                        size.incrementAndGet();
                        throw new IllegalStateException("Dequeue failed to reach consistent state");
                    }
                    continue;
                }
            }while(headUpdater.compareAndSet(this, _head, _head.next));

            if (_tail == _head){
                if (!tailUpdater.compareAndSet(this, _tail, null)){
                    log.error("inconsistent state");
                    size.incrementAndGet();
                    throw new IllegalStateException("Dequeue failed to reach consistent state");
                }
            }
            commitSize.decrementAndGet();
        }else{
            commitSize.incrementAndGet();
            return null;
        }


        if (_head!=null) {
            log.debug("dequeue stop:" + _head.value.toString());
        }
        if(_head !=null)
            return _head.value;
        else
            return null;
    }

    public int size(){
        return commitSize.get();
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
