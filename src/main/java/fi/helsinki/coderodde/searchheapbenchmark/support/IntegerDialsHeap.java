package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class implements the integer version of the Dial's heap, mapping each
 * (non-negative) priority to the list of elements holding that priority. This
 * version is unindexed, which implies that this class does not provide the 
 * method for raising priorities of elements in this heap.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 30, 2017)
 * 
 * @param <E> the actual type of elements.
 */
public final class IntegerDialsHeap<E> implements PriorityQueue<E, Integer> {

    private static final int DEFAULT_STORAGE_ARRAY_LENGTH = 1024;
    
    private static final class DialsHeapNode<E> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The integer priority of the element.
         */
        Integer priority;
        
        /**
         * The sibling node in the collision chain.
         */
        DialsHeapNode<E> next;
        
        DialsHeapNode(E element, Integer priority, DialsHeapNode<E> next) {
            this.element = element;
            this.priority = priority;
            this.next = next;
        }
    }
    
    /**
     * This array maps integer priority keys to the their respective node lists.
     */
    private DialsHeapNode<E>[] storageArray =
            new DialsHeapNode[DEFAULT_STORAGE_ARRAY_LENGTH];
    
    /**
     * The number of elements stored in this heap.
     */
    private int size;
    
    /**
     * Caches the lowest priority over all nodes in the heap.
     */
    private int minimumPriority = Integer.MAX_VALUE;
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void add(E element, Integer priority) {
        ensureCapacityFor(priority);
        storageArray[priority] = new DialsHeapNode<>(element, 
                                                     priority,
                                                     storageArray[priority]);
        
        minimumPriority = Math.min(minimumPriority, priority);
        ++size;
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public boolean decreasePriority(E element, Integer newPriority) {
        throw new UnsupportedOperationException("This heap is not indexed.");
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        DialsHeapNode<E> node = storageArray[minimumPriority];
        DialsHeapNode<E> nextNode = (storageArray[minimumPriority] = node.next);
        --size;
        
        if (size == 0) {
            minimumPriority = Integer.MAX_VALUE;
        } else if (nextNode == null) {
            for (int priority = minimumPriority + 1; 
                    priority != storageArray.length; 
                    priority++) {
                if (storageArray[priority] != null) {
                    minimumPriority = priority;
                    break;
                }
            }
        }
            
        return node.element;
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public void clear() {
        Arrays.fill(storageArray, null);
        size = 0;
    }
    
    @Override
    public String toString() {
        return "IntegerDialsHeap";
    }
    
    private void ensureCapacityFor(int priority) {
        if (priority < storageArray.length) {
            return;
        }
        
        int newCapacity = Math.max(priority + 1, 2 * storageArray.length);
        storageArray = Arrays.copyOf(storageArray, newCapacity);
    }
    
    private void checkHeapNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This DialsHeap is empty.");
        }
    }
}
