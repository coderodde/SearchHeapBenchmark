
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinomialHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.support.FibonacciHeap;
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
    
    public static void main(String[] args) {
        long seed = 1484930215392L; System.currentTimeMillis();
        System.out.println("Seed = " + seed);
        Random random = new Random(seed);
        
        GraphData graphData = createRandomGraph(GRAPH_NODES,
                                                SPARSE_GRAPH_ARCS,
                                                MAX_WEIGHT,
                                                random);
        
        List<SearchTask> searchTaskList =
                getRandomSearchTaskList(SEARCH_TASKS,
                                        graphData.nodeList, 
                                        random);
                             
        System.out.println("Warming up...");
        //// Warming up. ////
        warmup(graphData, searchTaskList);
        System.out.println("Warming up done.");
        System.out.println();
        System.gc();
        
        //// Benchmark on sparse graphs. ////
        System.out.println("===== SPARSE GRAPH =====");
        benchmark(graphData, searchTaskList);
        System.gc();
        
        //// Benchmark on medium graphs. ////
        graphData = createRandomGraph(GRAPH_NODES,
                                      MEDIUM_GRAPH_ARCS,
                                      MAX_WEIGHT,
                                      random);
        
        searchTaskList = getRandomSearchTaskList(SEARCH_TASKS,
                                                 graphData.nodeList,
                                                 random);
        
        System.out.println("===== MEDIUM GRAPH =====");
        benchmark(graphData, searchTaskList);
        System.gc();
        
        //// Benchmark on dense graphs. ////
        graphData = createRandomGraph(GRAPH_NODES,
                                      DENSE_GRAPH_ARCS,
                                      MAX_WEIGHT,
                                      random);
        
        searchTaskList = getRandomSearchTaskList(SEARCH_TASKS,
                                                 graphData.nodeList,
                                                 random);
        
        System.out.println("===== DENSE GRAPH ======");
        benchmark(graphData, searchTaskList);
        System.gc();
    }
    
    private static void benchmark(GraphData graphData, 
                                  List<SearchTask> searchTaskList) {
        perform(graphData, searchTaskList, true);
    }
    
    private static void warmup(GraphData graphData,
                               List<SearchTask> searchTaskList) {
        perform(graphData, searchTaskList, false);
    }
    
    private static void perform(GraphData graphData,
                                List<SearchTask> searchTaskList,
                                boolean output) {
        Map<String, List<List<DirectedGraphNode>>> pathMap = new HashMap<>();
        
        pathMap.put(new BinaryHeap<>().toString(),
                    new ArrayList<>(SEARCH_TASKS));
        
        for (int degree = 2; degree <= 10; ++degree) {
            pathMap.put(new DaryHeap<>(degree).toString(),
                        new ArrayList<>(SEARCH_TASKS));
            
        }
        
        pathMap.put(new BinomialHeap<>().toString(),
                    new ArrayList<>(SEARCH_TASKS));
        
        pathMap.put(new FibonacciHeap<>().toString(),
                    new ArrayList<>(SEARCH_TASKS));
        
        PriorityQueue<DirectedGraphNode, Double> heap = new BinaryHeap<>();
        DijkstraPathFinder finder = new DijkstraPathFinder(heap);
        DirectedGraphWeightFunction weightFunction = graphData.weightFunction;
        
        long start = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            List<DirectedGraphNode> path = finder.search(source, 
                                                         target, 
                                                         weightFunction);
            pathMap.get(heap.toString()).add(path);
        }
        
        long end = System.currentTimeMillis();
        
        if (output) {
            System.out.println(
                    heap + " in " + (end - start) + " milliseconds.");
        }
            
        for (int degree = 2; degree <= 10; ++degree) {
            heap = new DaryHeap<>(degree);
            finder = new DijkstraPathFinder(heap);
            
            start = System.currentTimeMillis();
            
            for (SearchTask searchTask : searchTaskList) {
                DirectedGraphNode source = searchTask.source;
                DirectedGraphNode target = searchTask.target;
                List<DirectedGraphNode> path = finder.search(source, 
                                                             target, 
                                                             weightFunction);
                pathMap.get(heap.toString()).add(path);
            }
            
            end = System.currentTimeMillis();
            
            if (output) {
                System.out.println(
                        heap + " in " + (end - start) + " milliseconds.");
            }
        }
        
        heap = new BinomialHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        start = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            List<DirectedGraphNode> path = finder.search(source, 
                                                         target, 
                                                         weightFunction);
            pathMap.get(heap.toString()).add(path);
        }
        
        end = System.currentTimeMillis();
        
        if (output) {
            System.out.println(
                    heap + " in " + (end - start) + " milliseconds.");
        }
        
        heap = new FibonacciHeap<>();
        finder = new DijkstraPathFinder(heap);
        
        start = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            List<DirectedGraphNode> path = finder.search(source, 
                                                         target, 
                                                         weightFunction);
            pathMap.get(heap.toString()).add(path);
        }
        
        end = System.currentTimeMillis();
        
        if (output) {
            System.out.println(
                    heap + " in " + (end - start) + " milliseconds.");
        }
        
        if (output) {
            System.out.println("Algorithms/heap agree: " + samePaths(pathMap));
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
        DirectedGraphWeightFunction weightFunction =
                new DirectedGraphWeightFunction();
        
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
        DirectedGraphWeightFunction weightFunction;
        
        GraphData(List<DirectedGraphNode> nodeList, 
                  DirectedGraphWeightFunction weightFunction) {
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
}
