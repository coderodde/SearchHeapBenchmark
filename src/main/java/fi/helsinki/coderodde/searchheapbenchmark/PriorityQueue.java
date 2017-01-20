package fi.helsinki.coderodde.searchheapbenchmark;

public interface PriorityQueue<E, P extends Comparable<? super P>> {

    /**
     * Adds {@code element} with priority {@code priority} to this priority 
     * queue.
     * 
     * @param element  the element to add.
     * @param priority the priority of the element.
     */
    public void add(E element, P priority);
    
    /**
     * Attempts to improve the priority of {@code element} to 
     * {@code newPriority}.
     * 
     * @param element     the element whose priority to improve.
     * @param newPriority the new priority of the element.
     */
    public boolean decreasePriority(E element, P newPriority);
    
    /**
     * Extracts the element with the highest priority and returns it.
     * 
     * @return the element with the highest priority.
     */
    public E extractMinimum();
    
    /**
     * Returns the number of elements in this priority queue.
     * 
     * @return the number of elements in this priority queue.
     */
    public int size();
    
    /**
     * Clears the priority queue.
     */
    public void clear();
}
