package lockfree;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LinkedListQueue <T> {
    private class Node <T>{
        T value;
        Node next;
        Node(T value){
            this.value=value;
            this.next=null;
        }
    }

    private AtomicInteger size ;
    private Node<T> head;
    private Node<T> tail;

    public LinkedListQueue(){
        size = new AtomicInteger(0);
        head=null;
        tail=null;
    }

    public void enqueue(T value){
        Node<T> node = new Node<T>(value);
        if (head==null){
            head = node;
            tail = node;
        }else {
            tail.next = node;
            tail = node;
        }
        size.incrementAndGet();
    }
    public T dequeue(){
        Node<T> value = head;
        if( head==tail){
            tail = null;
        }

        head = head!=null?head.next:null;

        size.decrementAndGet();
        if (value != null)
            return value.value;
        else
            return null;
    }
    public int size(){
        return size.get();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Size=");
        sb.append(size.get());
        sb.append(":");
        Node<T> node = this.head;
        while (node != null){
            sb.append(node.value.toString());
            sb.append(",");
            node = node.next;
        }
        return sb.toString();
    }

    public List<T> toList(){
        List<T> list = new LinkedList<>();
        Node<T> node = this.head;
        while (node != null){
            list.add(node.value);
            node = node.next;
        }
        return list;
    }
}


