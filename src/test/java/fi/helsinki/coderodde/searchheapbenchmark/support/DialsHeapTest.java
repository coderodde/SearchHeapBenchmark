package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DialsHeapTest {

    private final PriorityQueue<Integer, Integer> heap = new DialsHeap<>();
    
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

    @Test(expected = UnsupportedOperationException.class)
    public void testDecreasePrioirty() {
        heap.decreasePriority(0, 0);
    }
    
    @Test
    public void bruteForceTest() {
        PriorityQueue<Integer, Integer> heap1 = new BinaryHeap<>();
        PriorityQueue<Integer, Integer> heap2 = new DialsHeap<>();
        
        for (int i = 0; i < 10_000; ++i) {
            Integer element = i;
            Integer priority = 10_000 - i;
            heap1.add(element, priority);
            heap2.add(element, priority);
        }
        
        while (heap1.size() > 0) {
            assertEquals(heap1.extractMinimum(), heap2.extractMinimum());
        }
    }
}
