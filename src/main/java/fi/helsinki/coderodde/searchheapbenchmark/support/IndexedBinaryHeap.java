package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
/**
 * This class implements an indexed binary heap that supports 
 * {@code decreasePriority} in logarithmic time.
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.6 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class IndexedBinaryHeap<E, P extends Comparable<? super P>> 
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
        
        /**
         * The node array index at which this node is stored.
         */
        int index;
        
        BinaryHeapNode(E element, P priority, int index) {
            this.element = element;
            this.priority = priority;
            this.index = index;
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
    
    /**
     * This map maps each element to its node.
     */
    private final Map<E, BinaryHeapNode<E, P>> map = new HashMap<>();
    
    public IndexedBinaryHeap() {
        this.binaryHeapNodeArray = new BinaryHeapNode[DEFAULT_CAPACITY];
    }
    
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            // This heap already holds the element.
            return;
        }
            
        checkHeapHasSpace();
        
        BinaryHeapNode<E, P> newBinaryHeapNode = new BinaryHeapNode<>(element,
                                                                      priority,
                                                                      size);
        binaryHeapNodeArray[size] = newBinaryHeapNode;
        siftUp(size++);
        map.put(element, newBinaryHeapNode);
    }

    @Override
    public boolean decreasePriority(E element, P newPriority) {
        BinaryHeapNode<E, P> targetNode = map.get(element);
        
        if (targetNode == null) {
            // Element not in this heap.
            return false;
        }
        
        if (targetNode.priority.compareTo(newPriority) <= 0) {
            // Cannot improve the priority of the element.
            return false;
        }
        
        targetNode.priority = newPriority;
        siftUp(targetNode.index);
        return true;
    }

    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        BinaryHeapNode<E, P> topNode = binaryHeapNodeArray[0];
        binaryHeapNodeArray[0] = binaryHeapNodeArray[--size];
        siftDownRoot();
        binaryHeapNodeArray[size] = null;
        E element = topNode.element;
        map.remove(element);
        return element;
    }

    @Override
    public int size() {
        return size;
    }
    
    @Override 
    public void clear() {
        Arrays.fill(binaryHeapNodeArray, 0, size, null);
        map.clear();
        size = 0;
    }
    
    @Override
    public String toString() {
        return "IndexedBinaryHeap";
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
                binaryHeapNodeArray[index] = parentNode;
                parentNode.index = index;
                
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
        targetNode.index = index;
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
                targetHeapNode.index = minChildNodeIndex;
                return;
            }
            
            binaryHeapNodeArray[index] = binaryHeapNodeArray[minChildNodeIndex];
            binaryHeapNodeArray[index].index = index;
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
            throw new NoSuchElementException("This BinaryHeap is empty.");
        }
    }
    
    @Override
    public Map<E, P> getPriorityMap() {
        Map<E, P> m = new HashMap<>();
        
        for (Map.Entry<E, BinaryHeapNode<E, P>> entry : map.entrySet()) {
            m.put(entry.getKey(), entry.getValue().priority);
        }
        return m;
    }
}
