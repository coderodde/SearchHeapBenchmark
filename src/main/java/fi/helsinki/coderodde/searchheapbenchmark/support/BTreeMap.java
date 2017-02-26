package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BTreeMap<K extends Comparable<? super K>, V> 
        implements Map<K, V> {

    /**
     * The minimum number of children of a node.
     */
    private static final int MINIMUM_DEGREE = 2;
    
    private static final class BTreeNode<K> {
        
        /**
         * The number of key/value pairs in this B-tree node.
         */
        int size;
        
        /**
         * The array of actual keys of this B-tree node.
         */
        K[] keys;
        
        /**
         * The array of child nodes of this B-tree node.
         */
        BTreeNode<K>[] children;
        
        BTreeNode(int minimumDegree) {
            this.keys = (K[]) new Object[2 * minimumDegree - 1];
        }
        
        void makeInternal() {
            this.children = new BTreeNode[keys.length + 1];
        }
        
        boolean isLeaf() {
            return children == null;
        }
    }
    
    /**
     * Maps the keys present in this B-tree to their current values.
     */
    private final Map<K, V> map = new HashMap<>();
    
    /**
     * The root node of this B-tree.
     */
    private BTreeNode<K> root;
    
    private final int minimumDegree;
    
    public BTreeMap(int minimumDegree) {
        this.minimumDegree = Math.max(minimumDegree, MINIMUM_DEGREE);
        this.root = new BTreeNode(this.minimumDegree);
    }
    
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(
                "This BTreeMap does not support containsValue.");
    }

    @Override
    public V get(Object key) {
        return map.get(key);    
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException(
                "This BTreeMap does not support keySet.");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException(
                "This BTreeMap does not support values.");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException(
                "This BTreeMap does not support entrySet.");
    }
    
    private void bTreeInsert(K key) {
        BTreeNode<K> r = root;
        
        if (r.size == 2 * minimumDegree - 1) {
            BTreeNode<K> s = new BTreeNode<>(minimumDegree);
            root = s;
            s.makeInternal();
            s.children[0] = r;
            bTreeSplitChild(s, 0);
            bTreeInsertNonfull(s, key);
        } else {
            bTreeInsertNonfull(r, key);
        }
    }
    
    private void bTreeSplitChild(BTreeNode<K> x, int i) {
        BTreeNode<K> z = new BTreeNode<>(minimumDegree);
        BTreeNode<K> y = x.children[i];
        
        if (!y.isLeaf()) {
            z.makeInternal();
        }
        
        z.size = minimumDegree - 1;
        
        for (int j = 0; j < minimumDegree - 1; ++j) {
            z.keys[j] = y.keys[j + minimumDegree];
        }
        
        if (!y.isLeaf()) {
            for (int j = 0; j < minimumDegree; ++j) {
                z.children[j] = y.children[j + minimumDegree];
            }
        }
        
        y.size = minimumDegree - 1;
        
        for (int j = x.size;  j >= i; --j) {
            x.children[j + 1] = x.children[j];
        }
        
        x.children[i] = z;
        
        for (int j = x.size - 1; j >= i - 1; --j) {
            x.keys[j + 1] = x.keys[j];
        }
        
        x.keys[i - 1] = y.keys[minimumDegree - 1];
        x.size++;
    }
    
    private void bTreeInsertNonfull(BTreeNode<K> x, K k) {
        int i = x.size - 1;
        
        if (x.isLeaf()) {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            
            x.keys[i] = k;
            x.size++;
        } else {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                --i;
            }
            
            ++i;
            
            if (x.children[i].size == 2 * minimumDegree - 1) {
                bTreeSplitChild(x, i);
                
                if (k.compareTo(x.keys[i]) > 0) {
                    ++i;
                }
            }
            
            bTreeInsertNonfull(x.children[i], k);
        }
    }
}
