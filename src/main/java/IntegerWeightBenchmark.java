
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.AVLTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedAVLTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedFibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedIntegerDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedPairingHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedVanEmdeBoasTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.PairingHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.VanEmdeBoasTreeHeap;
import java.util.ArrayList;
import java.util.List;


public final class IntegerWeightBenchmark {
    
    private final List<SearchTask> searchTaskList;
    private final List<List<List<DirectedGraphNode>>> resultCollectorList =
            new ArrayList<>();
    
    private final DirectedGraphWeightFunction<Integer> weightFunction;
    private final IntegerWeight weight = new IntegerWeight();
    private final int keyUniverse;
    
    public IntegerWeightBenchmark
        (List<SearchTask> searchTaskList,
         DirectedGraphWeightFunction<Integer> weightFunction,
         int keyUniverse) {
        this.searchTaskList = searchTaskList;
        this.weightFunction = weightFunction;
        this.keyUniverse = keyUniverse;
    }
    
    public void run() {
        //// Unindexed heaps ////
//        benchmarkUnindexed(new BinaryHeap<>());
//        
//        for (int degree = 2; degree <= 10; ++degree) {
//            benchmarkUnindexed(new DaryHeap<>(degree));
//        }
        
        benchmarkUnindexed(new BinomialHeap<>());
        benchmarkUnindexed(new FibonacciHeap<>());
        benchmarkUnindexed(new PairingHeap<>());
        benchmarkUnindexed(new IntegerDialsHeap<>());
        benchmarkUnindexed(new AVLTreeHeap<>());
        benchmarkUnindexed(new VanEmdeBoasTreeHeap<>(keyUniverse));
        
        //// Indexed heaps ////
//        benchmarkIndexed(new IndexedBinaryHeap<>());
//        
//        for (int degree = 2; degree <= 10; ++degree) {
//            benchmarkIndexed(new IndexedDaryHeap<>(degree));
//        }
        
        benchmarkIndexed(new IndexedBinomialHeap<>());
        benchmarkIndexed(new IndexedFibonacciHeap<>());
        benchmarkIndexed(new IndexedPairingHeap<>());
        benchmarkIndexed(new IndexedIntegerDialsHeap<>());
        benchmarkIndexed(new IndexedAVLTreeHeap<>());
        benchmarkIndexed(new IndexedVanEmdeBoasTreeHeap<>(keyUniverse));
        
        System.out.println("Algorithms/heaps agree: " + samePaths());
    }
    
    private void benchmarkUnindexed
        (PriorityQueue<DirectedGraphNode, Integer> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Integer> finder = new DijkstraPathFinder<>(heap);
        
        long startTime = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
                
        long endTime = System.currentTimeMillis();
        
        System.out.println(heap.toString() + " in " + (endTime - startTime) + 
                           " milliseconds.");
        
        resultCollectorList.add(shortestPathList);
    }
    
    private void benchmarkIndexed
        (PriorityQueue<DirectedGraphNode, Integer> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Integer> finder = new IndexedDijkstraPathFinder<>(heap);
        
        long startTime = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
                
        long endTime = System.currentTimeMillis();
        
        System.out.println(heap.toString() + " in " + (endTime - startTime) + 
                           " milliseconds.");
        
        resultCollectorList.add(shortestPathList);
    }
        
    private boolean samePaths() {
        for (int i = 0; i < resultCollectorList.size() - 1; ++i) {
            if (!samePaths(resultCollectorList.get(i),
                           resultCollectorList.get(i + 1))) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean samePaths(List<List<DirectedGraphNode>> pathList1,
                              List<List<DirectedGraphNode>> pathList2) {
        if (pathList1.size() != pathList2.size()) {
            return false;
        }
        
        for (int i = 0; i < pathList1.size(); ++i) {
            List<DirectedGraphNode> path1 = pathList1.get(i);
            List<DirectedGraphNode> path2 = pathList2.get(i);
            
            if (path1.isEmpty() && path2.isEmpty()) {
                continue;
            }
            
            if (path1.isEmpty()) {
                return false;
            }
            
            if (path2.isEmpty()) {
                return false;
            }
            
            if (!pathsHaveSameCost(path1, path2)) {
                return false;
            }
            
            if (!path1.get(0).equals(path2.get(0))) {
                return false;
            }
            
            if (!path1.get(path1.size() - 1)
                    .equals(path2.get(path2.size() - 1))) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean pathsHaveSameCost(List<DirectedGraphNode> path1,
                                      List<DirectedGraphNode> path2) {
        return cost(path1) == cost(path2);
    }
    
    private int cost(List<DirectedGraphNode> path) {
        int cost = 0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            cost += weightFunction.getWeight(path.get(i), path.get(i + 1));
        }
        
        return cost;
    }
}
