package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphDoubleWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class IndexedDijkstraPathFinder implements PathFinder {

    private final PriorityQueue<DirectedGraphNode, Double> searchFrontier;
    
    public IndexedDijkstraPathFinder(
            PriorityQueue<DirectedGraphNode, Double> heap) {
        heap.clear();
        this.searchFrontier = Objects.requireNonNull(heap, "The heap is null.");
    }
    
    @Override
    public List<DirectedGraphNode> 
        search(DirectedGraphNode sourceNode, 
               DirectedGraphNode targetNode,
               DirectedGraphDoubleWeightFunction weightFunction) {
        searchFrontier.clear();
        Set<DirectedGraphNode> closedSet = new HashSet<>();
        Map<DirectedGraphNode, Double> distanceMap = new HashMap<>();
        Map<DirectedGraphNode, DirectedGraphNode> parentMap = new HashMap<>();
        
        searchFrontier.add(sourceNode, 0.0);
        distanceMap.put(sourceNode, 0.0);
        parentMap.put(sourceNode, null);
        
        while (searchFrontier.size() > 0) {
            DirectedGraphNode currentNode = searchFrontier.extractMinimum();
            
            if (currentNode.equals(targetNode)) {
                return tracebackPath(targetNode, parentMap);
            }
            
            closedSet.add(currentNode);
            
            for (DirectedGraphNode childNode : currentNode.getChildren()) {
                if (closedSet.contains(childNode)) {
                    continue;
                }
                
                double tentativeDistance = 
                        distanceMap.get(currentNode) +
                        weightFunction.getWeight(currentNode, childNode);
                
                if (!distanceMap.containsKey(childNode)) {
                    searchFrontier.add(childNode, tentativeDistance);
                    distanceMap.put(childNode, tentativeDistance);
                    parentMap.put(childNode, currentNode);
                } else if (distanceMap.get(childNode) > tentativeDistance) {
                    searchFrontier.decreasePriority(childNode, 
                                                    tentativeDistance);
                    distanceMap.put(childNode, tentativeDistance);
                    parentMap.put(childNode, currentNode);
                }
            }
        }
        
        return new ArrayList<>(0);
    }
        
    private List<DirectedGraphNode> 
        tracebackPath(DirectedGraphNode targetNode,
                      Map<DirectedGraphNode, DirectedGraphNode> parentMap) {
        List<DirectedGraphNode> path = new ArrayList<>();
        DirectedGraphNode currentNode = targetNode;
        
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = parentMap.get(currentNode);
        }
        
        Collections.<DirectedGraphNode>reverse(path);
        return path;
    }
}
