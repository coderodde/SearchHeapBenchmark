package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.HashMap;
import java.util.Map;

public final class DirectedGraphWeightFunction<W> {

    private final Map<DirectedGraphNode, Map<DirectedGraphNode, W>> map = 
            new HashMap<>();
    
    public void addWeight(DirectedGraphNode tail,
                          DirectedGraphNode head,
                          W weight) {
        if (!map.containsKey(tail)) {
            map.put(tail, new HashMap<>());
        }
        
        map.get(tail).put(head, weight);
    }
    
    public W getWeight(DirectedGraphNode tail, DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
