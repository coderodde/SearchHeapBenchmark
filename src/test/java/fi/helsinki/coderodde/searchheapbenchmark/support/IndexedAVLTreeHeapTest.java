package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class IndexedAVLTreeHeapTest {

    private final PriorityQueue<Integer, Integer> heap = 
            new IndexedAVLTreeHeap<>();
    
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
        
        heap.clear();
        
        for (int i = 100; i < 200; ++i) {
            heap.add(i, i);
        }
        
        for (int i = 150; i < 200; ++i) {
            heap.decreasePriority(i, i - 150);
        }
        
        for (int i = 150; i < 200; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
        
        for (int i = 100; i < 150; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
    }
    
    @Test
    public void bruteForceTest() {
        final int NUMBER_OF_ADDS = 10;
        final int NUMBER_OF_DECREASE_KEYS = 20;
        final int UNIVERSE_SIZE = 10;
        final int MAX_PRIORITY = 100_000;
        
        PriorityQueue<Integer, Integer> referenceHeap = 
                new IndexedBinaryHeap<>();
        long seed = 1487833140298L; // System.currentTimeMillis();
        Random random = new Random(seed);
        
        System.out.println("Seed = " + seed);
        
        for (int i = 0; i < NUMBER_OF_ADDS; ++i) {
            int key = random.nextInt(UNIVERSE_SIZE);
            int priority = random.nextInt(MAX_PRIORITY);
            
            referenceHeap.add(key, priority);
            heap.add(key, priority);
        }
        
        for (int i = 0; i < NUMBER_OF_DECREASE_KEYS; ++i) {
            int key= random.nextInt(UNIVERSE_SIZE);
            int priority = random.nextInt(MAX_PRIORITY);
            heap.decreasePriority(key, priority);
            referenceHeap.decreasePriority(key, priority);
        }
        
        while (referenceHeap.size() > 0) {
            System.out.println(referenceHeap.size());
            assertEquals(referenceHeap.extractMinimum(), heap.extractMinimum());
        }
        
        assertEquals(0, heap.size());
    }
}
