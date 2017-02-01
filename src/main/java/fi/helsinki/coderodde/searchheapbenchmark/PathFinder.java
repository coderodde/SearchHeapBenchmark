package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.List;

public interface PathFinder<W extends Comparable<? super W>> {

    public List<DirectedGraphNode> 
        search(DirectedGraphNode source,
               DirectedGraphNode target,
               DirectedGraphWeightFunction<W> weightFunction,
               Weight<W> weight);
}
