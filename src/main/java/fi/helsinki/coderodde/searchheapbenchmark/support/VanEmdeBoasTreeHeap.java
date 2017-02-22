package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.NoSuchElementException;

public final class VanEmdeBoasTreeHeap<E> implements PriorityQueue<E, Integer> {

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
    
    /**
     * Maps each used integer priority to the collision chain of 
     * {@code HeapNode} objects.
     */
    private final VanEmdeBoasTreeMap<HeapNode<E>> map;
    
    /**
     * Holds the number of elements currently in this heap.
     */
    private int size;
    
    public VanEmdeBoasTreeHeap(int universe) {
        this.map = new VanEmdeBoasTreeMap<>(universe);
    }
    
    @Override
    public void add(E element, Integer priority) {
        HeapNode<E> newNode = new HeapNode<>(element);
        HeapNode<E> neighborNode = map.get(priority);
        
        if (neighborNode != null) {
            newNode.next = neighborNode.next;
            neighborNode.next = newNode;
        } else {
            map.put(priority, newNode);
        }
        
        ++size;
    }

    @Override
    public boolean decreasePriority(E element, Integer newPriority) {
        throw new UnsupportedOperationException(
                "This VanEmdeBoasTreeHeap is not indexed.");
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        Integer minimumKey = map.getMinimum();
        HeapNode<E> heapNodeChainHead = map.get(minimumKey);
        E returnValue;
        
        if (heapNodeChainHead.next != null) {
            returnValue = heapNodeChainHead.next.element;
            heapNodeChainHead.next = heapNodeChainHead.next.next;
        } else {
            returnValue = heapNodeChainHead.element;
            map.remove(minimumKey);
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
        return "VanEmdeBoasTreeHeap";
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
