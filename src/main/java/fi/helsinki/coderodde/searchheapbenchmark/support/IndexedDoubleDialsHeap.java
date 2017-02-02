package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class IndexedDoubleDialsHeap<E> 
        implements PriorityQueue<E, Double> {

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
    
    /**
     * Maps the element to the node that holds it.
     */
    private final Map<E, DoubleDialsHeapNode<E>> map = new HashMap<>();
    
    public IndexedDoubleDialsHeap(double range) {
        this.range = checkRange(range);
    }
    
    @Override
    public void add(E element, Double priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
        
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
        map.put(element, newNode);
    }

    @Override
    public boolean decreasePriority(E element, Double newPriority) {
        DoubleDialsHeapNode<E> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
            
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        int sourceBucketIndex = (int)(targetNode.priority / range);
        
        // Unlink the target node from its current bucket:
        if (targetNode.prev != null) {
            targetNode.prev.next = targetNode.next;
        } else {
            storageArray[sourceBucketIndex] = targetNode.next;
        }
        
        if (targetNode.next != null) {
            targetNode.next.prev = targetNode.prev;
        }
        
        // Link the target node to its new bucket:
        targetNode.priority = newPriority;
        int targetBucketIndex = (int)(newPriority / range);
        targetNode.next = storageArray[targetBucketIndex];
        
        if (storageArray[targetBucketIndex] != null) {
            storageArray[targetBucketIndex].prev = targetNode;
        }
        
        storageArray[targetBucketIndex] = targetNode;
        targetNode.prev = null;
        minimumBucketIndex = Math.min(minimumBucketIndex, targetBucketIndex);
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapNotEmpty();
        DoubleDialsHeapNode<E> targetNode = findMinimumPriorityNode();
        E element = targetNode.element;
        --size;
        unlink(targetNode);
        map.remove(element);
        return element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(storageArray, null);
        map.clear();
        size = 0;
    }
    
    @Override
    public String toString() {
        return "IndexedDoubleDialsHeap, range = " + range;
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
            throw new NoSuchElementException(
                    "This IndexedDoubleDialsHeap is empty.");
        }
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
        } else if ((storageArray[minimumBucketIndex] = node.next) == null) {
            updateMinimumBucketIndex();
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
