package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;

public class BinaryHeap<E, P extends Comparable<? super P>> 
        implements PriorityQueue<E, P> {

    /**
     * This class bundles the element and its priority.
     * 
     * @param <E> the element type.
     * @param <P> the priority type;
     */
    private static final class BinaryHeapNode<E, P> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The priority of {@code element}.
         */
        P priority;
        
        BinaryHeapNode(E element, P priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * The default capacity of the underlying array.
     */
    private static final int DEFAULT_CAPACITY = 1024;
    
    /**
     * Stores the actual array of binary heap nodes.
     */
    private BinaryHeapNode[] binaryHeapNodeArray;
    
    /**
     * Caches the number of elements hold by this heap.
     */
    private int size;
    
    public BinaryHeap() {
        this.binaryHeapNodeArray = new BinaryHeapNode[DEFAULT_CAPACITY];
    }
    
    @Override
    public void add(E element, P priority) {
        checkHeapHasSpace();
        
        BinaryHeapNode<E, P> newBinaryHeapNode = new BinaryHeapNode<>(element,
                                                                      priority);
        binaryHeapNodeArray[size] = newBinaryHeapNode;
        siftUp(size++);
    }

    @Override
    public void decreasePrioirty(E element, P newPriority) {
        throw new UnsupportedOperationException("This heap is not indexed.");
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        BinaryHeapNode<E, P> topNode = binaryHeapNodeArray[0];
        binaryHeapNodeArray[0] = binaryHeapNodeArray[--size];
        siftDownRoot();
        return topNode.element;
    }

    @Override
    public int size() {
        return size;
    }
    
    @Override 
    public void clear() {
        size = 0;
        Arrays.fill(binaryHeapNodeArray, null);
    }
    
    private void siftUp(int index) {
        if (index == 0) {
            return;
        }
     
        BinaryHeapNode<E, P> targetNode = binaryHeapNodeArray[index];
        P targetNodePriority = targetNode.priority;
        int parentNodeIndex = getParentNodeIndex(index);
        
        while (true) {
            BinaryHeapNode<E, P> parentNode = 
                    binaryHeapNodeArray[parentNodeIndex];
            
            P parentNodePriority = parentNode.priority;
            
            if (targetNodePriority.compareTo(parentNodePriority) < 0) {
                binaryHeapNodeArray[index] =
                        binaryHeapNodeArray[parentNodeIndex];
                
                index = parentNodeIndex;
                parentNodeIndex = getParentNodeIndex(index);
            } else {
                break;
            }
            
            if (index == 0) {
                break;
            }
        }
        
        binaryHeapNodeArray[index] = targetNode;
    }
    
    private void siftDownRoot() {
        int index = 0;
        int leftChildNodeIndex = getLeftChildIndex(0);
        int rightChildNodeIndex = leftChildNodeIndex + 1;
        int minChildNodeIndex = 0;
        
        BinaryHeapNode<E, P> targetHeapNode = binaryHeapNodeArray[0];
        BinaryHeapNode<E, P> leftChildNode = null;
        
        while (true) {
            if (leftChildNodeIndex < size) {
                leftChildNode = binaryHeapNodeArray[leftChildNodeIndex];
                
                if (leftChildNode.priority
                        .compareTo(targetHeapNode.priority) < 0) {
                    minChildNodeIndex = leftChildNodeIndex;
                }
            } 
            
            if (minChildNodeIndex == index) {
                if (rightChildNodeIndex < size) {
                    BinaryHeapNode<E, P> rightChildNode = 
                            binaryHeapNodeArray[rightChildNodeIndex];
                    
                    if (rightChildNode.priority
                            .compareTo(targetHeapNode.priority) < 0) {
                        minChildNodeIndex = rightChildNodeIndex;
                    }
                }
            } else {
                if (rightChildNodeIndex < size) {
                    BinaryHeapNode<E, P> rightChildNode = 
                            binaryHeapNodeArray[rightChildNodeIndex];
                    
                    if (rightChildNode.priority
                            .compareTo(leftChildNode.priority) < 0) {
                        minChildNodeIndex = rightChildNodeIndex;
                    }
                }
            }
            
            if (minChildNodeIndex == index) {
                binaryHeapNodeArray[minChildNodeIndex] = targetHeapNode;
                return;
            }
            
            binaryHeapNodeArray[index] = binaryHeapNodeArray[minChildNodeIndex];
            index = minChildNodeIndex;
            leftChildNodeIndex = getLeftChildIndex(index);
            rightChildNodeIndex = leftChildNodeIndex + 1;
        }
    }
    
    private static int getParentNodeIndex(int index) {
        return (index - 1) >>> 1;
    }
    
    private static int getLeftChildIndex(int index) {
        return (index << 1) + 1;
    }
    
    private void checkHeapHasSpace() {
        if (size == binaryHeapNodeArray.length) {
            binaryHeapNodeArray = Arrays.copyOf(binaryHeapNodeArray, 2 * size);
        }
    }
    
    private void checkHeapIsNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("This BinaryHeap is empty.");
        }
    }
}
