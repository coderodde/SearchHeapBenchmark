package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.HashMap;
import java.util.Map;

public final class DirectedGraphWeightFunction {

    private final Map<DirectedGraphNode, Map<DirectedGraphNode, Double>> map =
            new HashMap<>();
    
    public void addWeight(DirectedGraphNode tail, 
                          DirectedGraphNode head, 
                          double weight) {
        if (!map.containsKey(tail)) {
            map.put(tail, new HashMap<>());
        }
        
        map.get(tail).put(head, weight);
    }
    
    public double getWeight(DirectedGraphNode tail, DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
