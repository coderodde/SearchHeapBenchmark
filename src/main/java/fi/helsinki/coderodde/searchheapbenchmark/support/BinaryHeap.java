package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * This class implements an un-indexed binary heap. "Unindexed" means that this 
 * heap does not map elements to their nodes, for which reason the decrease 
 * operation of the priority key of an element is not implemented.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class BinaryHeap<E, P extends Comparable<? super P>> 
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
    private BinaryHeapNode<E, P>[] binaryHeapNodeArray;
    
    /**
     * Caches the number of elements hold by this heap.
     */
    private int size;
    
    public BinaryHeap() {
        this.binaryHeapNodeArray = new BinaryHeapNode[DEFAULT_CAPACITY];
    }
    
    /**
     * {@inheritDoc } 
     */
    @Override
    public void add(E element, P priority) {
        expandStorageArrayIfNeeded(); // Expand the array if needed.
        
        BinaryHeapNode<E, P> newBinaryHeapNode = new BinaryHeapNode<>(element,
                                                                      priority);
        binaryHeapNodeArray[size] = newBinaryHeapNode;
        siftUp(size++);
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        throw new UnsupportedOperationException("This heap is not indexed.");
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        E element = binaryHeapNodeArray[0].element;
        binaryHeapNodeArray[0] = binaryHeapNodeArray[--size];
        binaryHeapNodeArray[size] = null;
        siftDownRoot();
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
        Arrays.fill(binaryHeapNodeArray, 0, size, null);
        size = 0;
    }
    
    @Override
    public String toString() {
        return "BinaryHeap";
    }
    
    private void siftUp(int index) {
        if (index == 0) {
            // Cannot sift up the root node.
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
            } else {
                // Do this in order to not to check 'minChildNodeIndex == index'
                // This was measured to have a performance advantage.
                binaryHeapNodeArray[minChildNodeIndex] = targetHeapNode;
                return;
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
            
            // Go to the minimum child node:
            binaryHeapNodeArray[index] = binaryHeapNodeArray[minChildNodeIndex];
            index = minChildNodeIndex;
            leftChildNodeIndex = getLeftChildIndex(index);
            rightChildNodeIndex = leftChildNodeIndex + 1;
        }
    }
    
    /**
     * Given the index of a node, this method will return the index of its 
     * parent node.
     * 
     * @param index the index of the start node.
     * @return the index of the parent node of the start node.
     */
    private static int getParentNodeIndex(int index) {
        return (index - 1) >>> 1;
    }
    
    /**
     * Given the index of a node, this method will return the index of its
     * left child node. The index of the right child node may be obtained as
     * {@code getLeftChildIndex(index) + 1}.
     * 
     * @param index the index of the start node.
     * @return the index of the left child node of the start node.
     */
    private static int getLeftChildIndex(int index) {
        return (index << 1) + 1;
    }
    
    /**
     * Expands the storage array by doubling its length.
     */
    private void expandStorageArrayIfNeeded() {
        if (size == binaryHeapNodeArray.length) {
            binaryHeapNodeArray = Arrays.copyOf(binaryHeapNodeArray, 2 * size);
        }
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
