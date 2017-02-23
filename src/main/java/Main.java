
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final int GRAPH_NODES = 10_000;
    private static final int SPARSE_GRAPH_ARCS = 40_000;
    private static final int MEDIUM_GRAPH_ARCS = 100_000;
    private static final int DENSE_GRAPH_ARCS =  600_000;
    private static final int SEARCH_TASKS = 20;
    private static final double MAX_ARC_WEIGHT = 10.0;
    private static final int MAX_INT_ARC_WEIGHT = 10;
    
    private static final int WARMUP_GRAPH_NODES = 4_000;
    private static final int WARMUP_GRAPH_ARCS = 40_000;
    private static final int WARMUP_SEARCH_TASKS = 100;
    private static final int KEY_UNIVERSE = MAX_INT_ARC_WEIGHT *
             (WARMUP_GRAPH_NODES + 1);
    
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        GraphData<Integer> intGraphData = 
                createRandomIntGraph(WARMUP_GRAPH_NODES, 
                                     WARMUP_GRAPH_ARCS, 
                                     MAX_INT_ARC_WEIGHT, 
                                     random);
        
        SearchTaskCreator stcInt = 
                new SearchTaskCreator(intGraphData.nodeList,
                                      WARMUP_SEARCH_TASKS,
                                      random);
        
        List<SearchTask> searchTaskListInt = stcInt.getSearchTaskList();
        
        IntegerWeightWarmup warmupInt = 
                new IntegerWeightWarmup(searchTaskListInt,
                                        intGraphData.weightFunction,
                                        KEY_UNIVERSE);
        
        System.out.println("*** Integer weight search benchmark ***");
        System.out.println("Warming up integer weight search...");
//        warmupInt.run();
        System.out.println("Warming up integer weight search complete!");
        
        IntegerWeightBenchmark benchmarkInt = 
                new IntegerWeightBenchmark(searchTaskListInt,
                                           intGraphData.weightFunction,
                                           KEY_UNIVERSE);
        
        benchmarkInt.run();
        System.out.println("***************************************");
        System.exit(0);
        System.out.println();
        System.out.println("*** Double weight search benchmark ****");
        
        GraphData<Double> graphData1 = createRandomGraph(WARMUP_GRAPH_NODES,
                                                         WARMUP_GRAPH_ARCS,
                                                         MAX_ARC_WEIGHT,
                                                         random);
        
        SearchTaskCreator stc1 = new SearchTaskCreator(graphData1.nodeList,
                                                       WARMUP_SEARCH_TASKS,
                                                       random);
        
        List<SearchTask> searchTaskList1 = stc1.getSearchTaskList();
        
        DoubleWeightWarmup warmup = 
                new DoubleWeightWarmup(searchTaskList1,
                                       graphData1.weightFunction);
        
        System.out.println("Warming up...");
        warmup.run();
        System.out.println("Warming up done!");
        
        GraphData<Double> graphData2 = createRandomGraph(GRAPH_NODES,
                                                         SPARSE_GRAPH_ARCS,
                                                         MAX_ARC_WEIGHT,
                                                         random);
        
        SearchTaskCreator stc2 = new SearchTaskCreator(graphData2.nodeList,
                                                       SEARCH_TASKS,
                                                       random);
        
        List<SearchTask> searchTaskList2 = stc2.getSearchTaskList();
        
        DoubleWeightBenchmark sparseBenchmarkSparse = 
                new DoubleWeightBenchmark(searchTaskList2, 
                                          graphData2.weightFunction);
        sparseBenchmarkSparse.run();
        
        GraphData<Double> graphData3 = createRandomGraph(GRAPH_NODES,
                                                         MEDIUM_GRAPH_ARCS,
                                                         MAX_ARC_WEIGHT,
                                                         random);
        
        SearchTaskCreator stc3 = new SearchTaskCreator(graphData3.nodeList,
                                                       SEARCH_TASKS,
                                                       random);
        
        List<SearchTask> searchTaskList3 = stc3.getSearchTaskList();
        
        DoubleWeightBenchmark sparseBenchmarkMedium = 
                new DoubleWeightBenchmark(searchTaskList3, 
                                          graphData3.weightFunction);
        sparseBenchmarkMedium.run();
        
        GraphData<Double> graphData4 = createRandomGraph(GRAPH_NODES,
                                                         DENSE_GRAPH_ARCS,
                                                         MAX_ARC_WEIGHT,
                                                         random);
        
        SearchTaskCreator stc4 = new SearchTaskCreator(graphData4.nodeList,
                                                       SEARCH_TASKS,
                                                       random);
        
        List<SearchTask> searchTaskList4 = stc4.getSearchTaskList();
        
        DoubleWeightBenchmark sparseBenchmarkDense = 
                new DoubleWeightBenchmark(searchTaskList4, 
                                          graphData4.weightFunction);
        sparseBenchmarkDense.run();
        
        System.out.println("***************************************");
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
    
    private static boolean 
        eq(List<List<List<DirectedGraphNode>>> paths,
           DirectedGraphWeightFunction<Integer> weightFunction,
           List<SearchTask> searchTaskList) {
        for (int i = 0; i < paths.size() - 1; ++i) {
            if (!eq(paths.get(i), 
                    paths.get(i + 1),
                    weightFunction, 
                    searchTaskList)) {
                System.out.println("shit!");
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
            
            DirectedGraphNode source = null; //searchTask.source;
            DirectedGraphNode target = null; //searchTask.target;
            
            if (path1.isEmpty() && path2.isEmpty()) {
                continue;
            }
            
            if (path1.isEmpty()) {
                return false;
            }
            
            if (path2.isEmpty()) {
                return false;
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
}
