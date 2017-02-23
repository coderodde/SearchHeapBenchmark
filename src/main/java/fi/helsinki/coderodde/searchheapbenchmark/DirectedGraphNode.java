package fi.helsinki.coderodde.searchheapbenchmark;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DirectedGraphNode {

    private final Set<DirectedGraphNode> children = new HashSet<>();
    
    private final int id;
    
    public DirectedGraphNode(int id) {
        this.id = id;
    }
    
    public void addChildNode(DirectedGraphNode child) {
        children.add(child);
    }
    
    public Collection<DirectedGraphNode> getChildren() {
        return Collections.<DirectedGraphNode>unmodifiableCollection(children);
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        return id == ((DirectedGraphNode) o).id;
    }
    
    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
