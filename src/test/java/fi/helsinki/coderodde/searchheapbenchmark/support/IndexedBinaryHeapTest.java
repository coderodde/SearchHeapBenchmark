package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class IndexedBinaryHeapTest {

    private final PriorityQueue<Integer, Integer> heap = 
            new IndexedBinaryHeap<>();
    
    @Before
    public void setUp() {
        heap.clear();
    }

    @Test
    public void testAddAndExtract() {
        heap.add(4, 4);
        heap.add(3, 3);
        heap.add(1, 1);
        heap.add(5, 5);
        heap.add(2, 2);
        
        assertEquals(5, heap.size());
        
        assertEquals(Integer.valueOf(1), heap.extractMinimum());
        assertEquals(Integer.valueOf(2), heap.extractMinimum());
        assertEquals(Integer.valueOf(3), heap.extractMinimum());
        assertEquals(Integer.valueOf(4), heap.extractMinimum());
        assertEquals(Integer.valueOf(5), heap.extractMinimum());
        
        assertEquals(0, heap.size());
    }
    
    @Test
    public void testAddAndExtract2() {
        heap.add(4, 2);
        heap.add(3, 3);
        heap.add(1, 5);
        heap.add(5, 1);
        heap.add(2, 4);
        
        assertEquals(5, heap.size());
        
        assertEquals(Integer.valueOf(5), heap.extractMinimum());
        assertEquals(Integer.valueOf(4), heap.extractMinimum());
        assertEquals(Integer.valueOf(3), heap.extractMinimum());
        assertEquals(Integer.valueOf(2), heap.extractMinimum());
        assertEquals(Integer.valueOf(1), heap.extractMinimum());
        
        assertEquals(0, heap.size());
    }
    
    @Test
    public void testDecreaseKey() {
        heap.add(1, 1);
        heap.add(2, 2);
        
        heap.decreasePriority(2, 3);
        
        assertEquals(Integer.valueOf(1), heap.extractMinimum());
        assertEquals(Integer.valueOf(2), heap.extractMinimum());
        
        heap.add(1, 1);
        heap.add(2, 2);
        
        heap.decreasePriority(2, 0);
        
        assertEquals(Integer.valueOf(2), heap.extractMinimum());
        assertEquals(Integer.valueOf(1), heap.extractMinimum());
        
        for (int i = 0; i < 100; ++i) {
            heap.add(i, i);
        }
        
        for (int i = 50; i < 100; ++i) {
            heap.decreasePriority(i, i - 100);
        }
        
        for (int i = 0; i < 50; ++i) {
            assertEquals(Integer.valueOf(i + 50), heap.extractMinimum());
        }
        
        for (int i = 0; i < 50; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
    }
}
