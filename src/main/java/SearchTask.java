
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;

public final class SearchTask {

    private final DirectedGraphNode source;
    private final DirectedGraphNode target;
    
    public SearchTask(DirectedGraphNode source, DirectedGraphNode target) {
        this.source = source;
        this.target = target;
    }
    
    public DirectedGraphNode getSource() {
        return source;
    }
    
    public DirectedGraphNode getTarget() {
        return target;
    }
}
