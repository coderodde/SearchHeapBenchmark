
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.support.BinaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DaryHeap;
import fi.helsinki.coderodde.searchheapbenchmark.support.DijkstraPathFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final int GRAPH_NODES = 100;
    private static final int SPARSE_GRAPH_ARCS = 300;
    private static final int MEDIUM_GRAPH_ARCS = 1000;
    private static final int DENSE_GRAPH_ARCS = 8000;
    private static final int SEARCH_TASKS = 100;
    private static final double MAX_WEIGHT = 10.0;
    
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
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
        
        //// Benchmark on sparse graphs. ////
        System.out.println("===== SPARSE GRAPH =====");
        benchmark(graphData, searchTaskList);
        
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
        
        //// Benchmark on dense graphs. ////
        graphData = createRandomGraph(GRAPH_NODES,
                                      MEDIUM_GRAPH_ARCS,
                                      MAX_WEIGHT,
                                      random);
        
        searchTaskList = getRandomSearchTaskList(SEARCH_TASKS,
                                                 graphData.nodeList,
                                                 random);
        
        System.out.println("===== DENSE GRAPH ======");
        benchmark(graphData, searchTaskList);
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
        List<List<DirectedGraphNode>> paths = new ArrayList<>();
        PriorityQueue<DirectedGraphNode, Double> heap = new BinaryHeap<>();
        DijkstraPathFinder finder = new DijkstraPathFinder(heap);
        DirectedGraphWeightFunction weightFunction = graphData.weightFunction;
        
        long start = System.currentTimeMillis();
        
        for (SearchTask searchTask : searchTaskList) {
            DirectedGraphNode source = searchTask.source;
            DirectedGraphNode target = searchTask.target;
            paths.add(finder.search(source, target, weightFunction));
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
                paths.add(finder.search(source, target, weightFunction));
            }
            
            end = System.currentTimeMillis();
            
            if (output) {
                System.out.println(
                        heap + " in " + (end - start) + " milliseconds.");
            }
        }
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
