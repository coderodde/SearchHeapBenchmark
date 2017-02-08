
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.Weight;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DoubleWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedBinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedDoubleDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedFibonacciHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedIntegerDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IndexedPairingHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerDialsHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.IntegerWeight;
import fi.helsinki.coderodde.searchheapbenchmark.support.PairingHeap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    private static final double[] RANGES = { 0.1, 0.3, 0.5 };
    private static final int GRAPH_NODES = 10_000;
    private static final int SPARSE_GRAPH_ARCS = 35_000;
    private static final int MEDIUM_GRAPH_ARCS = 200_000;
    private static final int DENSE_GRAPH_ARCS =  800_000;
    private static final int SEARCH_TASKS = 30;
    private static final double MAX_WEIGHT = 10.0;
    private static final int MAX_INT_WEIGHT = 10;
    
    private final DirectedGraphWeightFunction<Double> weightFunction;
    private final Map<String, List<List<DirectedGraphNode>>> pathMap = 
            new HashMap<>();
    private final List<SearchTask> searchTaskList;
    
    Main(GraphData<Double> graphData, Random random) {
        this.weightFunction = graphData.weightFunction;
        this.searchTaskList = getRandomSearchTaskList(SEARCH_TASKS, 
                                                      graphData.nodeList, 
                                                      random);
    }
    
    private void warmup(PathFinder<Double> finder, 
                        PriorityQueue<DirectedGraphNode, Double> heap) {
        perform(finder, heap, false);
    }
    
    private void benchmark(PathFinder<Double> finder, 
                           PriorityQueue<DirectedGraphNode, Double> heap) {
        perform(finder, heap, true);
    }
    
    private void perform(PathFinder<Double> finder, 
                         PriorityQueue<DirectedGraphNode, Double> heap,
                         boolean output) {
        long startTime = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            List<DirectedGraphNode> path = finder.search(source,
                                                         target, 
                                                         weightFunction,
                                                         new DoubleWeight());
            
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
        
        for (double range : RANGES) {
            this.pathMap.put(new DoubleDialsHeap<>(range).toString(),
                             new ArrayList<>(SEARCH_TASKS));
        }
        
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
        
        for (double range : RANGES) {
            this.pathMap.put(new IndexedDoubleDialsHeap<>(range).toString(),
                             new ArrayList<>(SEARCH_TASKS));
        }
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
        
        for (double range : RANGES) {
            heap = new DoubleDialsHeap<>(range);
            finder = new DijkstraPathFinder(heap);
            
            warmup(finder, heap);
        }
        
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
        
        for (double range : RANGES) {
            heap = new IndexedDoubleDialsHeap<>(range);
            finder = new IndexedDijkstraPathFinder(heap);
            
            warmup(finder, heap);
        }
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
        
        for (double range : RANGES) {
            heap = new DoubleDialsHeap<>(range);
            finder = new DijkstraPathFinder(heap);
            
            benchmark(finder, heap);
        }
        
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
        
        for (double range : RANGES) {
            heap = new IndexedDoubleDialsHeap<>(range);
            finder = new IndexedDijkstraPathFinder(heap);
            
            benchmark(finder, heap);
        }
        
        System.out.println("---");
        System.out.println("Algorithms/heaps agree: " + samePaths(pathMap));
    }
    
    public static void main(String[] args) {
        warmupAndProfileIntegerWeightGraph();
        System.exit(0);
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
        
        System.out.println("===== Integer weight graph =====");
        warmupAndProfileIntegerWeightGraph();
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
    
    private static GraphData<Double> createRandomGraph(int nodes,
                                                       int arcs,
                                                       double maxArcWeight,
                                                       Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphWeightFunction<Double> weightFunction =
                new DirectedGraphWeightFunction<>();
        
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
        
        return new GraphData(nodeList, weightFunction);
    }
    
    private static GraphData<Integer> createRandomIntGraph(int nodes,
                                                           int arcs,
                                                           int maxArcWeight,
                                                           Random random) {
        List<DirectedGraphNode> nodeList = new ArrayList<>(nodes);
        DirectedGraphWeightFunction<Integer> weightFunction =
                new DirectedGraphWeightFunction<>();
        
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
        
        return new GraphData(nodeList, weightFunction);
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
    
    static class GraphData<W> {
        List<DirectedGraphNode> nodeList;
        DirectedGraphWeightFunction<W> weightFunction;
        
        GraphData(List<DirectedGraphNode> nodeList, 
                  DirectedGraphWeightFunction<W> weightFunction) {
            this.nodeList = nodeList;
            this.weightFunction = weightFunction;
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
    
    private static void warmupAndProfileIntegerWeightGraph() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GraphData<Integer> graphData = createRandomIntGraph(GRAPH_NODES,
                                                            MEDIUM_GRAPH_ARCS,
                                                            MAX_INT_WEIGHT,
                                                            random);
        System.out.println(
                "warmupAndProfileIntegerWeightGraph, seed = " + seed);
        
        List<SearchTask> searchTaskList = 
                getRandomSearchTaskList(SEARCH_TASKS, 
                                        graphData.nodeList, 
                                        random);
        // Warmup unindexed:
        PriorityQueue<DirectedGraphNode, Integer> heap = new BinaryHeap<>();
        PathFinder<Integer> finder = new DijkstraPathFinder<>(heap);
        
        System.out.println("Warming up...");
        
        warmup(heap, finder, graphData, searchTaskList);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new DaryHeap<>(degree);
            finder = new DijkstraPathFinder<>(heap);
            warmup(heap, finder, graphData, searchTaskList);
        }
        
        heap = new BinomialHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new FibonacciHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new PairingHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new IntegerDialsHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        // Warmup indexed heaps:
        heap = new IndexedBinaryHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        
        warmup(heap, finder, graphData, searchTaskList);
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new IndexedDaryHeap<>(degree);
            finder = new IndexedDijkstraPathFinder<>(heap);
            warmup(heap, finder, graphData, searchTaskList);
        }
        
        heap = new IndexedBinomialHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new IndexedFibonacciHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new IndexedPairingHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
        heap = new IndexedIntegerDialsHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        warmup(heap, finder, graphData, searchTaskList);
        
          ///////////////
         // BENCHMARK //
        ///////////////
        System.out.println("Warming up done.");
        
        List<List<List<DirectedGraphNode>>> paths = new ArrayList<>();
        heap = new BinaryHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new DaryHeap<>(degree);
            finder = new DijkstraPathFinder<>(heap);
            benchmark(heap, finder, graphData, searchTaskList);
        }
        
        heap = new BinomialHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new FibonacciHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new PairingHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new IntegerDialsHeap<>();
        finder = new DijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        // Indexed benchmarks:
        heap = new IndexedBinaryHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new IndexedDaryHeap<>(degree);
            finder = new IndexedDijkstraPathFinder<>(heap);
            paths.add(benchmark(heap, finder, graphData, searchTaskList));
        }
        
        heap = new IndexedBinomialHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new IndexedFibonacciHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new IndexedPairingHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        heap = new IndexedIntegerDialsHeap<>();
        finder = new IndexedDijkstraPathFinder<>(heap);
        paths.add(benchmark(heap, finder, graphData, searchTaskList));
        
        System.out.println("Integer weight algorithms/heaps agree: " + 
                eq(paths, graphData.weightFunction, searchTaskList));
    }
    
    private static boolean 
        eq(List<List<List<DirectedGraphNode>>> paths,
           DirectedGraphWeightFunction<Integer> weightFunction,
           List<SearchTask> searchTaskList) {
        for (int i = 0; i < paths.size() - 1; ++i) {
            if (!eq(paths.get(i), 
                    paths.get(i + 1),
                    weightFunction, 
                    searchTaskList)) {
                return false;
            }
        }
        
        return true;
    }
        
    private static boolean 
        eq(List<List<DirectedGraphNode>> paths1,
           List<List<DirectedGraphNode>> paths2,
           DirectedGraphWeightFunction<Integer> weightFunction,
           List<SearchTask> searchTaskList) {
        if (paths1.size() != paths2.size()) {
            return false;
        }       
        
        if (paths1.size() != searchTaskList.size()) {
            return false;
        }
        
        for (int i = 0; i < paths1.size(); ++i) {
            SearchTask searchTask = searchTaskList.get(i);
            List<DirectedGraphNode> path1 = paths1.get(i);
            List<DirectedGraphNode> path2 = paths2.get(i);
            
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            
            if (path1.isEmpty() && path2.isEmpty()) {
                continue;
            }
            
            if (!path1.get(0).equals(source)) {
                return false;
            }
            
            if (!path1.get(path1.size() - 1).equals(target)) {
                return false;
            }
            
            if (!path2.get(0).equals(source)) {
                return false;
            }
            
            if (!path2.get(path2.size() - 1).equals(target)) {
                return false;
            }
            
            int path1Length = getPathLength(path1, weightFunction);
            int path2Length = getPathLength(path2, weightFunction);
            
            if (path1Length != path2Length) {
                return false;
            }
        }
        
        return true;
    }
        
    private static int
         getPathLength(List<DirectedGraphNode> path,
                       DirectedGraphWeightFunction<Integer> weightFunction) {
        int length = 0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            length += weightFunction.getWeight(path.get(i), path.get(i + 1));
        }
        
        return length;
    }
    
    private static void warmup(PriorityQueue<DirectedGraphNode, Integer> heap,
                               PathFinder<Integer> finder,
                               GraphData<Integer> graphData,
                               List<SearchTask> searchTaskList) {
        perform(heap, finder, graphData, searchTaskList, false);
    }
    
    private static List<List<DirectedGraphNode>> 
        benchmark(PriorityQueue<DirectedGraphNode, Integer> heap,
                  PathFinder<Integer> finder,
                  GraphData<Integer> graphData,
                  List<SearchTask> searchTaskList) {
        return perform(heap, finder, graphData, searchTaskList, true);
    }
    
    private static List<List<DirectedGraphNode>>
         perform(PriorityQueue<DirectedGraphNode, Integer> heap,
                 PathFinder<Integer> finder,
                 GraphData<Integer> graphData,
                 List<SearchTask> searchTaskList,
                 boolean output) {
        Weight<Integer> weight = new IntegerWeight();
        List<List<DirectedGraphNode>> listOfPaths = 
                new ArrayList<>(searchTaskList.size());
        long start = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            listOfPaths.add(finder.search(source, 
                                          target, 
                                          graphData.weightFunction, 
                                          weight));
        }
        
        long end = System.currentTimeMillis();
        
        if (output) {
            System.out.println(
                    heap + " in " + (end - start) + " milliseconds.");
        }
        
        return listOfPaths;
    }
}
