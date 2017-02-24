//package fi.helsinki.coderodde.searchheapbenchmark.support;
//
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Set;
//
///**
// * This class implements a van Emde Boas tree -based set for integers.
// * 
// * This class is not bug-free! (Feb 24, 2017).
// * 
// * @author Rodion "rodde" Efremov
// * @version 1.6 (Feb 24, 2017)
// */
//public class VanEmdeBoasTreeSet implements Set<Integer> {
//
//    /**
//     * Holds the minimum universe size.
//     */
//    private static final int MINIMUM_UNIVERSE_SIZE = 2;
//
//    @Override
//    public boolean contains(Object o) {
//        return root.contains((Integer) o);    
//    }
//
//    @Override
//    public Iterator<Integer> iterator() {
//        throw new UnsupportedOperationException("iterator() not supported."); 
//    }
//
//    @Override
//    public Object[] toArray() {
//        throw new UnsupportedOperationException("toArray() not supported.");
//    }
//
//    @Override
//    public <T> T[] toArray(T[] a) {
//        throw new UnsupportedOperationException("toArray(T[]) not supported."); 
//    }
//
//    @Override
//    public boolean add(Integer e) {
//        return root.treeInsert(e);
//    }
//
//    @Override
//    public boolean containsAll(Collection<?> c) {
//        for (Object o : c) {
//            if (!root.contains(o)) {
//                return false;
//            }
//        }
//        
//        return true;
//    }
//
//    @Override
//    public boolean addAll(Collection<? extends Integer> c) {
//        boolean changed = false;
//        
//        for (Integer i : c) {
//            if (add(i)) {
//                changed = true;
//            }
//        }
//        
//        return changed;
//    }
//
//    @Override
//    public boolean retainAll(Collection<?> c) {
//        throw new UnsupportedOperationException(
//                "retainAll(Collection<?>) not supported.");
//    }
//
//    @Override
//    public boolean removeAll(Collection<?> c) {
//        boolean changed = false;
//        
//        for (Object o : c) {
//            if (remove(o)) {
//                changed = true;
//            }
//        }
//        
//        return changed;
//    }
//
//    /**
//     * This static inner class implements recursively the entire van Emde Boas-
//     * tree.
//     * 
//     * @param <E> The type of the satellite data.
//     */
//    private static final class VEBTree<E> {
//
//        /**
//         * The universe size of this vEB-tree.
//         */
//        private final int universeSize;
//
//        /**
//         * The mask used to compute the low index.
//         */
//        private final int lowMask;
//
//        /**
//         * The shift length for computing the high index.
//         */
//        private final int highShift;
//
//        /**
//         * The minimum integer key in this tree.
//         */
//        private Integer min;
//
//        /**
//         * The maximum integer key in this tree.
//         */
//        private Integer max;
//
//        /**
//         * The summary vEB-tree.
//         */
//        private final VEBTree<E> summary;
//
//        /**
//         * The children vEB-trees of this tree.
//         */
//        private final VEBTree<E>[] cluster;
//
//        VEBTree(int universeSize) {
//            this.universeSize = universeSize;
//            int universeSizeLowerSquare = lowerSquare(universeSize);
//            this.lowMask = universeSizeLowerSquare - 1;
//            this.highShift = 
//                    Integer.numberOfTrailingZeros(universeSizeLowerSquare);
//
//            if (universeSize != MINIMUM_UNIVERSE_SIZE) {
//                int upperUniverseSizeSquare = upperSquare(universeSize);
//                int lowerUniverseSizeSquare = lowerSquare(universeSize);
//                this.summary = new VEBTree<>(upperUniverseSizeSquare);
//                this.cluster = new VEBTree[upperUniverseSizeSquare];
//
//                for (int i = 0; i != upperUniverseSizeSquare; ++i) {
//                    this.cluster[i] = new VEBTree<>(lowerUniverseSizeSquare);
//                }
//            } else {
//                this.summary = null;
//                this.cluster = null;
//            }
//        }
//
//        int getUniverseSize() {
//            return universeSize;
//        }
//
//        Integer getMinimum() {
//            return min;
//        }
//
//        Integer getMaximum() {
//            return max;
//        }
//        
//        boolean contains(Integer x) {
//            if (x.equals(min) || x.equals(max)) {
//                return true;
//            } else if (universeSize == 2) {
//                // We are at leaf, no where to recur.
//                return false;
//            } else {
//                return cluster[high(x)].contains(low(x));
//            }
//        }
//        
//        Integer getSuccessor(Integer x) {
//            if (universeSize == 2) {
//                if (x == 0 && max == 1) {
//                    return 1;
//                }
//
//                return null;
//            }
//
//            if (min != null && x < min) {
//                return min;
//            }
//
//            Integer maximumLow = cluster[high(x)].getMaximum();
//
//            if (maximumLow != null && low(x) < maximumLow) {
//                int offset = cluster[high(x)].getSuccessor(low(x));
//                return index(high(x), offset);
//            }
//
//            Integer successorCluster = summary.getSuccessor(high(x));
//
//            if (successorCluster == null) {
//                return null;
//            }
//
//            int offset = cluster[successorCluster].getMinimum();
//            return index(successorCluster, offset);
//        }
//
//        Integer getPredecessor(Integer x) {
//            if (universeSize == 2) {
//                if (min == null) {
//                    return null;
//                }
//
//                if (x == 1 && min == 0) {
//                    return 0;
//                }
//
//                return null;
//            }
//
//            if (max != null && x > max) {
//                return max;
//            }
//
//            Integer minimumLow = cluster[high(x)].getMinimum();
//
//            if (minimumLow != null && low(x) > minimumLow) {
//                int offset = cluster[high(x)].getPredecessor(low(x));
//                return index(high(x), offset);
//            }
//
//            Integer predecessorCluster = summary.getPredecessor(high(x));
//
//            if (predecessorCluster == null) {
//                if (min != null && x > min) {
//                    return min;
//                }
//
//                return null;
//            }
//
//            int offset = cluster[predecessorCluster].getMaximum();
//            return index(predecessorCluster, offset);
//        }
//
//        void treeInsert(Integer x, E value) {
//            if (min == null) {
//                emptyTreeInsert(x, value);
//                return;
//            }
//
//            if (x < min) {
//                Integer tmp = x;
//                x = min;
//                min = tmp;
//            }
//
//            if (universeSize != 2) {
//                Integer minimum = cluster[high(x)].getMinimum();
//
//                if (minimum == null) {
//                    summary.treeInsert(high(x), value);
//                    cluster[high(x)].emptyTreeInsert(low(x), value);
//                } else {
//                    cluster[high(x)].treeInsert(low(x), value);
//                }
//            }
//
//            if (max < x) {
//                max = x;
//            }
//        }
//
//        void treeDelete(Integer x) {
//            if (min.equals(max)) {
//                min = null;
//                max = null;
//                return;
//            }
//
//            if (universeSize == 2) {
//                if (x == 0) {
//                    min = 1;
//                } else {
//                    max = 0;
//                }
//
//                return;
//            }
//
//            if (min.equals(x)) {
//                Integer firstCluster = summary.getMinimum();
//                x = index(firstCluster, cluster[firstCluster].getMinimum());
//                min = x;
//            }
//            
//            cluster[high(x)].treeDelete(low(x));
//
//            if (cluster[high(x)].getMinimum() == null) {
//                summary.treeDelete(high(x));
//
//                if (x.equals(max)) {
//                    Integer summaryMaximum = summary.getMaximum();
//
//                    if (summaryMaximum == null) {
//                        max = min;
//                    } else {
//                        max = index(summaryMaximum, 
//                                    cluster[summaryMaximum].getMaximum());
//                    }
//                }
//            } else if (x.equals(max)) {
//                max = index(high(x), cluster[high(x)].getMaximum());
//            }
//        }
//
//        private void emptyTreeInsert(Integer x, E value) {
//            min = x;
//            max = x;
//        }
//
//        private int high(int x) {
//            return x >>> highShift;
//        }
//
//        private int low(int x) {
//            return x & lowMask;
//        }
//
//        private int index(int x, int y) {
//            return (x << highShift) | (y & lowMask);
//        }
//    }
//
//    /**
//     * The root tree.
//     */
//    private VEBTree<E> root;
//
//    /**
//     * A hash table set for keeping track of the integer keys used so far.
//     */
//    private final Set<Integer> set = new HashSet<>();
//
//    public VanEmdeBoasTreeSet(int requestedUniverseSize) {
//        checkRequestedUniverseSize(requestedUniverseSize);
//        requestedUniverseSize = fixUniverseSize(requestedUniverseSize);
//        root = new VEBTree<>(requestedUniverseSize);
//    }
//    
//    public E put(Integer key, E value) {
//        if (set.contains(key)) {
//            return root.treeUpdate(key, value);
//        }
//
//        root.treeInsert(key, value);
//        set.add(key);
//        return null;
//    }
//
//    public E get(Object key) {
//        return root.get((Integer) key);    
//    }
//
//    public E remove(Object key) {
//        if (set.contains((Integer) key)) {
//            set.remove((Integer) key);
//            return root.treeDelete((Integer) key);
//        }
//
//        return null;
//    }
//
//    public boolean containsKey(Object key) {
//        if (key == null) {
//            return false;
//        }
//
//        if (!key.getClass().equals(Integer.class)) {
//            return false;
//        }
//
//        return set.contains((Integer) key);
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return set.isEmpty();
//    }
//
//    @Override
//    public int size() {
//        return set.size();
//    }
//
//    @Override
//    public void clear() {
//        root = new VEBTree<>(root.universeSize);
//        set.clear();
//    }
//
//    public void putAll(Map<? extends Integer, ? extends E> m) {
//        for (Map.Entry<? extends Integer, ? extends E> entry : m.entrySet()) {
//            root.treeInsert(entry.getKey(), entry.getValue());
//        }
//    }
//    
//    public Integer getMinimum() {
//        if (set.isEmpty()) {
//            throw new NoSuchElementException(
//            "Asking for minimum integer key in empty VanEmdeBoasTreeMap.");
//        }
//
//        return root.getMinimum();
//    }
//    
//    public Integer getPredessor(Integer x) {
//        if (set.isEmpty()) {
//            throw new NoSuchElementException(
//            "Asking for predecessor integer key in empty VanEmdeBoasTreeMap.");
//        }
//
//        checkIntegerWithinUniverse(x, root.getUniverseSize());
//        return root.getPredecessor(x);
//    }
//
//    public Integer getSuccessor(Integer x) {
//        if (set.isEmpty()) {
//            throw new NoSuchElementException(
//            "Asking for successor integer key in empty VanEmdeBoasTreeMap.");
//        }
//
//        checkIntegerWithinUniverse(x, root.getUniverseSize());
//        return root.getSuccessor(x);
//    }
//
//    public Integer getMaximum() {
//        if (set.isEmpty()) {
//            throw new NoSuchElementException(
//            "Asking for maximum integer key in empty VanEmdeBoasTreeMap.");
//        }
//
//        return root.getMaximum();
//    }
//
//    private void checkIntegerWithinUniverse(int x, int universeSize) {
//        if (x < 0) {
//            throw new IllegalArgumentException(
//                    "This VanEmdeBoasTreeMap supports only non-negative " +
//                    "keys. Received " + x + ".");
//        }
//
//        if (x >= universeSize) {
//            throw new IllegalArgumentException(
//                    "The input integer is too large: " + x + ". " +
//                    "Must be at most " + (universeSize - 1) + ".");
//        }
//    }
//
//    /**
//     * Returns the fixed universe size that is a power of two and no smaller
//     * than {@code requestedUniverseSize}.
//     * 
//     * @param requestedUniverseSize the requested universe size.
//     * @return the fixed universe size.
//     */
//    private int fixUniverseSize(int requestedUniverseSize) {
//        int tmp = Integer.highestOneBit(requestedUniverseSize);
//        return tmp == requestedUniverseSize ? 
//                      requestedUniverseSize : 
//                     (tmp << 1);
//    }
//
//    private void checkRequestedUniverseSize(int requestedUniverseSize) {
//        if (requestedUniverseSize < MINIMUM_UNIVERSE_SIZE) {
//            throw new IllegalArgumentException(
//                    "The requested universe size is too small: " + 
//                    requestedUniverseSize + ". Should be at least " +
//                    MINIMUM_UNIVERSE_SIZE + ".");
//        }
//    }
//
//    private static int upperSquare(int number) {
//        double exponent = Math.ceil(Math.log(number) / Math.log(2.0) / 2.0);
//        return (int) Math.pow(2.0, exponent);
//    }
//
//    private static int lowerSquare(int number) {
//        double exponent = Math.floor(Math.log(number) / Math.log(2.0) / 2.0);
//        return (int) Math.pow(2.0, exponent);
//    }
//}
