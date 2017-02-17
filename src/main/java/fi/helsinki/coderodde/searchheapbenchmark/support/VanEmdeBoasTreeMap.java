package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class VanEmdeBoasTreeMap<E> {
    
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
         * The lower square of the universe size.
         */
        private final int universeSizeLowerSquare;
        
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
            this.universeSizeLowerSquare = lowerSquare(universeSize);
            
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
                minValue = cluster[firstCluster].get(x);
            } else {
                returnValue = cluster[high(x)].treeDelete(low(x));
            }
            
            /*E returnValue =*/ cluster[high(x)].treeDelete(low(x));
            
            if (cluster[high(x)].getMinimumKey() == null) {
                summary.treeDelete(high(x));
                
                if (x.equals(max)) {
                    Integer summaryMaximum = summary.getMaximumKey();
                    
                    if (summaryMaximum == null) {
                        max = min;
                        //maxValue = minValue;
                    } else {
                        max = index(summaryMaximum,
                                    cluster[summaryMaximum].getMaximumKey());
                    }
                }
            } else if (x.equals(max)) {
                max = index(high(x), cluster[high(x)].getMaximumKey());
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
            return x / universeSizeLowerSquare;
        }

        private int low(int x) {
            return x % universeSizeLowerSquare;
        }

        private int index(int x, int y) {
            return x * universeSizeLowerSquare + y;
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
    
    public void insert(Integer x, E value) {
        if (set.contains(x)) {
            root.treeUpdate(x, value);
        } else {
            set.add(x);
            root.treeInsert(x, value);
        }
    }
    
    public boolean contains(Integer x) {
        return set.contains(x);
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
    
    public E get(Integer x) {
        if (!set.contains(x)) {
            return null;
        }
        
        return root.get(x);
    }
    
    public E delete(Integer x) {
        checkIntegerWithinUniverse(x, root.getUniverseSize());
        
        if (!set.contains(x)) {
            return null;
        }
        
        set.remove(x);
        return root.treeDelete(x);
    }
    
    public int size() {
        return set.size();
    }
    
    public void clear() {
        root = new VEBTree<>(root.universeSize);
        set.clear();
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
        VanEmdeBoasTreeMap<Integer> t = new VanEmdeBoasTreeMap<>(4);
        t.insert(1, 11);
        t.insert(0, 10);
        t.insert(2, 12);
        t.insert(3, 13);
        
//        System.out.println(t.delete(3));
//        System.out.println(t.delete(2));
        System.out.println(t.delete(0));
        System.out.println(t.delete(1));
//        System.out.println(t.delete(3)); // fails
//        System.out.println(t.delete(0));
        
        System.exit(0);
        
        /*for (int i = 1; i < 100; i *= 2) {
            System.out.println(i + " -> " + upperSquare(i) + " : " + lowerSquare(i));
        }
        
        VanEmdeBoasTreeMap<Integer> tree = new VanEmdeBoasTreeMap<>(4);
        
        System.out.println("yeah");*/
        VanEmdeBoasTreeMap<Integer> tree = new VanEmdeBoasTreeMap<>(9);
        tree.insert(4, null);
        tree.insert(3, null);
        tree.insert(2, null);
        tree.insert(5, null);
        tree.insert(15, null);
        tree.insert(14, null);
        tree.insert(7, null);
//        System.out.println("");

        tree = new VanEmdeBoasTreeMap<>(4);
        tree.insert(2, 12);
        tree.insert(2, 11);
        System.out.println(tree.delete(2));
    }
}
