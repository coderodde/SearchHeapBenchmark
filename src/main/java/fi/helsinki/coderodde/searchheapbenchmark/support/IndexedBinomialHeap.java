package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class implements a binomial heap.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class IndexedBinomialHeap<E, P extends Comparable<? super P>> 
implements PriorityQueue<E, P> {

    /**
     * This class implements a binomial tree.
     * 
     * @param <E> the element type.
     * @param <P> the priority key of the element.
     */
    private static final class BinomialTree<E, P> {
        
        /**
         * The actual element of this node.
         */
        E element;
        
        /**
         * The priority of the element.
         */
        P priority;
        
        /**
         * The parent node.
         */
        BinomialTree<E, P> parent;
        
        /**
         * Immediate sibling of this node to the right.
         */
        BinomialTree<E, P> sibling;
        
        /**
         * The leftmost child of this node.
         */
        BinomialTree<E, P> child;
        
        /**
         * The number of children of this node.
         */
        int degree;
        
        BinomialTree(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }

    /**
     * The number of elements this heap currently stores.
     */
    private int size;

    /**
     * The leftmost node in the root list of this heap.
     */
    private BinomialTree<E, P> head;

    /**
     * The binomial tree with the highest priority.
     */
    private BinomialTree<E, P> minimumTree;
        
    /**
     * Maps each element to the binomial tree node it is stored in.
     */
    private final Map<E, BinomialTree<E, P>> map;
    
    /**
     * Constructs a new empty binomial heap.
     */
    public IndexedBinomialHeap() {
        map = new HashMap<>();
    }

    /**
     * Used for the insertion operation.
     * 
     * @param element  the only element of the new heap.
     * @param priority the priority of the element.
     */
    private IndexedBinomialHeap(E element, P priority) {
        BinomialTree<E, P> tree = new BinomialTree<>(element, priority);
        head = tree;
        minimumTree = tree;
        size = 1;
        map = null;
    }
    
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
        IndexedBinomialHeap<E, P> h = new IndexedBinomialHeap<>(element, 
                                                                priority);
        
        if (size == 0) {
            this.head = h.head;
            this.minimumTree = h.head;
            this.size = 1;
            this.map.put(element, this.head);
        } else {
            heapUnion(h.head);
            this.map.put(element, h.head);
            size++;
            
            if (minimumTree.priority.compareTo(h.minimumTree.priority) > 0) {
                minimumTree = h.minimumTree;
            }
        }
    }
    
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        BinomialTree<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // The element is not present in this binomial heap.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        E storeElement = targetNode.element;
        BinomialTree<E, P> upperNode = targetNode.parent;
        BinomialTree<E, P> lowerNode = targetNode;
        
        while (upperNode != null 
                && upperNode.priority.compareTo(newPriority) > 0) {
            lowerNode.element = upperNode.element;
            lowerNode.priority = upperNode.priority;
            
            lowerNode = upperNode;
            upperNode = upperNode.parent;
        }
        
        lowerNode.element = storeElement;
        lowerNode.priority = newPriority;
        
        if (minimumTree.priority.compareTo(lowerNode.priority) > 0) {
            minimumTree = lowerNode;
        }
        
        return true;
    }
    
    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        
        BinomialTree<E, P> x = head;
        BinomialTree<E, P> prevx = null;
        BinomialTree<E, P> best = x;
        BinomialTree<E, P> bestprev = null;
        
        P minPriorityKey = x.priority;
        
        while (x != null) {
            if (minPriorityKey.compareTo(x.priority) > 0) {
                minPriorityKey = x.priority;
                best = x;
                bestprev = prevx;
            }
            
            prevx = x;
            x = x.sibling;
        }
        
        if (bestprev == null) {
            head = best.sibling;
        } else {
            bestprev.sibling = best.sibling;
        }
        
        heapUnion(reverseRootList(best.child));
        
        if (--size > 0) {
            BinomialTree<E, P> minTree = head;
            BinomialTree<E, P> t = head.sibling;
            P minPriority = head.priority;
            
            while (t != null) {
                if (minPriority.compareTo(t.priority) > 0) {
                    minPriority = t.priority;
                    minTree = t;
                }
                
                t = t.sibling;
            }
            
            minimumTree = minTree;
        }
        
        E element = best.element;
        map.remove(element);
        return element;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public void clear() {
        this.head = null;
        this.size = 0;
    }
    
    @Override
    public String toString() {
        return "BinomialHeap";
    }
    
    private BinomialTree<E, P> mergeRoots(BinomialTree<E, P> other) {
        BinomialTree<E, P> a = head;
        BinomialTree<E, P> b = other;
        
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }
        
        BinomialTree<E, P> rootListHead;
        BinomialTree<E, P> rootListTail;
        
        if (a.degree < b.degree) {
            rootListHead = a;
            rootListTail = a;
            a = a.sibling;
        } else {
            rootListHead = b;
            rootListTail = b;
            b = b.sibling;
        }
        
        while (a != null & b != null) {
            if (a.degree < b.degree) {
                rootListTail.sibling = a;
                rootListTail = a;
                a = a.sibling;
            } else {
                rootListTail.sibling = b;
                rootListTail = b;
                b = b.sibling;
            }
        }
        
        if (a != null) {
            rootListTail.sibling = a;
        } else {
            rootListTail.sibling = b;
        }
        
        return rootListHead;
    }
    
    private void heapUnion(BinomialTree<E, P> other) {
        if (other == null) {
            return;
        }
        
        BinomialTree<E, P> t = mergeRoots(other);
        BinomialTree<E, P> prev = null;
        BinomialTree<E, P> x = t;
        BinomialTree<E, P> next = x.sibling;
        
        while (next != null) {
            if ((x.degree != next.degree)
                    || (next.sibling != null
                        && next.sibling.degree == x.degree)) {
                prev = x;
                x = next;
            } else if (x.priority.compareTo(next.priority) <= 0) {
                x.sibling = next.sibling;
                link(next, x);
            } else {
                if (prev == null) {
                    t = next;
                } else {
                    prev.sibling = next;
                }
                
                link(x, next);
                x = next;
            }
            
            next = x.sibling;
        }
        
        this.head = t;
    }
    
    private void link(BinomialTree<E, P> child, BinomialTree<E, P> parent) {
        child.parent = parent;
        child.sibling = parent.child;
        parent.child = child;
        parent.degree++;
    }
    
    private BinomialTree<E, P> reverseRootList(BinomialTree<E, P> first) {
        BinomialTree<E, P> tmp = first;
        BinomialTree<E, P> tmpnext;
        BinomialTree<E, P> newHead = null;
        
        while (tmp != null) {
            tmpnext = tmp.sibling;
            tmp.sibling = newHead;
            newHead = tmp;
            tmp = tmpnext;
        }
        
        return newHead;
    }
    
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This BinaryHeap is empty.");
        }
    }
    
    public static void main(String[] args) {
        Random random = new Random();
        PriorityQueue<Integer, Integer> heap = new IndexedBinomialHeap<>();
        heap.add(1, 1);
        heap.add(2, 2);
        heap.decreasePriority(2, 0);
        
        /*
        
        for (int i = 0; i < 1_000_000; ++i) {
            heap.add(i, i);
        }
        
        long start = System.currentTimeMillis();
        
        for (int i = 500_000; i < 1_000_000; ++i) {
            heap.decreasePriority(i, i - 1_000_000);
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("Time: " + (end - start) + " milliseconds.");*/
    }
}
