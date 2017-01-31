package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import static fi.helsinki.coderodde.searchheapbenchmark.support.Utils.test;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static fi.helsinki.coderodde.searchheapbenchmark.support.Utils.getExtendedRandomHeapTaskList;

public class IndexedIntegerDialsHeapTest {

    private final PriorityQueue<Integer, Integer> heap = 
            new IndexedIntegerDialsHeap<>();
    
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
        
        for (int i = 100; i < 200; ++i) {
            heap.add(i, i);
        }
        
        for (int i = 150; i < 200; ++i) {
            heap.decreasePriority(i, i - 100);
        }
        
        for (int i = 150; i < 200; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
        
        for (int i = 100; i < 150; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
    }
    
    @Test
    public void testBruteForce() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<HeapTask> heapTaskList = getExtendedRandomHeapTaskList(10_000, 
                                                                    500_000, 
                                                                    random);
        List<Integer> result1 = test(new IndexedBinaryHeap<>(),
                                     heapTaskList,
                                     new Random(seed));
        
        List<Integer> result2 = test(new IndexedIntegerDialsHeap<>(),
                                     heapTaskList,
                                     new Random(seed));
        
        assertEquals(result1, result2);
    }
}
