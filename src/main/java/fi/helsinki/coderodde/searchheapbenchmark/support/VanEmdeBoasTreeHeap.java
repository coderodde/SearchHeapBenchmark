package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;

public final class VanEmdeBoasTreeHeap<E> implements PriorityQueue<E, Integer> {

    private static final class HeapNode<E> {
        
        /**
         * The actual element.
         */
        E element;
        
        /**
         * The next node in the collision chain.
         */
        HeapNode<E> next;
        
        HeapNode(E element) {
            this.element = element;
        }
    }
    
    private static final class HeapNodeList<E> {
        
        /**
         * The priority of all the elements in this list.
         */
        Integer priority;
        
        /**
         * The head node of this list.
         */
        HeapNode<E> head;
        
        /**
         * The tail node of this list.
         */
        HeapNode<E> tail;
        
        HeapNode<E> removeFirst() {
            HeapNode<E> removed = head;
            head = head.next;
            return removed;
        }
        
        void addLast(HeapNode<E> node) {
            tail.next = node;
            tail = node;
        }
    }
    
    /**
     * Maps each used integer priority to the collision chain of 
     * {@code HeapNode} objects.
     */
    private final VanEmdeBoasTreeMap<HeapNode<E>> map;
    
    /**
     * Holds the number of elements currently in this heap.
     */
    private int size;
    
    public VanEmdeBoasTreeHeap(int universe) {
        this.map = new VanEmdeBoasTreeMap<>(universe);
    }
    
    @Override
    public void add(E element, Integer priority) {
        HeapNode<E> newNode = new HeapNode<>(element);
        HeapNodeList<E> collisionList = map.get(priority);
        
        if (collisionList == null) {
            map.insert(priority, collisionList = new HeapNodeList<>());
        }
        
        collisionList.addLast(newNode);
        ++size;
    }

    @Override
    public boolean decreasePriority(E element, Integer newPriority) {
        throw new UnsupportedOperationException(
                "This VanEmdeBoasTreeHeap is not indexed.");
    }

    @Override
    public E extractMinimum() {
        HeapNodeList<E> collisionChain = map.getMinimum();
        --size;
        return collisionChain.removeFirst().element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
