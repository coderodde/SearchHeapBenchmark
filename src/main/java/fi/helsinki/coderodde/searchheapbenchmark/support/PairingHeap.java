package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements an unindexed pairing heap. "Unindexed" means that this 
 * heap does not map elements to their nodes, for which reason the decrease 
 * operation of the priority key of an element is not implemented.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 23, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class PairingHeap<E, P extends Comparable<? super P>>
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
     * Used for merging the children of the removed root node.
     */
    private final Deque<PairingHeapNode<E, P>> queue;
    
    public PairingHeap() {
        this.queue = new ArrayDeque<>();
    }
    
    private PairingHeap(E element, P priority) {
        this.queue = null;
        this.root = new PairingHeapNode<>(element, priority);
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void add(E element, P priority) {
        PairingHeapNode<E, P> node = new PairingHeapNode<>(element, priority);
        
        if (root == null) {
            root = node;
        } else {
            root = merge(root, node);
        }
            
        ++size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException(
                "PairingHeap.decreasePriority is not implemented.");
    }

    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        
        if (root.child == null) {
            E element = root.element;
            root = null;
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
        --size;
        return element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public String toString() {
        return "PairingHeap";
    }
    
    private PairingHeapNode<E, P> merge(PairingHeapNode<E, P> node1,
                                        PairingHeapNode<E, P> node2) {
        /*if (node1 == null) {
            return node2;
        }
        
        if (node2 == null) {
            return node1;
        }*/
        
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
