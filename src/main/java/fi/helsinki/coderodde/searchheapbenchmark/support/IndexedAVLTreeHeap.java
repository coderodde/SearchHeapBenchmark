package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class IndexedAVLTreeHeap<E, P extends Comparable<? super P>> 
        implements PriorityQueue<E, P> {

    private static final class HeapNode<E, P> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The element priority.
         */
        P priority;
        
        /**
         * The next node in the collision chain.
         */
        HeapNode<E, P> next;
        
        /**
         * The previous node in the collision chain.
         */
        HeapNode<E, P> prev;
        
        HeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * Caches the number of elements in this heap.
     */
    private int size;
    
    /**
     * Maps each present element to its respective node.
     */
    private final Map<E, HeapNode<E, P>> map = new HashMap<>();
    
    /**
     * Maps each integer priority key to the list of elements with that very
     * priority.
     */
    private final AVLTreeMap<P, HeapNode<E, P>> nodeMap = new AVLTreeMap<>();
    
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
        HeapNode<E, P> newNode = new HeapNode<>(element, priority);
        HeapNode<E, P> heapNodeChainHead = nodeMap.get(priority);
        
        if (heapNodeChainHead == null) {
            nodeMap.put(priority, newNode);
        } else if (heapNodeChainHead.next != null) {
            newNode.prev = heapNodeChainHead;
            newNode.next = heapNodeChainHead.next;
            heapNodeChainHead.next = newNode;
            newNode.next.prev = newNode;
        } else {
            heapNodeChainHead.next = newNode;
            newNode.prev = heapNodeChainHead;
        }
        
        map.put(element, newNode);
        ++size;
    }

    @Override
    public boolean decreasePriority(E element, P newPriority) {
        HeapNode<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
        
        P targetNodePriority = targetNode.priority;
        
        if (targetNodePriority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        // Unlink the targetNode from its current chain.
        if (targetNode.prev == null) {
            // targetNode is the head of its chain.
            HeapNode<E, P> newHead = targetNode.next;
            
            if (newHead == null) {
                // targetNode is the ONLY node in its chain.
                nodeMap.remove(targetNodePriority);
            } else {
                newHead.prev = null;
                nodeMap.put(targetNodePriority, newHead);
            }
        } else {
            HeapNode<E, P> nextNode = targetNode.next;
            HeapNode<E, P> previousNode = targetNode.prev;
            
            previousNode.next = nextNode;
            
            if (nextNode != null) {
                nextNode.prev = previousNode;
            }
        }
        
        // Link the targetNode to its new chain.
        targetNode.priority = newPriority;
        
        HeapNode<E, P> heapNodeChainHead = nodeMap.get(newPriority);
        targetNode.prev = targetNode.next = null;
        
        if (heapNodeChainHead == null) {
            nodeMap.put(newPriority, targetNode);
        } else {
            targetNode.prev = heapNodeChainHead;
            targetNode.next = heapNodeChainHead.next;
            
            if (heapNodeChainHead.next != null) {
                heapNodeChainHead.next.prev = targetNode;
            }
            
            heapNodeChainHead.next = targetNode;
        }
        
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        P minimumPriorityKey = nodeMap.getMinimumKey();
        HeapNode<E, P> heapNodeChainHead = nodeMap.get(minimumPriorityKey);
        E returnValue;
        
        if (heapNodeChainHead.next == null) {
            returnValue = heapNodeChainHead.element;
            nodeMap.remove(heapNodeChainHead.priority);
        } else {
            HeapNode<E, P> removedNode = heapNodeChainHead.next;
            returnValue = removedNode.element;
            heapNodeChainHead.next = removedNode.next;
            
            if (heapNodeChainHead.next != null) {
                heapNodeChainHead.next.prev = heapNodeChainHead;
            }
        }
        
        map.remove(returnValue);
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
        nodeMap.clear();
        size = 0;
    }
    
    @Override
    public String toString() {
        return "IndexedAVLTreeHeap";
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
