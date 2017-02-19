package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class implements a van Emde Boas tree -based map that maps integer keys
 * to arbitrary satellite data.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Feb 19, 2017)
 * 
 * @param <E> the type of the satellite data.
 */
public class VanEmdeBoasTreeMap<E> implements Map<Integer, E> {
    
    /**
     * Holds the minimum universe size.
     */
    private static final int MINIMUM_UNIVERSE_SIZE = 2;

    /**
     * This static inner class implements recursively the entire van Emde Boas-
     * tree.
     * 
     * @param <E> The type of the satellite data.
     */
    private static final class VEBTree<E> {
        
        /**
         * The universe size of this vEB-tree.
         */
        private final int universeSize;
        
        /**
         * The mask used to compute the low index.
         */
        private final int lowMask;
        
        /**
         * The shift length for computing the high index.
         */
        private final int highShift;
        
        /**
         * The minimum integer key in this tree.
         */
        private Integer min;
        
        /**
         * The maximum integer key in this tree.
         */
        private Integer max;
        
        /**
         * The element associated with the minimum key.
         */
        private E minValue;
        
        /**
         * The element associated with the maximum key.
         */
        private E maxValue;
        
        /**
         * The summary vEB-tree.
         */
        private final VEBTree<E> summary;
        
        /**
         * The children vEB-trees of this tree.
         */
        private final VEBTree<E>[] cluster;
        
        VEBTree(int universeSize) {
            this.universeSize = universeSize;
            int universeSizeLowerSquare = lowerSquare(universeSize);
            this.lowMask = universeSizeLowerSquare - 1;
            this.highShift = 
                    Integer.numberOfTrailingZeros(universeSizeLowerSquare);
            
            if (universeSize != MINIMUM_UNIVERSE_SIZE) {
                int upperUniverseSizeSquare = upperSquare(universeSize);
                int lowerUniverseSizeSquare = lowerSquare(universeSize);
                this.summary = new VEBTree<>(upperUniverseSizeSquare);
                this.cluster = new VEBTree[upperUniverseSizeSquare];
                
                for (int i = 0; i != upperUniverseSizeSquare; ++i) {
                    this.cluster[i] = new VEBTree<>(lowerUniverseSizeSquare);
                }
            } else {
                this.summary = null;
                this.cluster = null;
            }
        }
        
        int getUniverseSize() {
            return universeSize;
        }
        
        Integer getMinimumKey() {
            return min;
        }
        
        Integer getMaximumKey() {
            return max;
        }
        
        E get(Integer x) {
            if (x.equals(min)) {
                return minValue;
            }
            
            if (x.equals(max)) {
                return maxValue;
            }
            
            if (universeSize == 2) {
                return null;
            }
            
            return cluster[high(x)].get(low(x));
        }
        
        Integer getSuccessor(Integer x) {
            if (universeSize == 2) {
                if (x == 0 && max == 1) {
                    return 1;
                }
                
                return null;
            }
            
            if (min != null && x < min) {
                return min;
            }
            
            Integer maximumLow = cluster[high(x)].getMaximumKey();
            
            if (maximumLow != null && low(x) < maximumLow) {
                int offset = cluster[high(x)].getSuccessor(low(x));
                return index(high(x), offset);
            }
            
            Integer successorCluster = summary.getSuccessor(high(x));
            
            if (successorCluster == null) {
                return null;
            }
            
            int offset = cluster[successorCluster].getMinimumKey();
            return index(successorCluster, offset);
        }
        
        Integer getPredecessor(Integer x) {
            if (universeSize == 2) {
                if (min == null) {
                    return null;
                }
                
                if (x == 1 && min == 0) {
                    return 0;
                }
                
                return null;
            }
            
            if (max != null && x > max) {
                return max;
            }
            
            Integer minimumLow = cluster[high(x)].getMinimumKey();
            
            if (minimumLow != null && low(x) > minimumLow) {
                int offset = cluster[high(x)].getPredecessor(low(x));
                return index(high(x), offset);
            }
            
            Integer predecessorCluster = summary.getPredecessor(high(x));
            
            if (predecessorCluster == null) {
                if (min != null && x > min) {
                    return min;
                }
                
                return null;
            }
            
            int offset = cluster[predecessorCluster].getMaximumKey();
            return index(predecessorCluster, offset);
        }
        
