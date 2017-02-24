package fi.helsinki.coderodde.searchheapbenchmark.support;

import org.junit.Test;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.Weight;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.assertEquals;

public class ExtendedIndexedVEBTreeHeapTest {

    private static final int UNIVERSE_SIZE = 32;
    
//    @Test
    public void test() {
        long seed = System.currentTimeMillis();
        IndexedVanEmdeBoasTreeHeap<DirectedGraphNode> heap = 
                new IndexedVanEmdeBoasTreeHeap<>(UNIVERSE_SIZE);
        
        IndexedBinaryHeap<DirectedGraphNode, Integer> referenceHeap = 
                new IndexedBinaryHeap<>();
        
        PathFinder<Integer> finder = new IndexedDijkstraPathFinder<>(heap);
        Random random = new Random(seed);
        GraphData graphData = createRandomIntegerWeightGraph(10000, 400000, 10, random);
        DirectedGraphWeightFunction<Integer> weightFunction = 
                graphData.weightFunction;
        List<DirectedGraphNode> nodeList = graphData.nodeList;
        
        DirectedGraphNode source = choose(nodeList, random);
        DirectedGraphNode target = choose(nodeList, random);
        Weight<Integer> weight = new IntegerWeight();
        
        System.out.println("Seed = " + seed);
        
        List<DirectedGraphNode> path = finder.search(source, 
                                                     target, 
                                                     graphData.weightFunction,
                                                     weight);
        
        finder = new IndexedDijkstraPathFinder<>(referenceHeap);
        
        List<DirectedGraphNode> referencePath = 
                finder.search(source, 
                              target, 
                              graphData.weightFunction, 
                              weight);
        
        assertEquals(getPathCost(referencePath, weightFunction), 
                     getPathCost(path, weightFunction));
    }
    
    private static final int SEARCH_GRAPH_NODES = 6;
    private static final int SEARCH_GRAPH_ARCS = 10;
    private static final int SEARCH_GRAPH_MAX_WEIGHT = 4;
    
    @Test
    public void findFailingCase() {
        PathFinder<Integer> finder;
        DirectedGraphWeightFunction<Integer> weightFunction;
        List<DirectedGraphNode> nodeList;
        DirectedGraphNode source;
        DirectedGraphNode target;
        Weight<Integer> weight = new IntegerWeight();
        
        while (true) {
//            long seed = 1487856892778L; //System.currentTimeMillis();
            long seed = 1487942488716L; // System.currentTimeMillis();
            Random random = new Random(seed);
            GraphData graphData = 
                    createRandomIntegerWeightGraph(SEARCH_GRAPH_NODES,
                                                   SEARCH_GRAPH_ARCS,
                                                   SEARCH_GRAPH_MAX_WEIGHT,
                                                   random);
            
            PriorityQueue<DirectedGraphNode, Integer> referenceHeap = 
                    new IndexedBinaryHeap<>();
            
            PriorityQueue<DirectedGraphNode, Integer> vebTreeHeap =
                    new IndexedVanEmdeBoasTreeHeap<>(32);
            
            weightFunction = graphData.weightFunction;
            nodeList = graphData.nodeList;
            source = choose(nodeList, random);
            target = choose(nodeList, random);
            
            finder = new IndexedDijkstraPathFinder<>(referenceHeap);
            List<DirectedGraphNode> referencePath =
                    finder.search(source, target, weightFunction, weight);
            
            finder = new IndexedDijkstraPathFinder<>(vebTreeHeap);
            List<DirectedGraphNode> targetPath =
                    finder.search(source, target, weightFunction, weight);
            
            int referencePathLength = getPathCost(referencePath, 
                                                  weightFunction);
            
            int targetPathLength = getPathCost(targetPath, weightFunction);
            
            if (referencePathLength != targetPathLength) {
                System.out.println("Seed = " + seed + "!");
                System.out.println(
                        referencePath + ": cost = " + referencePathLength);
                
                System.out.println(
                        targetPath + ": cost = " + targetPathLength);
                return;
            } 
        }
    }
    
    @Test
    public void testFindBadCase() {
        IndexedVanEmdeBoasTreeHeap<Integer> heap = 
                new IndexedVanEmdeBoasTreeHeap<>(32);
        
        heap.add(2, 0);
        
        heap.extractMinimum(); // 2
        heap.add(1, 1);
        heap.add(3, 1);
        heap.add(4, 4);
        
        heap.extractMinimum(); // 3
        heap.add(0, 4);
        heap.decreasePriority(4, 1);
        heap.add(5, 1);
        
        heap.extractMinimum(); // 5
        
        heap.extractMinimum(); // 4
        
        heap.extractMinimum(); // 1
        System.out.println("Removed: " + heap.extractMinimum());
    }
    
    private static GraphData createRandomIntegerWeightGraph(int nodes,
                                                            int arcs,
                                                            int maxArcWeight,
                                                            Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction<>();
        
        for (int i = 0; i < nodes; ++i) {
            nodeList.add(new DirectedGraphNode(i));
        }
        
        while (arcs-- > 0) {
            DirectedGraphNode tail = choose(nodeList, random);
            DirectedGraphNode head = choose(nodeList, random);
            int weight = random.nextInt(maxArcWeight + 1);
            tail.addChildNode(head);
            weightFunction.addWeight(tail, head, weight);
        }
        
        GraphData ret = new GraphData();
        ret.nodeList = nodeList;
        ret.weightFunction = weightFunction;
        return ret;
    }
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    private static class GraphData {
        List<DirectedGraphNode> nodeList;
        DirectedGraphWeightFunction<Integer> weightFunction;
    }
    
    private static int getPathCost(List<DirectedGraphNode> path,
                                   DirectedGraphWeightFunction<Integer> wf) {
        int cost = 0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            cost += wf.getWeight(path.get(i), path.get(i + 1));
        }
        
        return cost;
    }
}
