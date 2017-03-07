
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.AVLTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedAVLTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDoubleDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedFibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedPairingHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.PairingHeap;
import java.util.ArrayList;
import java.util.List;

public final class DoubleWeightBenchmark {

    private final List<SearchTask> searchTaskList;
    private final List<List<List<DirectedGraphNode>>> resultCollectorList =
            new ArrayList<>();
    
    private final DirectedGraphWeightFunction<Double> weightFunction;
    private final DoubleWeight weight = new DoubleWeight();
    
    public DoubleWeightBenchmark
        (List<SearchTask> searchTaskList,
         DirectedGraphWeightFunction<Double> weightFunction) {
        this.searchTaskList = searchTaskList;
        this.weightFunction = weightFunction;
    }
    
    public void run() {
        //// Unindexed heaps ////
        benchmarkUnindexed(new BinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            benchmarkUnindexed(new DaryHeap<>(degree));
        }
        
        benchmarkUnindexed(new BinomialHeap<>());
        benchmarkUnindexed(new FibonacciHeap<>());
        benchmarkUnindexed(new PairingHeap<>());
        
        for (double range : new double[] { 0.1, 0.2, 0.3 }) {
            benchmarkUnindexed(new DoubleDialsHeap<>(range));
        }
        
        benchmarkUnindexed(new AVLTreeHeap<>());
        
        for (int minimumDegree : new int[]{ 32, 64, 128 }) {
            benchmarkUnindexed(new BTreeHeap<>(minimumDegree));
        }
        
        //// Indexed heaps ////
        benchmarkIndexed(new IndexedBinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            benchmarkIndexed(new IndexedDaryHeap<>(degree));
        }
        
        benchmarkIndexed(new IndexedBinomialHeap<>());
        benchmarkIndexed(new IndexedFibonacciHeap<>());
        benchmarkIndexed(new IndexedPairingHeap<>());
        
        for (double range : new double[] { 0.1, 0.2, 0.3 }) {
            benchmarkIndexed(new IndexedDoubleDialsHeap<>(range));
        }
        
        benchmarkIndexed(new IndexedAVLTreeHeap<>());
        
        for (int minimumDegree : new int[]{ 32, 64, 128 }) {
            benchmarkIndexed(new IndexedBTreeHeap<>(minimumDegree));
        }
        
        System.out.println("Algorithms/heaps agree: " + samePaths());
    }
    
    private void benchmarkUnindexed
        (PriorityQueue<DirectedGraphNode, Double> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Double> finder = new DijkstraPathFinder<>(heap);
        
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
        (PriorityQueue<DirectedGraphNode, Double> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Double> finder = new IndexedDijkstraPathFinder<>(heap);
        
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
            if (!pathList1.get(i).equals(pathList2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
}
