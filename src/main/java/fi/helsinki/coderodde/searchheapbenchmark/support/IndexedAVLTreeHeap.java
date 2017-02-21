package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class IndexedAVLTreeHeap<E, P extends Comparable<? super P>> 
        implements PriorityQueue<E, P> {

    private static final class HeapNode<E, P extends Comparable<? super P>> {
        
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
    
    private static final class 
            HeapNodeList<E, P extends Comparable<? super P>> {
        
        /**
         * The head node of this list.
         */
        HeapNode<E, P> head;
        
        void add(HeapNode<E, P> node) {
            node.next = head;
            
            if (head != null) {
                head.prev = node;
            }
            
            head = node;
        }
        
        HeapNode<E, P> removeHead() {
            HeapNode<E, P> ret = head;
            head = head.next;
            
            if (head != null) {
                head.prev = null;
            }
            
            return ret;
        }
        
        void unlink(HeapNode<E, P> node) {
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
    private final Map<E, HeapNode<E, P>> map = new HashMap<>();
    
    /**
     * Maps each integer priority key to the list of elements with that very
     * priority.
     */
    private final AVLTreeMap<P, HeapNodeList<E, P>> nodeMap;
    
    public IndexedAVLTreeHeap() {
        this.nodeMap = new AVLTreeMap<>();
    }
    
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
        HeapNode<E, P> newNode = new HeapNode<>(element, priority);
        HeapNodeList<E, P> heapNodeList = nodeMap.get(priority);
        
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
    public boolean decreasePriority(E element, P newPriority) {
        HeapNode<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        HeapNodeList<E, P> heapNodeList = nodeMap.get(targetNode.priority);
        heapNodeList.unlink(targetNode);
        
        if (heapNodeList.isEmpty()) {
            nodeMap.remove(targetNode.priority);
        }
        
        targetNode.priority = newPriority;
        
        HeapNodeList<E, P> newHeapNodeList = nodeMap.get(newPriority);
        
        if (newHeapNodeList != null) {
            newHeapNodeList.add(targetNode);
        } else {
            newHeapNodeList = new HeapNodeList<>();
            newHeapNodeList.add(targetNode);
            nodeMap.put(newPriority, newHeapNodeList);
        }
        
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        P minimumPriorityKey = nodeMap.getMinimumKey();
        HeapNodeList<E, P> heapNodeList = nodeMap.get(minimumPriorityKey);
        HeapNode<E, P> heapNode = heapNodeList.removeHead();
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
