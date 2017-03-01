package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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
        
        final int minimumDegree;
        
        BTreeNode(int minimumDegree) {
            this.minimumDegree = minimumDegree;
            this.keys = (K[]) new Comparable[2 * minimumDegree - 1];
        }
        
        void makeInternal() {
            this.children = new BTreeNode[keys.length + 1];
        }
        
        boolean isLeaf() {
            return children == null;
        }
        
        void splitChild(BTreeNode<K> y, int i) {
            BTreeNode<K> z = new BTreeNode<>(minimumDegree);
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
            
            for (int j = size; j >= i + 1; --j) {
                children[j + 1] = children[j];
            }
            
            children[i + 1] = z;
            
            for (int j = size - 1; j >= i; --j) {
                keys[j + 1] = keys[j];
            }
            
            keys[i] = y.keys[minimumDegree - 1];
            size++;
        }
        
        int findKeyIndex(K k) {
            int keyIndex = 0;
            
            while (keyIndex < size && keys[keyIndex].compareTo(k) < 0) {
                ++keyIndex;
            }
            
            return keyIndex;
        }
        
        void deleteKey(K k) {
            int keyIndex = findKeyIndex(k);
            
            if (isLeaf()) {
                removeFromLeaf(keyIndex);
            } else {
                removeFromNonLeaf(keyIndex);
            }
        }
        
        void removeFromNonLeaf(int keyIndex) {
            K k = keys[keyIndex];
            
            if (children[keyIndex].size >= minimumDegree) {
                K predecessorKey = getPredecessorKey(keyIndex);
                keys[keyIndex] = predecessorKey;
                children[keyIndex].deleteKey(predecessorKey);
            } else if (children[keyIndex + 1].size >= minimumDegree) {
                K successorKey = getSuccessorKey(keyIndex);
                keys[keyIndex] = successorKey;
                children[keyIndex + 1].deleteKey(successorKey);
            } else {
                merge(keyIndex);
                children[keyIndex].deleteKey(k);
            }
        }
        
        void merge(int keyIndex) {
            BTreeNode<K> child = children[keyIndex];
            BTreeNode<K> sibling = children[keyIndex + 1];
            
            child.keys[minimumDegree - 1] = keys[keyIndex];
            
            for (int i = 0; i < sibling.size; ++i) {
                child.keys[i + minimumDegree] = sibling.keys[i];
            }
             
            if (!child.isLeaf()) {
                for (int i = 0; i <= sibling.size; ++i) {
                    child.children[i + minimumDegree] = sibling.children[i];
                }
            }
            
            for (int i = keyIndex + 1; i < size; ++i) {
                keys[i - 1] = keys[i];
            }
            
            for (int i = keyIndex + 2; i <= size; ++i) {
                children[i - 1] = children[i];
            }
            
            size--;
        }
        
        K getPredecessorKey(int keyIndex) {
            BTreeNode<K> current = children[keyIndex];
            
            while (!current.isLeaf()) {
                current = current.children[current.size];
            }
            
            return current.keys[current.size - 1];
        }
        
        K getSuccessorKey(int keyIndex) {
            BTreeNode<K> current = children[keyIndex + 1];
            
            while (!current.isLeaf()) {
                current = current.children[0];
            }
            
            return current.keys[0];
        }
        
        void removeFromLeaf(int keyIndex) {
            for (int i = keyIndex + 1; i < size; ++i) {
                keys[i - 1] = keys[i];
            }
            
            size--;
        }
        
        void insertNonFull(K k) {
            int i = size - 1;
            
            if (isLeaf()) {
                while (i >= 0 && keys[i].compareTo(k) > 0) {
                    keys[i + 1] = keys[i];
                    --i;
                }
                
                keys[i + 1] = k;
                size++;
            } else {
                while (i >= 0 && keys[i].compareTo(k) > 0) {
                    --i;
                }
                
                if (children[i + 1].size == 2 * minimumDegree - 1) {
                    splitChild(i + 1, children[i + 1]);
                    
                    if (keys[i + 1].compareTo(k) < 0) {
                        ++i;
                    }
                }
                
                children[i + 1].insertNonFull(k);
            }
        } 
        
        void splitChild(int i, BTreeNode<K> y) {
            BTreeNode<K> z = new BTreeNode<>(minimumDegree);
            
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
            
            for (int j = size; j >= i + 1; --j) {
                children[j + 1] = children[j];
            }
            
            children[i + 1] = z;
            
            for (int j = size - 1; j >= i; --j) {
                keys[j + 1] = keys[j];
            }
            
            keys[i] = y.keys[minimumDegree - 1];
            size++;
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
            root.deleteKey((K) key);
//            bTreeDeleteKey(root, (K) key);
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
        if (root.size == 2 * minimumDegree - 1) {
            BTreeNode<K> s = new BTreeNode<>(minimumDegree);
            s.makeInternal();
            
            s.children[0] = root;
            s.splitChild(0, root);
            
            int i = 0;
            
            if (s.keys[0].compareTo(key) < 0) {
                s.children[i].insertNonFull(key);
            }
            
            s.children[i].insertNonFull(key);
        } else {
            root.insertNonFull(key);
        }
        
//        BTreeNode<K> r = root;
//        
//        if (r.size == 2 * minimumDegree - 1) {
//            BTreeNode<K> s = new BTreeNode<>(minimumDegree);
//            root = s;
//            s.makeInternal();
//            s.children[0] = r;
//            bTreeSplitChild(s, 0);
//            bTreeInsertNonfull(s, key);
//        } else {
//            bTreeInsertNonfull(r, key);
//        }
    }
    
    private void bTreeSplitChild(BTreeNode<K> y, int i) {
//        BTreeNode<K> z = new BTreeNode<>(minimumDegree);
//        
//        if (!y.isLeaf()) {
//            z.makeInternal();
//        }
//        
//        z.size = minimumDegree - 1;
//        
//        for (int j = 0; j < minimumDegree - 1; ++j) {
//            z.keys[j] = y.keys[j + minimumDegree];
//        }
//        
//        if (!y.isLeaf()) {
//            for (int j = 0; j < minimumDegree; ++j) {
//                z.children[j] = y.children[j + minimumDegree];
//            }
//        }
//        
//        y.size = minimumDegree - 1;
//        
//        for (int j = y.size; j >= i + 1; --j) {
//            
//        }
        
//        BTreeNode<K> z = new BTreeNode<>(minimumDegree);
//        BTreeNode<K> y = x.children[i];
//        
//        if (!y.isLeaf()) {
//            z.makeInternal();
//        }
//        
//        z.size = minimumDegree - 1;
//        
//        for (int j = 0; j < minimumDegree - 1; ++j) {
//            z.keys[j] = y.keys[j + minimumDegree];
//        }
//        
//        if (!y.isLeaf()) {
//            for (int j = 0; j < minimumDegree; ++j) {
//                z.children[j] = y.children[j + minimumDegree];
//            }
//        }
//        
//        y.size = minimumDegree - 1;
//        
//        for (int j = x.size;  j >= i; --j) {
//            x.children[j + 1] = x.children[j];
//        }
//        
//        x.children[i] = z; // i + 1 ?
//        
//        for (int j = x.size - 1; j >= i; --j) {
//            x.keys[j + 1] = x.keys[j];
//        }
//        
//        x.keys[i] = y.keys[minimumDegree];
//        x.size++;
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
        
        for (int i = keyIndex + 1; i < node.size; ++i) {
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
        
    public static void main(String[] args) {
        final int MINIMUM_DEGREE = 32;
        final int UNIVERSE_SIZE = 30_000;
        final int LOAD_SIZE = 2_000_000;
        final int QUERY_SIZE = 1_000_000;
        final int DELETE_SIZE = 1_000_000;

        Map<Integer, Integer> tree1 = new BTreeMap<>(MINIMUM_DEGREE);
        Map<Integer, Integer> tree2 = new TreeMap<>();

        Random random = new Random();

        // Warmup:
        for (int i = 0; i < LOAD_SIZE; ++i) {
            int key = random.nextInt(UNIVERSE_SIZE);
            tree1.put(key, 3 * key);
            tree2.put(key, 3 * key);
        }

        for (int i = 0; i < QUERY_SIZE; ++i) {
            int key = random.nextInt(UNIVERSE_SIZE);

            if (!Objects.equals(tree1.get(key), tree2.get(key))) {
                throw new IllegalStateException(
                        "Trees do not agree during warmup.");
            }
        }

        for (int i = 0; i < DELETE_SIZE; ++i) {
            int key = random.nextInt(UNIVERSE_SIZE);

            if (!Objects.equals(tree1.remove(key), tree2.remove(key))) {
                throw new IllegalStateException(
                        "Trees do not agree during warmup.");
            }
        }

        if (tree1.size() != tree2.size()) {
            throw new IllegalStateException("Size mismatch after warmup.");
        }

        // Benchmark:
        long seed = System.currentTimeMillis();
        System.out.println("Seed = " + seed);

        Random random1 = new Random(seed);
        Random random2 = new Random(seed);

        long totalTime1 = 0L;
        long totalTime2 = 0L;

        ///// VanEmdeBoasTreeMap /////
        long startTime = System.currentTimeMillis();

        tree1 = new VanEmdeBoasTreeMap<>(UNIVERSE_SIZE);

        for (int i = 0; i < LOAD_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.put(key, 3 * key);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("BTreeMap.put in " + 
                (endTime - startTime) + " milliseconds.");

        totalTime1 += endTime - startTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < QUERY_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.get(key);
        }

        endTime = System.currentTimeMillis();

        System.out.println("BTreeMap.get in " +
                (endTime - startTime) + " milliseconds.");

        totalTime1 += endTime - startTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < DELETE_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.remove(key);
        }

        endTime = System.currentTimeMillis();

        System.out.println("BTreeMap.remove in " +
                (endTime - startTime) + " milliseconds.");

        totalTime1 += endTime - startTime;

        System.out.println("BTreeMap total time: " + totalTime1 +
                " milliseconds.");
        System.out.println();

        ///// TreeMap /////
        startTime = System.currentTimeMillis();

        tree2 = new TreeMap<>();

        for (int i = 0; i < LOAD_SIZE; ++i) {
            int key = random2.nextInt(UNIVERSE_SIZE);
            tree2.put(key, 3 * key);
        }

        endTime = System.currentTimeMillis();

        System.out.println("TreeMap.put in " + 
                (endTime - startTime) + " milliseconds.");

        totalTime2 += endTime - startTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < QUERY_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree2.get(key);
        }

        endTime = System.currentTimeMillis();

        System.out.println("TreeMap.get in " +
                (endTime - startTime) + " milliseconds.");

        totalTime2 += endTime - startTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < DELETE_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree2.remove(key);
        }

        endTime = System.currentTimeMillis();

        System.out.println("TreeMap.remove in " +
                (endTime - startTime) + " milliseconds.");

        totalTime2 += endTime - startTime;

        System.out.println("TreeMap total time: " + totalTime2 +
                " milliseconds.");
    }
}