        void treeInsert(Integer x, E value) {
            if (min == null) {
                emptyTreeInsert(x, value);
                return;
            }
            
            if (x < min) {
                Integer tmp = x;
                x = min;
                min = tmp;
                
                E tmpValue = value;
                value = minValue;
                minValue = tmpValue;
            }
            
            if (universeSize != 2) {
                Integer minimum = cluster[high(x)].getMinimumKey();
                
                if (minimum == null) {
                    summary.treeInsert(high(x), value);
                    cluster[high(x)].emptyTreeInsert(low(x), value);
                } else {
                    cluster[high(x)].treeInsert(low(x), value);
                }
            }
            
            if (max < x) {
                max = x;
                maxValue = value;
            }
        }
        
        private void emptyTreeUpdate(E value) {
            minValue = value;
            maxValue = value;
        }
        
        E treeUpdate(Integer x, E value) {
            E returnValue = null;
            
            if (min.equals(x)) {
                returnValue = minValue;
                minValue = value;
            } else if (max.equals(x)) {
                returnValue = maxValue;
                maxValue = value;
            } else if (universeSize != 2) {
                Integer minimum = cluster[high(x)].getMinimumKey();
                
                if (minimum == null) {
                    cluster[high(x)].emptyTreeUpdate(value);
                } else {
                    cluster[high(x)].treeUpdate(low(x), value);
                }
            }
            
            return returnValue;
        }
        
        E treeDelete(Integer x) {
            if (min.equals(max)) {
                E returnValue = minValue;
                min = null;
                max = null;
                minValue = null;
                maxValue = null;
                return returnValue;
            }
            
            if (universeSize == 2) {
                E returnValue;
                
                if (x == 0) {
                    min = 1;
                    returnValue = minValue;
                    minValue = maxValue;
                } else {
                    max = 0;
                    returnValue = maxValue;
                    maxValue = minValue;
                }
                
                return returnValue;
            }
            
            E returnValue;
            
            if (min.equals(x)) {
                returnValue = minValue;
                Integer firstCluster = summary.getMinimumKey();
                x = index(firstCluster, cluster[firstCluster].getMinimumKey());
                min = x;
                minValue = cluster[firstCluster].get(low(x));
                cluster[high(x)].treeDelete(low(x));
            } else {
                returnValue = cluster[high(x)].treeDelete(low(x));
            }
            
            if (cluster[high(x)].getMinimumKey() == null) {
                summary.treeDelete(high(x));
                
                if (x.equals(max)) {
                    Integer summaryMaximum = summary.getMaximumKey();
                    
                    if (summaryMaximum == null) {
                        max = min;
                        maxValue = minValue;
                    } else {
                        Integer maximumKey = 
                                cluster[summaryMaximum].getMaximumKey();
                        max = index(summaryMaximum, maximumKey);
                        maxValue = cluster[summaryMaximum].get(maximumKey);
                    }
                }
            } else if (x.equals(max)) {
                Integer maximumKey = cluster[high(x)].getMaximumKey();
                max = index(high(x), maximumKey);
                maxValue = cluster[high(x)].get(maximumKey);
            }
            
            return returnValue;
        }
        
        private void emptyTreeInsert(Integer x, E value) {
            min = x;
            max = x;
            minValue = value;
            maxValue = value;
        }
        
        private int high(int x) {
            return x >>> highShift;
        }

        private int low(int x) {
            return x & lowMask;
        }

        private int index(int x, int y) {
            return (x << highShift) | (y & lowMask);
        }
    }
    
    /**
     * The root tree.
     */
    private VEBTree<E> root;
    
    /**
     * A hash table set for keeping track of the integer keys used so far.
     */
    private final Set<Integer> set = new HashSet<>();
    
    public VanEmdeBoasTreeMap(int requestedUniverseSize) {
        checkRequestedUniverseSize(requestedUniverseSize);
        requestedUniverseSize = fixUniverseSize(requestedUniverseSize);
        root = new VEBTree<>(requestedUniverseSize);
    }

    @Override
    public E put(Integer key, E value) {
        if (set.contains(key)) {
            return root.treeUpdate(key, value);
        }
        
        root.treeInsert(key, value);
        set.add(key);
        return null;
    }

    @Override
    public E get(Object key) {
        return root.get((Integer) key);    
    }
    
    @Override
    public E remove(Object key) {
        if (set.contains((Integer) key)) {
            set.remove((Integer) key);
            return root.treeDelete((Integer) key);
        }
        
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        
        if (!key.getClass().equals(Integer.class)) {
            return false;
        }
        
        return set.contains((Integer) key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(
                "This " + getClass().getSimpleName() + 
                " does not implement 'containsValue'.");
    }
    
    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }
    
