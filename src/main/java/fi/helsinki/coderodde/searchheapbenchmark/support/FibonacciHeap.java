package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.NoSuchElementException;

/**
 * This class implements a Fibonacci heap.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type. 
 */
public final class FibonacciHeap<E, P extends Comparable<? super P>>
implements PriorityQueue<E, P> {

    private static final int DEFAULT_CHILD_ARRAY_LENGTH = 10;
    
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
        private P priority;
        
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
         * The number of children of this node.
         */
        private FibonacciHeapNode<E, P> child;
        
        /**
         * The number of children this node has.
         */
        private int degree;
        
        /**
         * Indicates whether this node has lost a child since the last time this
         * node was made the child of another node.
         */
        private boolean marked;
        
        FibonacciHeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
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

    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException(
                "This FibonacciHeap is not indexed."); 
    }

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

    @Override
    public int size() {
        return size;
    }

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
        
        for (int i = 0; i != arraySize; ++i) {
            array[i] = null;
        }
        
        int numberOfRoots = 0;
        FibonacciHeapNode<E, P> x = minimumNode;
        
        if (x != null) {
            ++numberOfRoots;
            x = x.right;
            
            while (x != minimumNode) {
                ++numberOfRoots;
                x = x.right;
            }
        }
        
        while (numberOfRoots > 0) {
            int degree = x.degree;
            FibonacciHeapNode<E, P> next = x.right;
            
            while (true) {
                FibonacciHeapNode<E, P> y = array[degree];
                
                if (y == null) {
                    break;
                }
                
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
            numberOfRoots--;
        }
        
        minimumNode = null;
        
        for (FibonacciHeapNode<E, P> y : array) {
            if (y == null) {
                continue;
            }
            
            if (minimumNode != null) {
                y.left.right = y.right;
                y.right.left = y.left;
                
                y.left = minimumNode;
                y.right = minimumNode.right;
                minimumNode.right = y;
                y.right.left = y;
                
                if (y.priority.compareTo(minimumNode.priority) < 0) {
                    minimumNode = y;
                }
            } else {
                minimumNode = y;
            }
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
    
    private void ensureArraySize(int arraySize) {
        if (array.length < arraySize) {
            array = new FibonacciHeapNode[arraySize];
        }
    }
        
    
    /**
     * Makes sure that the heap is not empty. If it is, an exception is thrown.
     */
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This DaryHeap is empty.");
        }
    }
}