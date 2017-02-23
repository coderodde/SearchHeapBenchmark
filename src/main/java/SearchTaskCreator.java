
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class SearchTaskCreator {

    private final List<DirectedGraphNode> graphNodeList;
    private final int tasks;
    private final Random random;
    
    public SearchTaskCreator(List<DirectedGraphNode> graphNodeList,
                             int tasks,
                             Random random) {
        this.graphNodeList = graphNodeList;
        this.tasks = tasks;
        this.random = random;
    }
    
    public List<SearchTask> getSearchTaskList() {
        List<SearchTask> searchTaskList = new ArrayList<>(tasks);
        
        for (int task = 0; task < tasks; ++task) {
            searchTaskList.add(new SearchTask(choose(graphNodeList, random),
                                              choose(graphNodeList, random)));
        }
        
        return searchTaskList;
    }
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