    @Override
    public int size() {
        return set.size();
    }
    
    @Override
    public void clear() {
        root = new VEBTree<>(root.universeSize);
        set.clear();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends E> m) {
        for (Map.Entry<? extends Integer, ? extends E> entry : m.entrySet()) {
            root.treeInsert(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Integer> keySet() {
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Collection<E> values() {
        throw new UnsupportedOperationException(
                "This " + getClass().getSimpleName() + " does not implement " +
                "'values'.");
    }

    @Override
    public Set<Entry<Integer, E>> entrySet() {
        throw new UnsupportedOperationException(
                "This " + getClass().getSimpleName() + " does not implement " +
                "'entrySet'.");
    }
    
    public Integer getMinimum() {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for minimum integer key in empty VanEmdeBoasTreeMap.");
        }
            
        return root.getMinimumKey();
    }
    
    public Integer getPredessor(Integer x) {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for predecessor integer key in empty VanEmdeBoasTreeMap.");
        }
        
        checkIntegerWithinUniverse(x, root.getUniverseSize());
        return root.getPredecessor(x);
    }
    
    public Integer getSuccessor(Integer x) {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for successor integer key in empty VanEmdeBoasTreeMap.");
        }
        
        checkIntegerWithinUniverse(x, root.getUniverseSize());
        return root.getSuccessor(x);
    }
    
    public Integer getMaximum() {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for maximum integer key in empty VanEmdeBoasTreeMap.");
        }
        
        return root.getMaximumKey();
    }
    
    private void checkIntegerWithinUniverse(int x, int universeSize) {
        if (x < 0) {
            throw new IllegalArgumentException(
                    "This VanEmdeBoasTreeMap supports only non-negative " +
                    "keys. Received " + x + ".");
        }

        if (x >= universeSize) {
            throw new IllegalArgumentException(
                    "The input integer is too large: " + x + ". " +
                    "Must be at most " + (universeSize - 1) + ".");
        }
    }
    
    /**
     * Returns the fixed universe size that is a power of two and no smaller
     * than {@code requestedUniverseSize}.
     * 
     * @param requestedUniverseSize the requested universe size.
     * @return the fixed universe size.
     */
    private int fixUniverseSize(int requestedUniverseSize) {
        int tmp = Integer.highestOneBit(requestedUniverseSize);
        return tmp == requestedUniverseSize ? 
                      requestedUniverseSize : 
                     (tmp << 1);
    }
    
    private void checkRequestedUniverseSize(int requestedUniverseSize) {
        if (requestedUniverseSize < MINIMUM_UNIVERSE_SIZE) {
            throw new IllegalArgumentException(
                    "The requested universe size is too small: " + 
                    requestedUniverseSize + ". Should be at least " +
                    MINIMUM_UNIVERSE_SIZE + ".");
        }
    }
    
    private static int upperSquare(int number) {
        double exponent = Math.ceil(Math.log(number) / Math.log(2.0) / 2.0);
        return (int) Math.pow(2.0, exponent);
    }
    
    private static int lowerSquare(int number) {
        double exponent = Math.floor(Math.log(number) / Math.log(2.0) / 2.0);
        return (int) Math.pow(2.0, exponent);
    }
    
    public static void main(String[] args) {
        final int UNIVERSE_SIZE = 30_000;
        final int LOAD_SIZE = 2_000_000;
        final int QUERY_SIZE = 1_000_000;
        final int DELETE_SIZE = 1_000_000;
        
        Map<Integer, Integer> tree1 = new VanEmdeBoasTreeMap<>(UNIVERSE_SIZE);
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
        
        System.out.println("VanEmdeBoasTreeMap.put in " + 
                (endTime - startTime) + " milliseconds.");
        
        totalTime1 += endTime - startTime;
        
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < QUERY_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.get(key);
        }
        
        endTime = System.currentTimeMillis();
        
        System.out.println("VanEmdeBoasTreeMap.get in " +
                (endTime - startTime) + " milliseconds.");
        
        totalTime1 += endTime - startTime;
        
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < DELETE_SIZE; ++i) {
            int key = random1.nextInt(UNIVERSE_SIZE);
            tree1.remove(key);
        }
        
        endTime = System.currentTimeMillis();
        
        System.out.println("VanEmdeBoasTreeMap.remove in " +
                (endTime - startTime) + " milliseconds.");
        
        totalTime1 += endTime - startTime;
        
        System.out.println("VanEmdeBoasTreeMap total time: " + totalTime1 +
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
