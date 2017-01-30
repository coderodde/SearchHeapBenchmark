
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphDoubleWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphIntegerWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedFibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedPairingHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.PairingHeap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    private static final int GRAPH_NODES = 10_000;
    private static final int SPARSE_GRAPH_ARCS = 35_000;
    private static final int MEDIUM_GRAPH_ARCS = 200_000;
    private static final int DENSE_GRAPH_ARCS =  800_000;
    private static final int SEARCH_TASKS = 100;
    private static final double MAX_WEIGHT = 10.0;
    private static final int MAX_INT_WEIGHT = 10;
    
    private final DirectedGraphDoubleWeightFunction weightFunction;
    private final Map<String, List<List<DirectedGraphNode>>> pathMap = 
            new HashMap<>();
    private final List<SearchTask> searchTaskList;
    
    Main(GraphData graphData, Random random) {
        this.weightFunction = graphData.doubleWeightFunction;
        this.searchTaskList = getRandomSearchTaskList(SEARCH_TASKS, 
                                                      graphData.nodeList, 
                                                      random);
    }
    
    private void warmup(PathFinder finder, 
                        PriorityQueue<DirectedGraphNode, Double> heap) {
        perform(finder, heap, false);
    }
    
    private void benchmark(PathFinder finder, 
                           PriorityQueue<DirectedGraphNode, Double> heap) {
        perform(finder, heap, true);
    }
    
    private void perform(PathFinder finder, 
                         PriorityQueue<DirectedGraphNode, Double> heap,
                         boolean output) {
        long startTime = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            List<DirectedGraphNode> path = finder.search(source,
                                                         target, 
                                                         weightFunction);
            
            pathMap.get(heap.toString()).add(path);
        }
        
        long endTime = System.currentTimeMillis();
        
        if (output) {
            System.out.println(
                    heap + " in " + (endTime - startTime) + " milliseconds.");
        }
    }
    
    private void loadPathMaps() {
        this.pathMap.clear();
        
        this.pathMap.put(new BinaryHeap<>().toString(), 
                         new ArrayList<>(SEARCH_TASKS));
        
        for (int degree = 2; degree <= 10; ++degree) {
            this.pathMap.put(new DaryHeap<>(degree).toString(),
                             new ArrayList<>(SEARCH_TASKS));
        }
        
        this.pathMap.put(new BinomialHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
        
        this.pathMap.put(new FibonacciHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
        
        this.pathMap.put(new PairingHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
        
        this.pathMap.put(new IndexedBinaryHeap<>().toString(), 
                         new ArrayList<>(SEARCH_TASKS));
        
        for (int degree = 2; degree <= 10; ++degree) {
            this.pathMap.put(new IndexedDaryHeap<>(degree).toString(),
                             new ArrayList<>(SEARCH_TASKS));
        }
        
        this.pathMap.put(new IndexedBinomialHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
        
        this.pathMap.put(new IndexedFibonacciHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
        
        this.pathMap.put(new IndexedPairingHeap<>().toString(),
                         new ArrayList<>(SEARCH_TASKS));
    }
    
    private void warmup() {
        loadPathMaps();
        PriorityQueue<DirectedGraphNode, Double> heap = new BinaryHeap<>();
        PathFinder finder = new DijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new DaryHeap<>(degree);
            finder = new DijkstraPathFinder(heap);
            warmup(finder, heap);
        }
        
        heap = new BinomialHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        heap = new FibonacciHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        heap = new PairingHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        //// Indexed heaps:
        heap = new IndexedBinaryHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new IndexedDaryHeap<>(degree);
            finder = new IndexedDijkstraPathFinder(heap);
            warmup(finder, heap);
        }
        
        heap = new IndexedBinomialHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        heap = new IndexedFibonacciHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        warmup(finder, heap);
        
        heap = new IndexedPairingHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        warmup(finder, heap);        
    }
    
    private void benchmark() {
        loadPathMaps();
        PriorityQueue<DirectedGraphNode, Double> heap = new BinaryHeap<>();
        PathFinder finder = new DijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new DaryHeap<>(degree);
            finder = new DijkstraPathFinder(heap);
            benchmark(finder, heap);
        }
        
        heap = new BinomialHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        heap = new FibonacciHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        heap = new PairingHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        //// Indexed heaps:
        heap = new IndexedBinaryHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new IndexedDaryHeap<>(degree);
            finder = new IndexedDijkstraPathFinder(heap);
            benchmark(finder, heap);
        }
        
        heap = new IndexedBinomialHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        heap = new IndexedFibonacciHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        benchmark(finder, heap);
        
        heap = new IndexedPairingHeap<>();
        finder = new IndexedDijkstraPathFinder(heap);
        
        benchmark(finder, heap);  
        
        System.out.println("---");
        System.out.println("Algorithms/heaps agree: " + samePaths(pathMap));
    }
    
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        System.out.println("Seed = " + seed);
        Random random = new Random(seed);
        GraphData graphData = createRandomGraph(GRAPH_NODES,
                                                SPARSE_GRAPH_ARCS,
                                                MAX_WEIGHT,
                                                random);
        
        Main main = new Main(graphData, new Random(seed));
        System.out.println("Warming up...");
        main.warmup();
        System.out.println("Warming up done!");
        System.gc();
        
        System.out.println("===== Sparse graph =====");
        main.benchmark();
        System.gc();
        
        graphData = createRandomGraph(GRAPH_NODES,
                                      MEDIUM_GRAPH_ARCS,
                                      MAX_WEIGHT,
                                      random);
        
        System.out.println("===== Medium graph =====");
        main = new Main(graphData, new Random(seed));
        main.benchmark();
        System.gc();
        
        graphData = createRandomGraph(GRAPH_NODES,
                                      DENSE_GRAPH_ARCS,
                                      MAX_WEIGHT,
                                      random);
        
        System.out.println("===== Dense graph ======");
        main = new Main(graphData, new Random(seed));
        main.benchmark();
        System.gc();
        
//        graphData = createRandomIntGraph(GRAPH_NODES, 
//                                         MEDIUM_GRAPH_ARCS, 
//                                         MAX_INT_WEIGHT,
//                                         random);
        
//        profileDialsHeap(graphData);
    }
    
    private static void profileDialsHeap(GraphData graphData, long seed) {
        Random random = new Random(seed);
        List<SearchTask> searchTaskList = 
                getRandomSearchTaskList(SEARCH_TASKS, 
                                        graphData.nodeList,
                                        random);
        warmupDialsHeap(graphData, searchTaskList, random);
        benchmarkDialsHeap(graphData, searchTaskList, random);
    }
    
    private static void warmupDialsHeap(GraphData graphData,
                                        List<SearchTask> searchTaskList,
                                        Random random) {
        perform(graphData, searchTaskList, random, false);
    }
    
    private static void benchmarkDialsHeap(GraphData graphData,
                                           List<SearchTask> searchTaskList,
                                           Random random) {
        perform(graphData, searchTaskList, random, true);
    }
    
    private static void perform(GraphData graphData, 
                                List<SearchTask> searchTaskList,
                                Random random,
                                boolean output) {
        PathFinder finder = new DijkstraPathFinder(new BinaryHeap<>());
        long startTime = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
//            finder.search(source, target, graphData.getW)
        }
        
        long endTime = System.currentTimeMillis();
        
        if (output) {
            System.out.println("Dials'heap ");
        }
    }
    
    private static boolean 
        samePaths(Map<String, List<List<DirectedGraphNode>>> pathMap) {
        List<List<List<DirectedGraphNode>>> data = 
                new ArrayList<>(pathMap.values());
        
        for (int i = 0; i < data.size() - 1; ++i) {
            if (!samePaths(data.get(i), data.get(i + 1))) {
                return false;
            }
        }
        
        return true;
    }
        
    private static boolean samePaths(List<List<DirectedGraphNode>> paths1,
                                     List<List<DirectedGraphNode>> paths2) {
        if (paths1.size() != paths2.size()) {
            return false;
        }
        
        for (int i = 0; i < paths1.size(); ++i) {
            if (!paths1.get(i).equals(paths2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    private static GraphData createRandomGraph(int nodes,
                                               int arcs,
                                               double maxArcWeight,
                                               Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphDoubleWeightFunction weightFunction =
                new DirectedGraphDoubleWeightFunction();
        
        for (int id = 0; id < nodes; ++id) {
            DirectedGraphNode node = new DirectedGraphNode(id);
            nodeList.add(node);
        }
        
        while (arcs-- > 0) {
            DirectedGraphNode tail = choose(nodeList, random);
            DirectedGraphNode head = choose(nodeList, random);
            tail.addChildNode(head);
            weightFunction.addWeight(tail, 
                                     head, 
                                     random.nextDouble() * maxArcWeight);
        }
        
        return new GraphData(nodeList, weightFunction, null);
    }
    
    private static GraphData createRandomIntGraph(int nodes,
                                                  int arcs,
                                                  int maxArcWeight,
                                                  Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphIntegerWeightFunction weightFunction =
                new DirectedGraphIntegerWeightFunction();
        
        for (int id = 0; id < nodes; ++id) {
            DirectedGraphNode node = new DirectedGraphNode(id);
            nodeList.add(node);
        }
        
        while (arcs-- > 0) {
            DirectedGraphNode tail = choose(nodeList, random);
            DirectedGraphNode head = choose(nodeList, random);
            tail.addChildNode(head);
            weightFunction.addWeight(tail, 
                                     head, 
                                     random.nextInt(maxArcWeight + 1));
        }
        
        return new GraphData(nodeList, null, weightFunction);
    }
    
    static List<SearchTask> 
        getRandomSearchTaskList(int tasks,
                                List<DirectedGraphNode> nodeList,
                                Random random) {
        List<SearchTask> taskList = new ArrayList<>(tasks);
        
        for (int i = 0; i < tasks; ++i) {
            taskList.add(new SearchTask(choose(nodeList, random),
                                        choose(nodeList, random)));
        }
        
        return taskList;
    }
    
    static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    static class GraphData {
        List<DirectedGraphNode> nodeList;
        DirectedGraphDoubleWeightFunction doubleWeightFunction;
        DirectedGraphIntegerWeightFunction intWeightFunction;
        
        GraphData(List<DirectedGraphNode> nodeList, 
                  DirectedGraphDoubleWeightFunction doubleWeightFunction,
                  DirectedGraphIntegerWeightFunction integerWeightFunction) {
            this.nodeList = nodeList;
            this.doubleWeightFunction = doubleWeightFunction;
            this.intWeightFunction = integerWeightFunction;
        }
    }
    
    static class SearchTask {
        DirectedGraphNode source;
        DirectedGraphNode target;
        
        SearchTask(DirectedGraphNode source, DirectedGraphNode target) {
            this.source = source;
            this.target = target;
        }
    }
}
