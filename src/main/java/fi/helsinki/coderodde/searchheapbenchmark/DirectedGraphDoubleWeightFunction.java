package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.HashMap;
import java.util.Map;

public final class DirectedGraphDoubleWeightFunction {

    private final Map<DirectedGraphNode, Map<DirectedGraphNode, Double>> map =
            new HashMap<>();
    
    public void addWeight(DirectedGraphNode tail, 
                          DirectedGraphNode head, 
                          Double weight) {
        if (!map.containsKey(tail)) {
            map.put(tail, new HashMap<>());
        }
        
        map.get(tail).put(head, weight);
    }
    
    public Double getWeight(DirectedGraphNode tail, DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
