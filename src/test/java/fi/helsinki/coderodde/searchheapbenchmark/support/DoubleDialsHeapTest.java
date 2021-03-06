package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleDialsHeapTest {
    
    private final PriorityQueue<Integer, Double> heap 
            = new DoubleDialsHeap<>(0.299);
    
    @Before
    public void before() {
        heap.clear();
    }
    
    @Test
    public void testAdd() {
        for (int i = 0; i < 10; ++i) {
            heap.add(i, (double) i);
        }
        
        for (int i = 0; i < 10; ++i) {
            assertEquals(Integer.valueOf(i), heap.extractMinimum());
        }
        
        heap.add(3, 1.5);
        heap.add(1, 0.5);
        heap.add(2, 1.0);
        
        assertEquals(Integer.valueOf(1), heap.extractMinimum());
        assertEquals(Integer.valueOf(2), heap.extractMinimum());
        assertEquals(Integer.valueOf(3), heap.extractMinimum());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void throwsOnDecreaseKey() {
        heap.decreasePriority(1, 0.0);
    }
    
    @Test
    public void bruteForceTest() {
        PriorityQueue<Integer, Double> heap2 = new BinaryHeap<>();
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.println("Seed = " + seed);
        
        for (int i = 0; i < 10_000; ++i) {
            float coin  = random.nextFloat();
            
            if (coin < 0.65f) {
                Integer element = random.nextInt(1000);
                Double priority = 10.0 * random.nextDouble();
                heap.add(element, priority);
                heap2.add(element, priority);
            } else if (heap2.size() > 0) {
                assertEquals(heap2.extractMinimum(), heap.extractMinimum());
            }
        }
        
        while (heap2.size() > 0) {
            assertEquals(heap2.extractMinimum(), heap.extractMinimum());
        }
        
        assertEquals(heap2.size(), heap.size());
    }
}
