package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
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
    
    @Test
    public void bruteForceTest2() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<HeapTask> heapTaskList = getRandomHeapTaskList(5_000, 
                                                            500_000, 
                                                            random);
        
        PriorityQueue<Integer, Integer> heap1 = new BinaryHeap<>();
        PriorityQueue<Integer, Integer> heap2 = new DialsHeap<>();
        
        List<Integer> list1 = test(heap1, heapTaskList, new Random(seed));
        List<Integer> list2 = test(heap2, heapTaskList, new Random(seed));
        
        assertEquals(list1, list2);
    }
    
    static List<HeapTask> getRandomHeapTaskList(int length, 
                                                int maxPriority,
                                                Random random) {
        List<HeapTask> heapTaskList = new ArrayList<>(length);
        
        for (int i = 0; i < length; ++i) {
            Operation operation = random.nextFloat() < 0.4f ?
                    Operation.EXTRACT : 
                    Operation.ADD;
            
            heapTaskList.add(new HeapTask(operation, 
                                          random.nextInt(), 
                                          random.nextInt(maxPriority)));
        }
        
        return heapTaskList;
    }
    
    static List<Integer> test(PriorityQueue<Integer, Integer> queue,
                              List<HeapTask> heapTaskList,
                              Random random) {
        System.out.println(queue);
        List<Integer> resultList = new ArrayList<>(heapTaskList.size());
        
        for (HeapTask task : heapTaskList) {
            switch (task.operation) {
                case ADD:
                    Integer element = task.element;
                    Integer priority = task.priority;
                    queue.add(element, priority);
                    break;
                    
                case EXTRACT:
                    
                    if (queue.size() == 0) {
                        continue;
                    }
                    
                    resultList.add(queue.extractMinimum());
                    break;
            }
        }
        
        return resultList;
    }
}
