package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IndexedHeapBruteForceTest {
    
    private static final int OPERATIONS_PER_HEAP = 500_000;
    
    // This test can fail in principle:
    // Suppose we have input element/priority tuples (1, 10), (2, 10), (3, 10)
    // inserted in that order. Now when extracting all, there is six possible
    // sequences, and different heaps will give different orders.
    @Test
    public void test() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.println("IndexedHeapBruteForceTest, seed = " + seed);
        List<HeapTask> heapTaskList = getRandomHeapTaskList(OPERATIONS_PER_HEAP,
                                                            random);
        
        PriorityQueue<Integer, Integer> binaryHeap = new IndexedBinaryHeap<>();
        PriorityQueue<Integer, Integer> d2aryHeap = new IndexedDaryHeap<>(2);
        PriorityQueue<Integer, Integer> d3aryHeap = new IndexedDaryHeap<>(3);
        PriorityQueue<Integer, Integer> d4aryHeap = new IndexedDaryHeap<>(4);
        PriorityQueue<Integer, Integer> d5aryHeap = new IndexedDaryHeap<>(5);
        PriorityQueue<Integer, Integer> binomialHeap = 
                new IndexedBinomialHeap<>();
        PriorityQueue<Integer, Integer> fibonacciHeap = 
                new IndexedFibonacciHeap<>();
        PriorityQueue<Integer, Integer> pairingHeap = 
                new IndexedPairingHeap<>();
        PriorityQueue<Integer, Integer> avlTreeHeap = 
                new IndexedAVLTreeHeap<>();
        
        List<Integer> resultListOfBinaryHeap = test(binaryHeap, 
                                                    heapTaskList,
                                                    new Random(seed));
        
        List<Integer> resultListOfDaryHeap2 = test(d2aryHeap,
                                                   heapTaskList,
                                                   new Random(seed));
        
        List<Integer> resultListOfDaryHeap3 = test(d3aryHeap,
                                                   heapTaskList,
                                                   new Random(seed));
        
        List<Integer> resultListOfDaryHeap4 = test(d4aryHeap,
                                                   heapTaskList,
                                                   new Random(seed));
        
        List<Integer> resultListOfDaryHeap5 = test(d5aryHeap,
                                                   heapTaskList,
                                                   new Random(seed));
        
        List<Integer> resultListOfBinomialHeap = test(binomialHeap,
                                                      heapTaskList,
                                                      new Random(seed));
        
        List<Integer> resultListOfFibonacciHeap = test(fibonacciHeap,
                                                       heapTaskList,
                                                       new Random(seed));
        
        List<Integer> resultListOfPairingHeap = test(pairingHeap,
                                                     heapTaskList,
                                                     new Random(seed));
        
        List<Integer> resultListOfAVLTreeHeap = test(avlTreeHeap,
                                                     heapTaskList,
                                                     new Random(seed));
        
        boolean allEqual = 
                listsEqual(resultListOfBinaryHeap,
                           resultListOfDaryHeap2,
                           resultListOfDaryHeap3,
                           resultListOfDaryHeap4,
                           resultListOfDaryHeap5,
                           resultListOfBinomialHeap,
                           resultListOfFibonacciHeap,
                           resultListOfPairingHeap,
                           resultListOfAVLTreeHeap);
        
        assertTrue(allEqual);
    }
    
    private static <T> boolean listsEqual(List<T>... lists) {
        for (int i = 0; i < lists.length - 1; ++i) {
            if (!lists[i].equals(lists[i + 1])) {
                return false;
            }
        }
        
        return true;
    }
    
    private static List<HeapTask> getRandomHeapTaskList(int length, 
                                                        int universeSize,
                                                        Random random) {
        List<HeapTask> heapTaskList = new ArrayList<>(length);
        
        for (int i = 0; i < length; ++i) {
            float f = random.nextFloat();
            Operation operation;
            
            if (f < 0.3f) {
                operation = Operation.EXTRACT;
            } else if (f < 0.6f) {
                operation = Operation.DECREASE_KEY;
            } else {
                operation = Operation.ADD;
            }
            
            heapTaskList.add(new HeapTask(operation, 
                                          random.nextInt(),
                                          random.nextInt(universeSize)));
        }
        
        return heapTaskList;
    }
    
    private static List<HeapTask> getRandomHeapTaskList(int length, 
                                                        Random random) {
        List<HeapTask> heapTaskList = new ArrayList<>(length);
        
        for (int i = 0; i < length; ++i) {
            float f = random.nextFloat();
            Operation operation;
            
            if (f < 0.3f) {
                operation = Operation.EXTRACT;
            } else if (f < 0.6f) {
                operation = Operation.DECREASE_KEY;
            } else {
                operation = Operation.ADD;
            }
            
            heapTaskList.add(new HeapTask(operation, 
                                          random.nextInt(),
                                          random.nextInt()));
        }
        
        return heapTaskList;
    }
    
    private static List<Integer> test(PriorityQueue<Integer, Integer> queue,
                                      List<HeapTask> heapTaskList,
                                      Random random) {
        System.out.println(queue);
        List<Integer> resultList = new ArrayList<>(heapTaskList.size());
        
        for (HeapTask task : heapTaskList) {
            switch (task.operation) {
                case ADD:
                    queue.add(task.element, task.priority);
                    break;
                    
                case EXTRACT:
                    
                    if (queue.size() == 0) {
                        continue;
                    }
                    
                    resultList.add(queue.extractMinimum());
                    break;
                    
                case DECREASE_KEY:
                    queue.decreasePriority(task.element, task.priority);
                    break;
            }
        }
        
        return resultList;
    }
}
