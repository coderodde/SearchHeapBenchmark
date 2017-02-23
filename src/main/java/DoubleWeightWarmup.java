
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.AVLTreeHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedAVLTreeHeap;
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


public final class DoubleWeightWarmup {

    private final List<SearchTask> searchTaskList;
    private final DirectedGraphWeightFunction<Double> weightFunction;
    private final DoubleWeight weight = new DoubleWeight();
    
    public DoubleWeightWarmup
        (List<SearchTask> searchTaskList,
         DirectedGraphWeightFunction<Double> weightFunction) {
        this.searchTaskList = searchTaskList;
        this.weightFunction = weightFunction;
    }
    
    public void run() {
        //// Unindexed heaps ////
        warmupUnindexed(new BinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            warmupUnindexed(new DaryHeap<>(degree));
        }
        
        warmupUnindexed(new BinomialHeap<>());
        warmupUnindexed(new FibonacciHeap<>());
        warmupUnindexed(new PairingHeap<>());
        
        for (double range : new double[] { 0.1, 0.2, 0.3 }) {
            warmupUnindexed(new DoubleDialsHeap<>(range));
        }
        
        warmupUnindexed(new AVLTreeHeap<>());
        
        //// Indexed heaps ////
        warmupIndexed(new IndexedBinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            warmupIndexed(new IndexedDaryHeap<>(degree));
        }
        
        warmupIndexed(new IndexedBinomialHeap<>());
        warmupIndexed(new IndexedFibonacciHeap<>());
        warmupIndexed(new IndexedPairingHeap<>());
        
        for (double range : new double[] { 0.1, 0.2, 0.3 }) {
            warmupIndexed(new IndexedDoubleDialsHeap<>(range));
        }
        
        warmupIndexed(new IndexedAVLTreeHeap<>());
    }
    
    private void warmupUnindexed
        (PriorityQueue<DirectedGraphNode, Double> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Double> finder = new DijkstraPathFinder<>(heap);
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
    }
    
    private void warmupIndexed
        (PriorityQueue<DirectedGraphNode, Double> heap) {
        List<List<DirectedGraphNode>> shortestPathList = 
                new ArrayList<>(searchTaskList.size());
        
        PathFinder<Double> finder = new IndexedDijkstraPathFinder<>(heap);
        
        for (SearchTask searchTask : searchTaskList) {
            shortestPathList.add(finder.search(searchTask.getSource(),
                                               searchTask.getTarget(), 
                                               weightFunction, 
                                               weight));
        }
    }
}
