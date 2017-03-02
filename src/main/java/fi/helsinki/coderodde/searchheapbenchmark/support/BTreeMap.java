package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
    
    /**
     * The minimum number of children of any non-root internal node.
     */
    private static final int DEFAULT_DEGREE = 32;
    
    /**
     * This class implements the B-tree nodes.
     * 
     * @param <K> the key type. Must be comparable.
     */
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
    
    public BTreeMap(int degree) {
        this.minimumDegree = Math.max(degree, MINIMUM_DEGREE);
        this.root = new BTreeNode(this.minimumDegree);
    }
    
    public BTreeMap() {
        this(DEFAULT_DEGREE);
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
        return (V) bTreeSearch(root, (K) key);
//        return map.get(key);    
    }

    @Override
    public V put(K key, V value) {
        if (map.containsKey(key)) {
            return map.put(key, value);
        }
        
        bTreeInsertKey(key);
        map.put(key, value);
        return null;
    }

    @Override
    public V remove(Object key) {
        if (map.containsKey((K) key)) {
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
    
    public K getMaximumKey() {
        checkBTreeMapNotEmpty();
        BTreeNode<K> current = root;
        
        while (!current.isLeaf()) {
            current = current.children[current.size];
        }
        
        return current.keys[current.size - 1];
    }
    
    public K getMinimumKey() {
        checkBTreeMapNotEmpty();
        BTreeNode<K> current = root;
        
        while (!current.isLeaf()) {
            current = current.children[0];
        }
        
        return current.keys[0];
    }
    
    public int getMinimumDegree() {
        return minimumDegree;
    }
    
    private void checkBTreeMapNotEmpty() {
        if (map.isEmpty()) {
            throw new NoSuchElementException("This BTreeMap is empty.");
        }
    }
    
    private void bTreeInsertKey(K key) {
        BTreeNode<K> r = root;
        
        if (r.size == 2 * minimumDegree - 1) {
            BTreeNode<K> s = new BTreeNode<>(minimumDegree);
            root = s;
            s.makeInternal();
            s.children[0] = r;
            bTreeSplitChild(s, 0);
            bTreeInsertNonFull(s, key);
        } else {
            bTreeInsertNonFull(r, key);
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
        
        for (int j = x.size; j >= i; --j) {
            x.children[j + 1] = x.children[j];
        }
        
        x.children[i + 1] = z; // ?
        
        for (int j = x.size - 1; j >= i; --j) { // ?
            x.keys[j + 1] = x.keys[j];
        }
        
        x.keys[i] = y.keys[minimumDegree - 1];
        x.size++;
    }
    
    private void bTreeInsertNonFull(BTreeNode<K> x, K k) {
        int i = x.size - 1;
        
        if (x.isLeaf()) {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                --i;
            }
            
            x.keys[i + 1] = k; // ?
            x.size++;
        } else {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                --i;
            }
                
            ++i;
            
            if (x.children[i].size == 2 * minimumDegree - 1) {
                bTreeSplitChild(x, i);
                
                if (k.compareTo(x.keys[i]) > 0) {
                    i++;
                }
            }
            
            bTreeInsertNonFull(x.children[i], k);
        }
    }
    
    private static <K extends Comparable<? super K>> 
        K findMinimumKey(BTreeNode<K> x) {
        while (!x.isLeaf()) {
            x = x.children[0];
        }
        
        return x.keys[0];
    }
        
    private static <K extends Comparable<? super K>>
        K findMaximumKey(BTreeNode<K> x) {
        while (!x.isLeaf()) {
            x = x.children[x.size];
        }       
        
        return x.keys[x.size - 1];
    }
        
    private static <K extends Comparable<? super K>> 
        BTreeNode<K> getMinimumNode(BTreeNode<K> x) {
        while (!x.isLeaf()) {
            x = x.children[0];
        }
        
        return x;
    }
        
    private static <K extends Comparable<? super K>> 
        BTreeNode<K> getMaximumNode(BTreeNode<K> x) {
        while (!x.isLeaf()) {
            x = x.children[x.size];
        }

        return  x;
    }
    
    private void bTreeDeleteKey(BTreeNode<K> x, K key) {
        int keyIndex = findKeyIndex(x, key);
        
        if (keyIndex >= 0) {
            if (x.isLeaf()) {
                // Case 1:
                removeFromLeafNode(x, keyIndex);
                return;
            }
            
            BTreeNode<K> y = x.children[keyIndex];
            
            if (y.size >= minimumDegree) {
                // Case 2a:
                BTreeNode<K> tmp = getMaximumNode(y);
                K keyPrime = tmp.keys[tmp.size - 1];
                x.keys[keyIndex] = keyPrime;
                tmp.keys[--tmp.size] = null;
                return;
            }
            
            BTreeNode<K> z = x.children[keyIndex + 1];
            
            if (z.size >= minimumDegree) {
                // Case 2b:
                BTreeNode<K> tmp = getMinimumNode(z);
                K keyPrime = tmp.keys[0];
                x.keys[keyIndex] = keyPrime;
                
                for (int i = 1; i != tmp.size; ++i) {
                    tmp.keys[i - 1] = tmp.keys[i];
                }
                
                tmp.keys[--tmp.size] = null;
                return;
            }
            
            // Case 2c:
            // Merge 'key' and all contents of 'z' to the end of 'y'
            y.keys[y.size] = key;
            
            for (int i = 0, j = y.size + 1; i != z.size; ++i, ++j) {
                y.keys[j] = z.keys[i];
                y.children[j] = z.children[i];
            }
            
            y.size = 2 * minimumDegree - 1;
            y.children[y.size] = z.children[z.size];
            
            for (int i = keyIndex + 1; i < x.size; ++i) {
                x.keys[i - 1] = x.keys[i];
                x.children[i - 1] = x.children[i];
            }
            
            x.children[x.size - 1] = x.children[x.size];
            x.children[x.size] = null;
            x.size--;
            bTreeDeleteKey(y, key);
        } else {
            int childIndex = -1;
            
            for (int i = 0; i <= x.size; ++i) {
                BTreeNode<K> currentChild = x.children[i];
                
                if (currentChild.keys[0].compareTo(key) <= 0
                        && key.compareTo(
                                currentChild.keys[currentChild.size - 1]) <= 0) {
                    childIndex = i;
                    break;
                }
            }
            
            BTreeNode<K> targetChild = x.children[childIndex];
            
            if (targetChild.size == minimumDegree - 1) {
                if (childIndex > 0 
                        && x.children[childIndex - 1].size >= minimumDegree) {
                // Case 3a: Move from left sibling:
                        BTreeNode<K> leftSibling = x.children[childIndex - 1];
                        K lastLeftSiblingKey = 
                                leftSibling.keys[leftSibling.size - 1];
                        x.keys[childIndex] = lastLeftSiblingKey;
                        
                        
                        
                        
                } else if (childIndex < x.size + 1
                        && x.children[childIndex + 1].size >= minimumDegree) {
                // Case 3a once again, but with right sibling:
                        BTreeNode<K> rightSibling = x.children[childIndex + 1];
                
                        
                        
                        
                } else {
                    // Case 3b: Merge right sibling with the target child:
                    BTreeNode<K> rightSibling = x.children[childIndex + 1];
                    targetChild.keys[targetChild.size++] = x.keys[childIndex];
                    
                    for (int i = 0, j = targetChild.size; 
                            i != rightSibling.size; 
                            ++i, ++j) {
                        targetChild.keys[j] = rightSibling.keys[i];
                        targetChild.children[j] = rightSibling.children[i];
                    }
                    
                    targetChild.size += rightSibling.size;
                    
                    targetChild.children[targetChild.size] = 
                            rightSibling.children[rightSibling.size];
                    
                    // Shift in x:
                    for (int i = childIndex + 1; i < x.size; ++i) {
                        x.keys[i - 1] = x.keys[i];
                        x.children[i - 1] = x.children[i];
                    }
                    
                    x.children[x.size--] = null;
                }
            }
            
            bTreeDeleteKey(targetChild, key);
        }
    }
    
    private void removeFromLeafNode(BTreeNode<K> x, int removedKeyIndex) {
        for (int i = removedKeyIndex + 1; i < x.size; ++i) {
            x.keys[i - 1] = x.keys[i];
        }
        
        x.keys[--x.size] = null;
    }
    
    private static <K extends Comparable<? super K>> 
        int findKeyIndex(BTreeNode<K> x, K key) {
        for (int i = 0; i != x.size; ++i) {
            if (x.keys[i].compareTo(key) == 0) {
                return i;
            }
        }
        
        return -1;
    }
    
    private K bTreeSearch(BTreeNode<K> x, K k) {
        int i = 0;
        
        while (i < x.size && k.compareTo(x.keys[i]) > 0) {
//            System.out.println(i);
            ++i;
        }
        
        if (i < x.size && k.compareTo(x.keys[i]) == 0) {
            return k;
        } else if (x.isLeaf()) {
            return null;
        } else {
//            System.out.println("yeah: " + k);
            return bTreeSearch(x.children[i], k);
        }
    }
        
    public static void main(String[] args) {
        final int MINIMUM_DEGREE = 16;
        final int UNIVERSE_SIZE = 30_000;
        final int LOAD_SIZE = 20_000;
        final int QUERY_SIZE = 10_000;
        final int DELETE_SIZE = 5_000;

        Map<Integer, Integer> tree1 = new BTreeMap<>(MINIMUM_DEGREE);
        Map<Integer, Integer> tree2 = new TreeMap<>();
        
        Random random = new Random();

        // Warmup:
        for (int i = 0; i < LOAD_SIZE; ++i) {
            int key = random.nextInt(UNIVERSE_SIZE);
            tree1.put(key, key);
            tree2.put(key, key);
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

        long startTime = System.currentTimeMillis();

        tree1 = new BTreeMap<>(MINIMUM_DEGREE);

        for (int i = 0; i < LOAD_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.put(key, key);
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
            int key = random1.nextInt(LOAD_SIZE);
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
            int key = random1.nextInt(LOAD_SIZE);
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
