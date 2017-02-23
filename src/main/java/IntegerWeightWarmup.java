
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
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.PairingHeap;
import java.util.ArrayList;
import java.util.List;


public final class IntegerWeightWarmup {

    private final List<SearchTask> searchTaskList;
    private final DirectedGraphWeightFunction<Integer> weightFunction;
    private final IntegerWeight weight = new IntegerWeight();
    
    public IntegerWeightWarmup
        (List<SearchTask> searchTaskList,
         DirectedGraphWeightFunction<Integer> weightFunction) {
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
        benchmarkUnindexed(new IntegerDialsHeap<>());
        benchmarkUnindexed(new AVLTreeHeap<>());
        
        //// Indexed heaps ////
        benchmarkIndexed(new IndexedBinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            benchmarkIndexed(new IndexedDaryHeap<>(degree));
        }
        
        benchmarkIndexed(new IndexedBinomialHeap<>());
        benchmarkIndexed(new IndexedFibonacciHeap<>());
        benchmarkIndexed(new IndexedPairingHeap<>());
        benchmarkUnindexed(new IntegerDialsHeap<>());
        benchmarkUnindexed(new IndexedIntegerDialsHeap<>());
        benchmarkIndexed(new IndexedAVLTreeHeap<>());
    }
    
    private void benchmarkUnindexed
        (PriorityQueue<DirectedGraphNode, Integer> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Integer> finder = new DijkstraPathFinder<>(heap);
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
    }
    
    private void benchmarkIndexed
        (PriorityQueue<DirectedGraphNode, Integer> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Integer> finder = new IndexedDijkstraPathFinder<>(heap);
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
    }
}
