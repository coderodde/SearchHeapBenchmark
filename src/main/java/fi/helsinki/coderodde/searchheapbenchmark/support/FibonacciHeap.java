package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class implements a Fibonacci heap. "Unindexed" means that this heap does 
 * not map elements to their nodes, for which reason the decrease operation of 
 * the priority key of an element is not implemented.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type. 
 */
public final class FibonacciHeap<E, P extends Comparable<? super P>>
implements PriorityQueue<E, P> {

    /**
     * The default length of the root list node array used for consolidation.
     */
    private static final int DEFAULT_CHILD_ARRAY_LENGTH = 5;
    
    /**
     * This class implements the Fibonacci heap nodes.
     * 
     * @param <E> the element type.
     * @param <P> the priority key type.
     */
    private static final class FibonacciHeapNode<E, P> {
        
        /**
         * The actual element.
         */
        private final E element;
        
        /**
         * The priority key of this node.
         */
        private final P priority;
        
        /**
         * The parent node of this node.
         */
        private FibonacciHeapNode<E, P> parent;
        
        /**
         * The left sibling.
         */
        private FibonacciHeapNode<E, P> left = this;
        
        /**
         * The right sibling.
         */
        private FibonacciHeapNode<E, P> right = this;
        
        /**
         * The leftmost child of this node.
         */
        private FibonacciHeapNode<E, P> child;
        
        /**
         * The number of children this node has.
         */
        private int degree;
        
        FibonacciHeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * Fibonacci heap -specific math.
     */
    private static final double LOG_PHI = Math.log((1 + Math.sqrt(5)) / 2);
    
    /**
     * The node with the minimum priority.
     */
    private FibonacciHeapNode<E, P> minimumNode;
    
    /**
     * The number of elements stored in this Fibonacci heap.
     */
    private int size;
    
    /**
     * The cached array for consolidation routine.
     */
    private FibonacciHeapNode<E, P>[] array = 
            new FibonacciHeapNode[DEFAULT_CHILD_ARRAY_LENGTH];
    
   /**
    * {@inheritDoc } 
    */ 
    @Override
    public void add(E element, P priority) {
        FibonacciHeapNode<E, P> node = new FibonacciHeapNode<>(element, 
                                                               priority);
        if (minimumNode != null) {
            node.left = minimumNode;
            node.right = minimumNode.right;
            minimumNode.right = node;
            node.right.left = node;
            
            if (priority.compareTo(minimumNode.priority) < 0) {
                minimumNode = node;
            }
        } else {
            minimumNode = node;
        }
        
        ++size;
    }

   /**
    * {@inheritDoc } 
    */ 
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException(
                "This FibonacciHeap is not indexed."); 
    }

   /**
    * {@inheritDoc } 
    */ 
    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        
        FibonacciHeapNode<E, P> z = minimumNode;
        int numberOfChildren = z.degree;
        FibonacciHeapNode<E, P> x = z.child;
        FibonacciHeapNode<E, P> tmpRight;
        
        while (numberOfChildren > 0) {
            tmpRight = x.right;
            
            x.left.right = x.right;
            x.right.left = x.left;
            
            x.left = minimumNode;
            x.right = minimumNode.right;
            minimumNode.right = x;
            x.right.left = x;
            
            x.parent = null;
            x = tmpRight;
            numberOfChildren--;
        }
        
        z.left.right = z.right;
        z.right.left = z.left;
        
        if (z == z.right) {
            minimumNode = null;
        } else {
            minimumNode = z.right;
            consolidate();
        }
        
        --size;
        return z.element;
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
        minimumNode = null;
        size = 0;
    }
    
    @Override
    public String toString() {
        return "FibonacciHeap";
    }
    
    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(size) / LOG_PHI)) + 1;
        ensureArraySize(arraySize);
        Arrays.fill(array, null);
    
        FibonacciHeapNode<E, P> x = minimumNode;
        int rootListSize = 0;
        
        if (x != null) {
            rootListSize = 1;
            x = x.right;
            
            while (x != minimumNode) {
                rootListSize++;
                x = x.right;
            }
        }
        
        while (rootListSize > 0) {
            int degree = x.degree;
            FibonacciHeapNode<E, P> next = x.right;
            
            while (array[degree] != null) {
                FibonacciHeapNode<E, P> y = array[degree];
                
                if (x.priority.compareTo(y.priority) > 0) {
                    FibonacciHeapNode<E, P> tmp = y;
                    y = x;
                    x = tmp;
                }
                
                link(y, x);
                array[degree] = null;
                degree++;
            }
            
            array[degree] = x;
            x = next;
            rootListSize--;
        }
        
        minimumNode = null;
        
        for (FibonacciHeapNode<E, P> y : array) {
            if (y == null) {
                continue;
            }
            
            if (minimumNode == null) {
                minimumNode = y;
            } else {
                moveToRootList(y);
            }
        }
    }
    
    private void moveToRootList(FibonacciHeapNode<E, P> node) {
        node.left.right = node.right;
        node.right.left = node.left;
        
        node.left = minimumNode;
        node.right = minimumNode.right;
        minimumNode.right = node;
        node.right.left = node;
        
        if (node.priority.compareTo(minimumNode.priority) < 0) {
            minimumNode = node;
        }
    }
    
    private void link(FibonacciHeapNode<E, P> y, FibonacciHeapNode<E, P> x) {
        y.left.right = y.right;
        y.right.left = y.left;
        
        y.parent = x;
        
        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }
        
        ++x.degree;
    }
        
    /**
     * Makes sure that all the root list nodes fit in the array.
     * 
     * @param arraySize new requested size.
     */
    private void ensureArraySize(int arraySize) {
        if (arraySize > array.length) {
            array = new FibonacciHeapNode[arraySize];
        } else {
            Arrays.fill(array, null);
        }
    }
    
    /**
     * Makes sure that the heap is not empty. If it is, an exception is thrown.
     */
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This FibonacciHeap is empty.");
        }
    }
}
