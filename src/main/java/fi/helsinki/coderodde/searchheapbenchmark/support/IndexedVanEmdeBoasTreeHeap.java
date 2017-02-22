package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class IndexedVanEmdeBoasTreeHeap<E> 
        implements PriorityQueue<E, Integer> {

    private static final class HeapNode<E> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The element priority.
         */
        Integer priority;
        
        /**
         * The next node in the collision chain.
         */
        HeapNode<E> next;
        
        /**
         * The previous node in the collision chain.
         */
        HeapNode<E> prev;
        
        HeapNode(E element, Integer priority) {
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
    private final Map<E, HeapNode<E>> map = new HashMap<>();
    
    /**
     * Maps each integer priority key to the list of elements with that very
     * priority.
     */
    private final VanEmdeBoasTreeMap<HeapNode<E>> nodeMap;
    
    public IndexedVanEmdeBoasTreeHeap(int universeSize) {
        this.nodeMap = new VanEmdeBoasTreeMap<>(universeSize);
    }
    
    @Override
    public void add(E element, Integer priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
        HeapNode<E> newNode = new HeapNode<>(element, priority);
        HeapNode<E> heapNodeChainHead = nodeMap.get(priority);
        
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
    public boolean decreasePriority(E element, Integer newPriority) {
        HeapNode<E> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
        
        Integer targetNodePriority = targetNode.priority;
        
        if (targetNodePriority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        if (targetNode.next == null) {
            // targetNode is the only node in its chain, remove the chain from
            // the node map.
            nodeMap.remove(targetNodePriority);
        } else if (targetNode.prev == null) {
            // targetNode is not the only node in its heap node chain + it is 
            // the actual head of the chain.
            nodeMap.put(targetNodePriority, targetNode.next);
            targetNode.next.prev = null;
            targetNode.next = null;
        } else {
            // targetNode is not the only node in its heap node chain + it is 
            // not the head of the chain.
            targetNode.prev.next = targetNode.next;
            
            if (targetNode.next != null) {
                targetNode.next.prev = targetNode.prev;
            }
            
            targetNode.next = null;
            targetNode.prev = null;
        }
        
        targetNode.priority = newPriority;
        
        HeapNode<E> heapNodeChainHead = nodeMap.get(newPriority);
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
        Integer minimumPriorityKey = nodeMap.getMinimum();
        HeapNode<E> heapNodeChainHead = nodeMap.get(minimumPriorityKey);
        E returnValue;
        
        if (heapNodeChainHead.next == null) {
            returnValue = heapNodeChainHead.element;
            nodeMap.remove(heapNodeChainHead.priority);
        } else {
            HeapNode<E> removedNode = heapNodeChainHead.next;
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
        return "IndexedVanEmdeBoasTreeHeap";
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
