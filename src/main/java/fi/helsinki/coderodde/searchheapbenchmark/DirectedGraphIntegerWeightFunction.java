package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.HashMap;
import java.util.Map;

public class DirectedGraphIntegerWeightFunction {
    
    private final Map<DirectedGraphNode, Map<DirectedGraphNode, Integer>> map =
            new HashMap<>();
    
    public void addWeight(DirectedGraphNode tail, 
                          DirectedGraphNode head, 
                          Integer weight) {
        if (!map.containsKey(tail)) {
            map.put(tail, new HashMap<>());
        }
        
        map.get(tail).put(head, weight);
    }
    
    public Integer getWeight(DirectedGraphNode tail, DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
