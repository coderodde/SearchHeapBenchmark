package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * This class implements {@code d}-ary heap. {@code d} is the "degree" of the
 * heap, or ,namely, the maximum amount of children of a node. 
 * 
 * @author Rodion "(code)rodde" Efremov
 * @version 1.61 (Jan 19, 2017)
 * 
 * @param <E> the element type.
 * @param <P> the priority key type.
 */
public final class IndexedDaryHeap<E, P extends Comparable<? super P>> 
        implements PriorityQueue<E, P> {
    
    /**
     * The default storage capacity.
     */
    private static final int DEFAULT_CAPACITY = 1024;
    
    /**
     * The minimum degree of the heaps.
     */
    private static final int MINIMUM_DEGREE = 2;
    
    /**
     * Stores an element, its priority, and its index in the storage array.
     * 
     * @param <E> the element type.
     * @param <P> the priority key type.
     */
    private static final class DaryHeapNode<E, P> {
        
        DaryHeapNode(E element, P priority, int index) {
            this.element = element;
            this.priority = priority;
            this.index = index;
        }
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The priority of the element.
         */
        P priority;
        
        /**
         * The index of the node in the node array.
         */
        int index;
    }
    
    /**
     * The actual degree of this heap.
     */
    private final int degree;
    
    /**
     * The actual storage array.
     */
    private DaryHeapNode<E, P>[] daryHeapNodeArray;
    
    /**
     * Holds the array of indices as to avoid creating index arrays
     * every time we are doing something.
     */
    private final int[] indices;
    
    /**
     * Caches the amount of elements in this heap.
     */
    private int size;
    
    /**
     * This map maps each element to its node.
     */
    private final Map<E, DaryHeapNode<E, P>> map = new HashMap<>();
    
    public IndexedDaryHeap(int degree) {
        this.degree = Math.max(degree, MINIMUM_DEGREE); 
        indices = new int[this.degree];
        daryHeapNodeArray = new DaryHeapNode[DEFAULT_CAPACITY];
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void add(E element, P priority) {
        if (map.containsKey(element)) {
            return;
        }
        
        checkHeapHasSpace();
        DaryHeapNode<E, P> newDaryHeapNode = new DaryHeapNode<>(element,
                                                                priority,
                                                                size);
        daryHeapNodeArray[size] = newDaryHeapNode;
        siftUp(size++);
        map.put(element, newDaryHeapNode);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean decreasePriority(E element, P newPriority) {
        DaryHeapNode<E, P> targetNode = map.get(element);
        
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

    /**
     * {@inheritDoc }
     */
    @Override
    public E extractMinimum() {
        checkHeapIsNotEmpty();
        DaryHeapNode<E, P> topNode = daryHeapNodeArray[0];
        daryHeapNodeArray[0] = daryHeapNodeArray[--size];
        daryHeapNodeArray[0].index = 0;
        siftDownRoot();
        daryHeapNodeArray[size] = null;
        E element = topNode.element;
        map.remove(element);
        return element;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        Arrays.fill(daryHeapNodeArray, 0, size, null);
        size = 0;
        map.clear();
    }

    /**
     * Returns the degree of this {@code d}-ary heap.
     * 
     * @return the degree of this heap.
     */
    public int getDegree() {
        return degree;
    }

    /**
     * {@inheritDoc}
     * 
     * @return the string indicating the implementation type.
     */
    @Override
    public String toString() {
        return "IndexedDaryHeap: degree = " + degree;
    }
  
    /**
     * Sifts the node at index <code>index</code> until <tt>d</tt>-ary heap 
     * invariant is fixed.
     * 
     * @param index the index of the node to sift up.
     */
    private void siftUp(int index) {
        if (index == 0) {
            return;
        }
        
        int parentNodeIndex = getParentNodeIndex(index);
        DaryHeapNode<E, P> targetNode = daryHeapNodeArray[index];
        
        while (true) {
            DaryHeapNode<E, P> parentNode = daryHeapNodeArray[parentNodeIndex];
            
            if (parentNode.priority.compareTo(targetNode.priority) > 0) {
                daryHeapNodeArray[index] = parentNode;
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
            
        daryHeapNodeArray[index] = targetNode;
        targetNode.index = index;
    }
    
    /**
     * Sifts down the element at position {@code index}.
     * 
     * @param index the index of the element to sift down.
     */
    private void siftDownRoot() {
        int index = 0;
        DaryHeapNode<E, P> targetNode = daryHeapNodeArray[0];
        P priority = targetNode.priority;
        
        while (true) {
            P minChildPriority = priority;
            int minChildIndex = -1;
            computeChildNodeIndices(index);
            
            for (int i : indices) {
                if (i == -1) {
                    break;
                }
                
                P tentativePriority = daryHeapNodeArray[i].priority;
                
                if (minChildPriority.compareTo(tentativePriority) > 0) {
                    minChildPriority = tentativePriority;
                    minChildIndex = i;
                }
            }
            
            if (minChildIndex == -1) {
                daryHeapNodeArray[index] = targetNode;
                targetNode.index = index;
                return;
            }
            
            daryHeapNodeArray[index] = daryHeapNodeArray[minChildIndex];
            daryHeapNodeArray[index].index = index;
            index = minChildIndex;
        }
    }
    
    /**
     * Loads the {@code this.indices} with child node indices of the node
     * {@code daryHeapNodeArray[index]}.
     * 
     * @param index the index of the heap node whose children's indices to load.
     */
    private void computeChildNodeIndices(int index) {
        int leftmostChildNodeIndex = degree * index + 1;
        
        for (int i = 0; i != degree; ++i) {
            indices[i] = leftmostChildNodeIndex + i;
            
            if (indices[i] >= size) {
                indices[i] = -1; // -1 is a sentinel value marking the end of
                                 // the child node index array.
                return;
            }
        }
    }
    
    /**
     * Makes sure that a new element fits into this heap. If the heap is full,
     * doubles the size of the node array.
     */
    private void checkHeapHasSpace() {
        if (size == daryHeapNodeArray.length) {
            daryHeapNodeArray = Arrays.copyOf(daryHeapNodeArray, 2 * size);
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
    
    /**
     * Returns the index of the parent node of the node at location 
     * {@code index}.
     * 
     * @param index the index of the child node.
     * @return the index of the parent node.
     */
    private int getParentNodeIndex(int index) {
        return (index - 1) / degree;
    }
    
    public static void main(String[] args) {
        PriorityQueue<Integer, Integer> heap = new IndexedDaryHeap<>(2);
        Random random = new Random(4900);
        
        for (int i = 0; i < 10; ++i) {
            heap.add(i, i);
        }
        
        for (int i = 0; i < 100; ++i) {
            heap.decreasePriority(random.nextInt(10), random.nextInt(100) - 50);
        }
        
        while (heap.size() > 0) {
            heap.extractMinimum();
        }
    }
}
