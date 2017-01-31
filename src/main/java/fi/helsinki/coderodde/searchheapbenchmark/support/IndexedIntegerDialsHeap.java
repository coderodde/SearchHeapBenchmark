package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements the integer version of the Dial's heap, that maps each
 * integer priority to the list of elements holding that priority key. This 
 * version is indexed, which implies that the method for improving priority keys
 * is available.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 30, 2017)
 * 
 * @param <E> the actual element type.
 */
public final class IndexedIntegerDialsHeap<E> implements PriorityQueue<E, Integer> {

    private static final int DEFAULT_STORAGE_ARRAY_LENGTH = 1024;

    private static final class DialsHeapNode<E> {
        
        /**
         * The actual element.
         */
        final E element;
        
        /**
         * The integer priority key.
         */
        Integer priority;
        
        /**
         * The previous node in the collision chain.
         */
        DialsHeapNode<E> prev;
        
        /**
         * The next node in the collision chain.
         */
        DialsHeapNode<E> next;
        
        DialsHeapNode(E element, Integer priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
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
     * This map maps each element to the node that contains it.
     */
    private final Map<E, DialsHeapNode<E>> map = new HashMap<>();
    
    @Override
    public void add(E element, Integer priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
        ensureCapacityFor(priority);
        
        DialsHeapNode<E> newDialsHeapNode = new DialsHeapNode<>(element, 
                                                                priority);
        DialsHeapNode<E> oldHead = storageArray[priority];
        
        if (oldHead != null) {
            newDialsHeapNode.next = oldHead;
            oldHead.prev = newDialsHeapNode;
        } 
        
        storageArray[priority] = newDialsHeapNode;
        minimumPriority = Math.min(minimumPriority, priority);
        size++;
        map.put(element, newDialsHeapNode);
    }

    @Override
    public boolean decreasePriority(E element, Integer newPriority) {
        DialsHeapNode<E> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this map.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        DialsHeapNode<E> nextNode = targetNode.next;
        
        // Unlink 'targetNode':
        if (targetNode.prev == null) {
            storageArray[targetNode.priority] = nextNode;
            
            if (nextNode != null) {
                nextNode.prev = null;
            }
        } else {
            targetNode.prev.next = nextNode;
            
            if (nextNode != null) {
                nextNode.prev = targetNode.prev;
            }
        }
        
        // Prepend to the new bucket:
        targetNode.priority = newPriority;
        targetNode.next = storageArray[newPriority];
        
        if (storageArray[newPriority] != null) {
            storageArray[newPriority].prev = targetNode;
        }
        
        minimumPriority = Math.min(minimumPriority, newPriority);
        storageArray[newPriority] = targetNode;
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        
        DialsHeapNode<E> node = storageArray[minimumPriority];
        storageArray[minimumPriority] = node.next;
        --size;
        
        if (storageArray[minimumPriority] != null) {
            storageArray[minimumPriority].prev = null;
        } else {
            if (size == 0) {
                minimumPriority = Integer.MAX_VALUE;
            } else {
                for (int priority = minimumPriority + 1; 
                        priority < storageArray.length; 
                        priority++) {
                    if (storageArray[priority] != null) {
                        minimumPriority = priority;
                        break;
                    }
                }
            }
        }
        
        E element = node.element;
        map.remove(element);
        return element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(storageArray, null);
        map.clear();
        size = 0;
    }
    
    private void checkHeapNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This DialsHeap is empty.");
        }
    }
    
    private void ensureCapacityFor(int priority) {
        if (priority < storageArray.length) {
            return;
        }
        
        int newCapacity = Math.max(priority + 1, 2 * storageArray.length);
        storageArray = Arrays.copyOf(storageArray, newCapacity);
    }
}
