package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.List;

public interface PathFinder {

    public List<DirectedGraphNode> 
        search(DirectedGraphNode source,
               DirectedGraphNode target,
               DirectedGraphDoubleWeightFunction weightFunction);
}
