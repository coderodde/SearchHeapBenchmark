package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utils {

    public static List<HeapTask> getRandomHeapTaskList(int length, 
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
    
    public static List<HeapTask> getExtendedRandomHeapTaskList(int length,
                                                               int maxPriority,
                                                               Random random) {
        List<HeapTask> heapTaskList = new ArrayList<>(length);
        
        for (int i = 0; i < length; ++i) {
            float coin = random.nextFloat();
            Operation operation;
            
            if (coin < 0.4f) {
                heapTaskList.add(new HeapTask(Operation.ADD,
                                              random.nextInt(length),
                                              random.nextInt(maxPriority)));
            } else if (coin < 0.65f) {
                heapTaskList.add(new HeapTask(Operation.DECREASE_KEY,
                                              random.nextInt(length),
                                              random.nextInt(maxPriority)));
            } else {
                heapTaskList.add(new HeapTask(Operation.EXTRACT, 0, 0));
            }
        }
        
        return heapTaskList;
        
    }
    
    public static List<HeapTask> getRandomHeapTaskList(int length,
                                                       Random random) {
        return getRandomHeapTaskList(length, Integer.MAX_VALUE, random);
    }
    
    public static List<HeapTask> getExtendedRandomHeapTaskList(int length,
                                                               Random random) {
        return getExtendedRandomHeapTaskList(length, 
                                             Integer.MAX_VALUE, 
                                             random);
    }
    
    public static List<Integer> test(PriorityQueue<Integer, Integer> queue,
                                     List<HeapTask> heapTaskList,
                                     Random random) {
        System.out.println(queue);
        List<Integer> resultList = new ArrayList<>(heapTaskList.size());
        Set<Integer> usedPrioritySet = new HashSet<>();
        
        loop:
        for (HeapTask task : heapTaskList) {
            switch (task.operation) {
                case ADD:
                    Integer element = task.element;
                    Integer priority = task.priority;
                    
                    if (usedPrioritySet.contains(priority)) {
                        continue loop;
                    }
                    
                    usedPrioritySet.add(priority);
                    queue.add(element, priority);
                    break;
                    
                case DECREASE_KEY:
                    element = task.element;
                    priority = task.priority;
                    
                    if (usedPrioritySet.contains(priority)) {
                        continue loop;
                    }
                    
                    queue.decreasePriority(element, priority);
                    break;
                    
                case EXTRACT:
                    
                    if (queue.size() == 0) {
                        continue;
                    }
                    
                    element = queue.extractMinimum();
                    usedPrioritySet.remove(element);
                    resultList.add(element);
                    break;
            }
        }
        
        return resultList;
    }
}
