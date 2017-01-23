package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class UnindexedHeapBruteForceTest {
    
    private static final int OPERATIONS_PER_HEAP = 1_000_000;
    
    @Test
    public void test() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.println("UnindexedHeapBruteForceTest seed = " + seed);
        List<HeapTask> heapTaskList = getRandomHeapTaskList(OPERATIONS_PER_HEAP,
                                                            random);
        
        PriorityQueue<Integer, Integer> binaryHeap = new BinaryHeap<>();
        PriorityQueue<Integer, Integer> d2aryHeap = new DaryHeap<>(2);
        PriorityQueue<Integer, Integer> d3aryHeap = new DaryHeap<>(3);
        PriorityQueue<Integer, Integer> d4aryHeap = new DaryHeap<>(4);
        PriorityQueue<Integer, Integer> d5aryHeap = new DaryHeap<>(5);
        PriorityQueue<Integer, Integer> binomialHeap = new BinomialHeap<>();
        PriorityQueue<Integer, Integer> fibonacciHeap = new FibonacciHeap<>();
        
        List<Integer> resultListOfBinaryHeap = test(binaryHeap, 
                                                    heapTaskList,
                                                    new Random(seed));
        
        List<Integer> resultListOf2aryHeap = test(d2aryHeap,
                                                  heapTaskList,
                                                  new Random(seed));
        
        List<Integer> resultListOf3aryHeap = test(d3aryHeap,
                                                  heapTaskList,
                                                  new Random(seed));
        
        List<Integer> resultListOf4aryHeap = test(d4aryHeap,
                                                  heapTaskList,
                                                  new Random(seed));
        
        List<Integer> resultListOf5aryHeap = test(d5aryHeap,
                                                  heapTaskList,
                                                  new Random(seed));
        
        List<Integer> resultListOfBinomialHeap = test(binomialHeap,
                                                      heapTaskList,
                                                      new Random(seed));
        
        List<Integer> resultListOfFibonacciHeap = test(fibonacciHeap,
                                                       heapTaskList,
                                                       new Random(seed));
        boolean allEqual = 
                listsEqual(resultListOfBinaryHeap,
                           resultListOf2aryHeap,
                           resultListOf3aryHeap,
                           resultListOf4aryHeap,
                           resultListOf5aryHeap,
                           resultListOfBinomialHeap,
                           resultListOfFibonacciHeap);
        
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
                                                        Random random) {
        List<HeapTask> heapTaskList = new ArrayList<>(length);
        
        for (int i = 0; i < length; ++i) {
            Operation operation = random.nextFloat() < 0.4f ?
                    Operation.EXTRACT : 
                    Operation.ADD;
            
            heapTaskList.add(new HeapTask(operation, random.nextInt(), random.nextInt()));
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
                    Integer element = random.nextInt();
                    Integer priority = random.nextInt();
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
