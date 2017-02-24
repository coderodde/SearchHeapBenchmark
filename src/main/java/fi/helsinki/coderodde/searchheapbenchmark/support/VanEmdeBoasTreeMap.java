package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class implements a van Emde Boas tree -based map that maps integer keys
 * to arbitrary satellite data.
 * 
 * This class is not bug-free! (Feb 24, 2017).
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
        
        void treeInsert(Integer x) {
            if (min == null) {
                emptyTreeInsert(x);
                return;
            }

            if (x < min) {
                Integer tmp = x;
                x = min;
                min = tmp;
            }

            if (universeSize != 2) {
                Integer minimum = cluster[high(x)].getMinimumKey();

                if (minimum == null) {
                    summary.treeInsert(high(x));
                    cluster[high(x)].emptyTreeInsert(low(x));
                } else {
                    cluster[high(x)].treeInsert(low(x));
                }
            }

            if (max < x) {
                max = x;
            }
        }

        void treeDelete(Integer x) {
            if (min.equals(max)) {
                min = null;
                max = null;
                return;
            }

            if (universeSize == 2) {
                if (x == 0) {
                    min = 1;
                } else {
                    max = 0;
                }
                
                max = min;
                return;
            }

            if (min.equals(x)) {
                Integer firstCluster = summary.getMinimumKey();
                x = index(firstCluster, cluster[firstCluster].getMinimumKey());
                min = x;
            }
            
            cluster[high(x)].treeDelete(low(x));

            if (cluster[high(x)].getMinimumKey() == null) {
                summary.treeDelete(high(x));

                if (x.equals(max)) {
                    Integer summaryMaximum = summary.getMaximumKey();

                    if (summaryMaximum == null) {
                        max = min;
                    } else {
                        Integer maximumKey = 
                                cluster[summaryMaximum].getMaximumKey();
                        max = index(summaryMaximum, maximumKey);
                    }
                }
            } else if (x.equals(max)) {
                Integer maximumKey = cluster[high(x)].getMaximumKey();
                max = index(high(x), maximumKey);
            }
        }

        private void emptyTreeInsert(Integer x) {
            min = x;
            max = x;
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
     * This map serves two purposes: first, it allows us to keep track of all 
     * the integer keys in the van Emde Boas tree, second, it maps each present
     * integer key to its satellite data.
     */
    private final Map<Integer, E> map = new HashMap<>();

    public VanEmdeBoasTreeMap(int requestedUniverseSize) {
        checkRequestedUniverseSize(requestedUniverseSize);
        requestedUniverseSize = fixUniverseSize(requestedUniverseSize);
        root = new VEBTree<>(requestedUniverseSize);
    }

    @Override
    public E put(Integer key, E value) {
        if (map.containsKey(key)) {
            return map.put(key, value);
        } else {
            map.put(key, value);
            root.treeInsert(key);
            return null;
        }
    }

    @Override
    public E get(Object key) {
        return map.get(key);    
    }

    @Override
    public E remove(Object key) {
        if (map.containsKey((Integer) key)) {
            E returnValue = map.remove(key);
            root.treeDelete((Integer) key);
            return returnValue;
        } else {
            return null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(
                "This " + getClass().getSimpleName() + 
                " does not implement 'containsValue'.");
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        root = new VEBTree<>(root.universeSize);
        map.clear();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends E> m) {
        for (Map.Entry<? extends Integer, ? extends E> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Integer> keySet() {
        return Collections.unmodifiableSet(map.keySet());
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
        if (map.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for minimum integer key in empty VanEmdeBoasTreeMap.");
        }

        return root.getMinimumKey();
    }
    
    public Integer getPredessor(Integer x) {
        if (map.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for predecessor integer key in empty VanEmdeBoasTreeMap.");
        }

        checkIntegerWithinUniverse(x, root.getUniverseSize());
        return root.getPredecessor(x);
    }

    public Integer getSuccessor(Integer x) {
        if (map.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for successor integer key in empty VanEmdeBoasTreeMap.");
        }

        checkIntegerWithinUniverse(x, root.getUniverseSize());
        return root.getSuccessor(x);
    }

    public Integer getMaximum() {
        if (map.isEmpty()) {
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
}
