package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.NoSuchElementException;

public final class AVLTreeHeap<E, P extends Comparable<? super P>> 
        implements PriorityQueue<E, P> {

    private static final class HeapNode<E> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The next node in the collision chain.
         */
        HeapNode<E> next;
        
        HeapNode(E element) {
            this.element = element;
        }
    }
    
    private static final class HeapNodeList<E> {
        
        /**
         * The head node of this list.
         */
        HeapNode<E> head;
        
        void add(HeapNode<E> node) {
            node.next = head;
            head = node;
        }
        
        HeapNode<E> remove() {
            HeapNode<E> ret = head;
            head = head.next;
            return ret;
        }
        
        boolean isEmpty() {
            return head == null;
        }
    }
    
    /**
     * Maps each used integer priority to the collision chain of 
     * {@code HeapNode} objects.
     */
    private final AVLTreeMap<P, HeapNodeList<E>> map = new AVLTreeMap<>();
    
    /**
     * Holds the number of elements currently in this heap.
     */
    private int size;
    
    
    @Override
    public void add(E element, P priority) {
        HeapNode<E> newNode = new HeapNode<>(element);
        HeapNodeList<E> heapNodeList = map.get(priority);
        
        if (heapNodeList != null) {
            heapNodeList.add(newNode);
        } else {
            heapNodeList = new HeapNodeList<>();
            heapNodeList.add(newNode);
            map.put(priority, heapNodeList);
        }
        
        ++size;
    }

    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException(
                "This VanEmdeBoasTreeHeap is not indexed.");
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        HeapNodeList<E> heapNodeList = map.getMinimumKeyValue();
        E returnValue = heapNodeList.remove().element;
        
        if (heapNodeList.isEmpty()) {
            map.remove(map.getMinimumKey());
        }
        
        --size;
        return returnValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        map.clear();
        size = 0;
    }
    
    @Override
    public String toString() {
        return "AVLTreeHeap";
    }
    
    /**
     * Makes sure that the heap is not empty, and if it is, throws an exception.
     * 
     * @throws NoSuchElementException if the heap is empty.
     */
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This BinaryHeap is empty.");
        }
    }
}
