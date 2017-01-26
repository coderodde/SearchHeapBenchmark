package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements an indexed pairing heap. "Indexed" means that this heap
 * maintains internally a hash map mapping each present element to the heap node
 * holding that element. This allows efficient decrease key operation.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 23, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class IndexedPairingHeap<E, P extends Comparable<? super P>>
implements PriorityQueue<E, P> {

    /**
     * This inner static class implements the pairing tree nodes.
     * 
     * @param <E> the element type.
     * @param <P> the priority key type.
     */
    private static final class PairingHeapNode<E, P> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The priority key of the element.
         */
        P priority;
        
        /**
         * The parent node of this node.
         */
        PairingHeapNode<E, P> parent;
        
        /**
         * The next sibling node of this node.
         */
        PairingHeapNode<E, P> next;
        
        /**
         * The leftmost child node of this node.
         */
        PairingHeapNode<E, P> child;
        
        PairingHeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * The number of elements stored in this heap.
     */
    private int size;
    
    /**
     * The root node of this heap.
     */
    private PairingHeapNode<E, P> root;
    
    /**
     * The map mapping each present element to its node.
     */
    private final Map<E, PairingHeapNode<E, P>> map;
    
    /**
     * Used for merging the children of the removed root node.
     */
    private final Deque<PairingHeapNode<E, P>> queue;
    
    public IndexedPairingHeap() {
        this.map = new HashMap<>();
        this.queue = new ArrayDeque<>();
    }
    
    private IndexedPairingHeap(E element, P priority) {
        this.map = null;
        this.queue = null;
        this.root = new PairingHeapNode<>(element, priority);
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // The element is already in this heap.
            return;
        }
        
        PairingHeapNode<E, P> node = new PairingHeapNode<>(element, priority);
        
        if (root == null) {
            root = node;
        } else {
            root = merge(root, node);
        }
            
        map.put(element, node);
        ++size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        PairingHeapNode<E, P> node = map.get(element);
        
        if (node == null) {
            // The element is not in this heap, do no more.
            return false;
        }
        
        if (node.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority.
            return false;
        }
        
        E storeElement = node.element;
        PairingHeapNode<E, P> upperNode = node.parent;
        PairingHeapNode<E, P> lowerNode = node;
        
        while (upperNode != null 
                && upperNode.priority.compareTo(newPriority) > 0) {
            lowerNode.element = upperNode.element;
            lowerNode.priority = upperNode.priority;
            map.put(lowerNode.element, lowerNode);
            
            lowerNode = upperNode;
            upperNode = upperNode.parent;
        }
        
        lowerNode.element = storeElement;
        lowerNode.priority = newPriority;
        map.put(storeElement, lowerNode);
        
        return true;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        
        if (root.child == null) {
            E element = root.element;
            root = null;
            map.remove(element);
            --size;
            return element;
        }
        
        E element = root.element;
        PairingHeapNode<E, P> tmp = root.child;
        PairingHeapNode<E, P> tmp2;
        
        while (tmp != null) {
            queue.addLast(tmp);
            tmp.parent = null;
            tmp2 = tmp;
            tmp = tmp.next;
            tmp2.next = null;
        }
        
        root = mergePairs();
        map.remove(element);
        --size;
        return element;
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
        map.clear();
        root = null;
        size = 0;
    }

    @Override
    public String toString() {
        return "IndexedPairingHeap";
    }
    
    private PairingHeapNode<E, P> merge(PairingHeapNode<E, P> node1,
                                        PairingHeapNode<E, P> node2) {
        if (node1.priority.compareTo(node2.priority) < 0) {
            PairingHeapNode<E, P> oldChild = node1.child;
            node1.child = node2;
            node2.next = oldChild;
            node2.parent = node1;
            return node1;
        } else {
            PairingHeapNode<E, P> oldChild = node2.child;
            node2.child = node1;
            node1.next = oldChild;
            node1.parent = node2;
            return node2;
        }
    }

    private PairingHeapNode<E, P> mergePairs() {
        while (queue.size() > 1) {
            PairingHeapNode<E, P> left = queue.removeFirst();
            PairingHeapNode<E, P> right = queue.removeFirst();
            queue.addLast(merge(left, right));
        }
        
        return queue.removeFirst();
    }
    
    private void checkHeapNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "This IndexedPairingHeap is empty.");
        }
    }
}
