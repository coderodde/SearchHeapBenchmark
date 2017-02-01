package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class implements the Dial's heap with double priority keys. The class 
 * requires a range parameter, which defines the "width" of the buckets. Also,
 * this version is not indexed, so it does not provide the decrease key method.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 31, 2017)
 * 
 * @param <E> the actual element type.
 */
public final class DoubleDialsHeap<E> implements PriorityQueue<E, Double> {

    private static final int DEFAULT_STORAGE_ARRAY_LENGTH = 1024;
    
    private static final class DoubleDialsHeapNode<E> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The priority of the element.
         */
        Double priority;
        
        /**
         * The previous node in the bucket.
         */
        DoubleDialsHeapNode<E> prev;
        
        /**
         * The next node in the bucket.
         */
        DoubleDialsHeapNode<E> next;
        
        DoubleDialsHeapNode(E element, Double priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * The "width" of each bucket.
     */
    private final double range;
    
    /**
     * Holds the index of the bucket containing the minimum element.
     */
    private int minimumBucketIndex = Integer.MAX_VALUE;
    
    /**
     * Maps the bucket indices to their respective chains of nodes.
     */
    private DoubleDialsHeapNode<E>[] storageArray = 
            new DoubleDialsHeapNode[DEFAULT_STORAGE_ARRAY_LENGTH];
    
    /**
     * Caches the number of elements in this heap.
     */
    private int size;
    
    public DoubleDialsHeap(double range) {
        this.range = checkRange(range);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void add(E element, Double priority) {
        ensureCapacityFor(priority);
        DoubleDialsHeapNode<E> newNode = new DoubleDialsHeapNode<>(element,
                                                                   priority);
        int bucketIndex = (int)(priority / range);
        minimumBucketIndex = Math.min(minimumBucketIndex, bucketIndex);
        
        newNode.next = storageArray[bucketIndex];
        
        if (newNode.next != null) {
            newNode.next.prev = newNode;
        }
        
        storageArray[bucketIndex] = newNode;
        size++;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean decreasePriority(E element, Double newPriority) {
        throw new UnsupportedOperationException(
                "This DoubleDialsHeap is not indexed."); 
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        DoubleDialsHeapNode<E> targetNode = findMinimumPriorityNode();
        E element = targetNode.element;
        --size;
        unlink(targetNode); // 'unlink' relies on 'size'.
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
        Arrays.fill(storageArray, null);
        size = 0;
    }
    
    @Override
    public String toString() {
        return "DoubleDialsHeap, range = " + range;
    }
    
    private DoubleDialsHeapNode<E> findMinimumPriorityNode() {
        DoubleDialsHeapNode<E> bestNode = null;
        DoubleDialsHeapNode<E> currentNode = storageArray[minimumBucketIndex];
        double bestPriority = Double.POSITIVE_INFINITY;
        
        while (currentNode != null) {
            Double currentPriority = currentNode.priority;
            
            if (bestPriority > currentPriority) {
                bestPriority = currentPriority;
                bestNode = currentNode;
            }
            
            currentNode = currentNode.next;
        }
        
        return bestNode;
    }

    private void unlink(DoubleDialsHeapNode<E> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            if ((storageArray[minimumBucketIndex] = node.next) == null) {
                updateMinimumBucketIndex();
            }
        }
        
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }
    
    private void updateMinimumBucketIndex() {
        if (size == 0) {
            minimumBucketIndex = Integer.MAX_VALUE;
            return;
        } 
        
        for (int i = minimumBucketIndex + 1; i < storageArray.length; ++i) {
            if (storageArray[i] != null) {
                minimumBucketIndex = i;
                return;
            }
        }
    }
    
    private double checkRange(double range) {
        if (Double.isNaN(range)) {
            throw new IllegalArgumentException("The range is NaN.");
        }
        
        if (range <= 0.0) {
            throw new IllegalArgumentException(
                    "The range is not positive: " + range + ".");
        }
        
        return range;
    }
    
    private void checkHeapNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This DoubleDialsHeap is empty.");
        }
    }
    
    private void ensureCapacityFor(Double priority) {
        int bucketIndex = (int)(priority / range);
        int requestedBucketSize = bucketIndex + 1;
        
        if (requestedBucketSize > storageArray.length) {
            storageArray = 
                    Arrays.copyOf(storageArray, 
                                  Math.max(requestedBucketSize, 
                                           2 * storageArray.length));
        }
    }
}
