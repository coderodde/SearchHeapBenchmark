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
    
    private static final class BTreeNode<K extends Comparable<? super K>> {
        
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
            this.keys = (K[]) new Comparable[2 * minimumDegree - 1];
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
        return bTreeSearch(root, (K) key);
//        return map.containsKey(key);
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
        if (map.containsKey(key)) {
            return map.put(key, value);
        }
        
        bTreeInsert(key);
        map.put(key, value);
        return null;
    }

    @Override
    public V remove(Object key) {
        if (map.containsKey((K) key)) {
            // Remove from B-tree.
            bTreeDeleteKey(root, (K) key);
            return map.remove(key);
        }
        
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
    
    private boolean bTreeSearch(BTreeNode<K> x, K key) {
        int i = 0;
        
        while (i < x.size && key.compareTo(x.keys[i]) > 0) {
            ++i;
        }
        
        if (i < x.size && key.equals(x.keys[i])) {
            return true;
        } else if (x.isLeaf()) {
            return false;
        } else {
            return bTreeSearch(x.children[i], key);
        }
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
            
            x.keys[i + 1] = k;
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
    
    private void bTreeDeleteKey(BTreeNode<K> node, K key) {
        int keyIndex = findKeyIndex(node, key);
        
        if (node.isLeaf()) {
            removeFromLeafNode(node, keyIndex);
        } else {
            removeFromInternalNode(node, keyIndex);
        }
    }
        
    private void removeFromInternalNode(BTreeNode<K> node, int keyIndex) {
        K key = node.keys[keyIndex];
        
        if (node.children[keyIndex].size >= minimumDegree) {
            K predecessorKey = getPredecessorKey(node, keyIndex);
            node.keys[keyIndex] = predecessorKey;
            bTreeDeleteKey(node.children[keyIndex], predecessorKey);
        } else if (node.children[keyIndex + 1].size >= minimumDegree) {
            K successorKey = getSuccessorKey(node, keyIndex);
            node.keys[keyIndex] = successorKey;
            bTreeDeleteKey(node.children[keyIndex + 1], successorKey);
        } else {
            merge(node, keyIndex);
            bTreeDeleteKey(node.children[keyIndex], key);
        }
    }
        
    private void merge(BTreeNode<K> node, int keyIndex) {
        BTreeNode<K> child = node.children[keyIndex];
        BTreeNode<K> sibling = node.children[keyIndex + 1];
        
        child.keys[minimumDegree - 1] = node.keys[keyIndex];
        
        for (int i = 0; i != sibling.size; ++i) {
            child.keys[i + minimumDegree] = sibling.keys[i];
        }
        
        if (!child.isLeaf()) {
            for (int i = 0; i <= sibling.size; ++i) {
                child.children[i + minimumDegree] = sibling.children[i];
            }
        }
        
        for (int i = keyIndex + 1; i != node.size; ++i) {
            node.keys[i - 1] = node.keys[i];
        }
        
        for (int i = keyIndex + 2; i <= node.size; ++i) {
            node.children[i - 1] = node.children[i];
        }
        
        child.size += sibling.size + 1;
        node.size--;
    }
        
    private static <K extends Comparable<? super K>>
        void removeFromLeafNode(BTreeNode<K> node, int keyIndex) {
        for (int i = keyIndex + 1; i != node.size; ++i) {
            node.keys[i - 1] = node.keys[i];
        }
        
        node.size--;
    }
        
    private K getPredecessorKey(BTreeNode<K> node, int keyIndex) {
        BTreeNode<K> currentNode = node.children[keyIndex];
        
        while (!currentNode.isLeaf()) {
            currentNode = currentNode.children[currentNode.size];
        }
        
        return currentNode.keys[currentNode.size - 1];
    }
        
    private K getSuccessorKey(BTreeNode<K> node, int keyIndex) {
        BTreeNode<K> currentNode = node.children[keyIndex + 1];
        
        while (!currentNode.isLeaf()) {
            currentNode = currentNode.children[0];
        }
        
        return currentNode.keys[0];
    }
    
    private static <K extends Comparable<? super K>> 
        int findKeyIndex(BTreeNode<K> node, K key) {
        int index = 0;
        
        while (index < node.size && node.keys[index].compareTo(key) != 0) {
            ++index;
        }
        
        return index;
    }
    
//    private void bTreeDeleteKeyShit(BTreeNode<K> x, K key) {
//        if (!x.isLeaf()) {
//            BTreeNode<K> y = precedingChild(x);
//            BTreeNode<K> z = successorChild(x);
//            
//            if (y.size > minimumDegree - 1) {
//                K keyPrime = findPredecessorKey(key, x);
//                moveKey(keyPrime, y, x);
//                moveKey(key, x, z);
//                bTreeDeleteKey(z, key);
//            } else if (z.size > minimumDegree - 1) {
//                K keyPrime = findSuccessorKey(key, x);
//                moveKey(keyPrime, z, x);
//                moveKey(key, x, y);
//                bTreeDeleteKey(y, key);
//            } else {
//                moveKey(key, x, y);
//                mergeNodes(y, z);
//                bTreeDeleteKey(y, key);
//            }
//        } else {
//            BTreeNode<K> y = precedingChild(x);
//            BTreeNode<K> z = successorChild(x);
//            BTreeNode<K> w = x.children[0];
//            K v = x.keys[0];
//            
//            if (x.size > minimumDegree - 1) {
//                removeKey(key, x);
//            } else if (y.size > minimumDegree - 1) {
//                K keyPrime = findPredecessorKey(v, w);
//                moveKey(keyPrime, y, w);
//                keyPrime = findSuccessorKey(v, w);
//                moveKey(keyPrime, w, x);
//                bTreeDeleteKey(x, key);
//            } else if (w.size > minimumDegree - 1) {
//                K keyPrime = findSuccessorKey(v, w);
//                moveKey(keyPrime, z, w);
//                keyPrime = findPredecessorKey(v, w);
//                moveKey(keyPrime, w, x);
//                bTreeDeleteKey(x, key);
//            } else {
//                BTreeNode<K> s = findSibling(w);
//                BTreeNode<K> wPrime = w.children[0];
//                
//                if (wPrime.size == minimumDegree - 1) {
//                    mergeNodes(wPrime, w);
//                    mergeNodes(w, s);
//                    bTreeDeleteKey(x, key);
//                } else {
//                    moveKey(v, w, x);
//                    bTreeDeleteKey(x, key);
//                }
//            }
//        }
//    }
    
//    private BTreeNode<K> findSibling(BTreeNode<K> x) {
//        return null;
//    }
//    
//    private void removeKey(K key, BTreeNode<K> x) {
//        int i = 0;
//        
//        while (i < x.size && x.keys[i].compareTo(key) != 0) {
//            ++i;
//        }
//        
//        ++i;
//        
//        while (i < x.size) {
//            x.keys[i - 1] = x.keys[i];
//            x.children[i] = x.children[i + 1];
//        }
//        
//        x.children[x.size] = null;
//        x.size--;
//        x.keys[x.size] = null;
//    }
//    
//    private void mergeNodes(BTreeNode<K> y, BTreeNode<K> z) {
//        
//    }
//    
//    private K findPredecessorKey(K key, BTreeNode<K> x) {
//        int i = 0;
//        
//        while (i < x.size && x.keys[i].compareTo(key) != 0) {
//            ++i;
//        }
//        
//        return x.keys[i - 1];
//    }
//    
//    private K findSuccessorKey(K key, BTreeNode<K> x) {
//        int i = 0;
//        
//        while (i < x.size && x.keys[i].compareTo(key) != 0) {
//            ++i;
//        }
//        
//        return x.keys[i + 1];
//    }
//    
//    private BTreeNode<K> precedingChild(BTreeNode<K> x) {
//        int i = 0;
//        
////        while (i < x.size && x.keys[i].compareTo(key))
//        
//        return null;
//    }
//    
//    private BTreeNode<K> successorChild(BTreeNode<K> x) {
//        return null;
//    }
//    
//    private void moveKey(K key, BTreeNode<K> y, BTreeNode<K> z) {
//        
//    }
}
