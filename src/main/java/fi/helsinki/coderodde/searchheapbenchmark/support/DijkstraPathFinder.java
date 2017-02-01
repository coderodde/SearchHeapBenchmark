package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphNode;
import fi.helsinki.coderodde.searchheapbenchmark.DirectedGraphWeightFunction;
import fi.helsinki.coderodde.searchheapbenchmark.PathFinder;
import fi.helsinki.coderodde.searchheapbenchmark.PriorityQueue;
import fi.helsinki.coderodde.searchheapbenchmark.Weight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DijkstraPathFinder<W extends Comparable<? super W>>
        implements PathFinder<W> {

    private final PriorityQueue<DirectedGraphNode, W> searchFrontier;
    
    public DijkstraPathFinder(PriorityQueue<DirectedGraphNode, W> heap) {
        heap.clear();
        this.searchFrontier = Objects.requireNonNull(heap, "The heap is null.");
    }
    
    @Override
    public List<DirectedGraphNode> 
        search(DirectedGraphNode sourceNode, 
               DirectedGraphNode targetNode,
               DirectedGraphWeightFunction<W> weightFunction,
               Weight<W> weight) {
        searchFrontier.clear();
        Set<DirectedGraphNode> closedSet = new HashSet<>();
        Map<DirectedGraphNode, W> distanceMap = new HashMap<>();
        Map<DirectedGraphNode, DirectedGraphNode> parentMap = new HashMap<>();
        
        searchFrontier.add(sourceNode, weight.zero());
        distanceMap.put(sourceNode, weight.zero());
        parentMap.put(sourceNode, null);
        
        while (searchFrontier.size() > 0) {
            DirectedGraphNode currentNode = searchFrontier.extractMinimum();
            
            if (currentNode.equals(targetNode)) {
                return tracebackPath(targetNode, parentMap);
            }
            
            if (closedSet.contains(currentNode)) {
                continue;
            }
            
            closedSet.add(currentNode);
            
            for (DirectedGraphNode childNode : currentNode.getChildren()) {
                if (closedSet.contains(childNode)) {
                    continue;
                }
                
                W tentativeDistance = 
                        weight.add(distanceMap.get(currentNode),
                                   weightFunction.getWeight(currentNode, 
                                                            childNode));
                if (!distanceMap.containsKey(childNode)
                        || distanceMap.get(childNode)
                                      .compareTo(tentativeDistance) > 0) {
                    searchFrontier.add(childNode, tentativeDistance);
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
