
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


public final class IntegerWeightWarmup {

    
    
    private final List<SearchTask> searchTaskList;
    private final DirectedGraphWeightFunction<Integer> weightFunction;
    private final IntegerWeight weight = new IntegerWeight();
    private final int keyUniverse;
    
    public IntegerWeightWarmup
        (List<SearchTask> searchTaskList,
         DirectedGraphWeightFunction<Integer> weightFunction,
         int keyUniverse) {
        this.searchTaskList = searchTaskList;
        this.weightFunction = weightFunction;
        this.keyUniverse = keyUniverse;
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
        warmupUnindexed(new IntegerDialsHeap<>());
        warmupUnindexed(new AVLTreeHeap<>());
        warmupUnindexed(new VanEmdeBoasTreeHeap<>(keyUniverse));
        
        //// Indexed heaps ////
        warmupIndexed(new IndexedBinaryHeap<>());
        
        for (int degree = 2; degree <= 10; ++degree) {
            warmupIndexed(new IndexedDaryHeap<>(degree));
        }
        
        warmupIndexed(new IndexedBinomialHeap<>());
        warmupIndexed(new IndexedFibonacciHeap<>());
        warmupIndexed(new IndexedPairingHeap<>());
        warmupIndexed(new IndexedIntegerDialsHeap<>());
        warmupIndexed(new IndexedAVLTreeHeap<>());
        warmupIndexed(new IndexedVanEmdeBoasTreeHeap<>(keyUniverse));
    }
    
    private void warmupUnindexed
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
    
    private void warmupIndexed
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
