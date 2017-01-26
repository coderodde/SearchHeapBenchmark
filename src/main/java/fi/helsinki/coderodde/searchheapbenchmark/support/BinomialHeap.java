package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.NoSuchElementException;

/**
 * This class implements an unindexed binomial heap. "Unindexed" means that this 
 * heap does not map elements to their nodes, for which reason the decrease 
 * operation of the priority key of an element is not implemented.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class BinomialHeap<E, P extends Comparable<? super P>> 
implements PriorityQueue<E, P> {

    /**
     * This class implements a binomial tree. A tree may have other trees as its
     * children.
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
     * Constructs a new empty binomial heap.
     */
    public BinomialHeap() {
        
    }

    /**
     * Used for the insertion operation.
     * 
     * @param element  the only element of the new heap.
     * @param priority the priority of the element.
     */
    private BinomialHeap(E element, P priority) {
        head = new BinomialTree<>(element, priority);
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void add(E element, P priority) {
        BinomialHeap<E, P> h = new BinomialHeap<>(element, priority);
        
        if (size == 0) {
            this.head = h.head;
        } else {
            heapUnion(h.head);
        }
        
        size++;
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException(
                "This BinomialHeap is un-indexed.");
    }
    
    /**
     * {@inheritDoc } 
     */
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
        
        BinomialTree<E, P> child = best.child;
        
        while (child != null) {
            child.parent = null; // The children will be moved to the root list.
                                 // For that reason, we have to set their parent
                                 // links to null.
            child = child.sibling; // Process the next child node.
        }
        
        // Both root list and children list are sorted by degree, yet in
        // opposite direction, so we need to reverse the child list so the two
        // may be merged.
        heapUnion(reverseRootList(best.child));
        --size;
        return best.element;
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
        this.head = null;
        this.size = 0;
    }
    
    @Override
    public String toString() {
        return "BinomialHeap";
    }
    
    /**
     * Both {@code this.head} and {@code other.head} are sorted by degree, so
     * in this method we merge the two lists such the entire merged list remains
     * sorted by the node degrees.
     * 
     * @param other another list to merge.
     * @return the head node of the entire list holding the least degree.
     */
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
        
        // Initialize the lists:
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
        
        // One of the lists are iterated over, just merge the leftovers.
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
    
    /**
     * Make the node {@code child} a leftmost child of {@code parent}.
     * 
     * @param child  the node to make the child of parent of.
     * @param parent the parent node.
     */
    private void link(BinomialTree<E, P> child, BinomialTree<E, P> parent) {
        child.parent = parent;
        child.sibling = parent.child;
        parent.child = child;
        parent.degree++;
    }
    
    /**
     * Reverses the tree list so that it might be merged with the root list.
     * 
     * @param first the first node of the list.
     * @return the new head node.
     */
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
}
