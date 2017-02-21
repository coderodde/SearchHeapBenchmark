package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    private static final class HeapNodeList<E> {
        
        /**
         * The head node of this list.
         */
        HeapNode<E> head;
        
        void add(HeapNode<E> node) {
            node.next = head;
            
            if (head != null) {
                head.prev = node;
            }
            
            head = node;
        }
        
        HeapNode<E> removeHead() {
            HeapNode<E> ret = head;
            head = head.next;
            
            if (head != null) {
                head.prev = null;
            }
            
            return ret;
        }
        
        void unlink(HeapNode<E> node) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }
            
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            
            node.next = null;
            node.prev = null;
        }
        
        boolean isEmpty() {
            return head == null;
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
    private final VanEmdeBoasTreeMap<HeapNodeList<E>> nodeMap;
    
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
        HeapNodeList<E> heapNodeList = nodeMap.get(priority);
        
        if (heapNodeList != null) {
            heapNodeList.add(newNode);
        } else {
            heapNodeList = new HeapNodeList<>();
            heapNodeList.add(newNode);
            nodeMap.put(priority, heapNodeList);
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
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        HeapNodeList<E> heapNodeList = nodeMap.get(targetNode.priority);
        heapNodeList.unlink(targetNode);
        
        if (heapNodeList.isEmpty()) {
            nodeMap.remove(targetNode.priority);
        }
        
        targetNode.priority = newPriority;
        
        HeapNodeList<E> newHeapNodeList = nodeMap.get(newPriority);
        
        if (newHeapNodeList != null) {
            newHeapNodeList.add(targetNode);
        } else {
            newHeapNodeList = new HeapNodeList<>();
            newHeapNodeList.add(targetNode);
            nodeMap.put(newPriority, newHeapNodeList);
        }
        
        ++size;
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        Integer minimumKey = nodeMap.getMinimum();
        HeapNodeList<E> heapNodeList = nodeMap.get(minimumKey);
        HeapNode<E> heapNode = heapNodeList.removeHead();
        E returnValue = heapNode.element;
        
        if (heapNodeList.isEmpty()) {
            nodeMap.remove(heapNode.priority);
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
