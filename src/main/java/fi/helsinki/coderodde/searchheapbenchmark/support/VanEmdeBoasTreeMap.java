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
        
        Integer getMinimumKey() {
            return min;
        }
        
        Integer getMaximumKey() {
            return max;
        }
        
        boolean contains(Integer x) {
            if (x.equals(min) || x.equals(max)) {
                return true;
            }
            
            if (universeSize == 2) {
                return false;
            }
            
            return cluster[high(x)].contains(low(x));
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
        
        void emptyTreeInsert(Integer x) {
            min = x;
            max = x;
        }
        
        void treeInsert(Integer x) {
            if (min == null) {
                emptyTreeInsert(x);
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
                    min = 0;
                }
                
                max = min;
                return; // This looks suspicious.
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
                        max = index(summaryMaximum,
                                    cluster[summaryMaximum].getMaximumKey());
                    }
                }
            } else if (x.equals(max)) {
                max = index(high(x), cluster[high(x)].getMaximumKey());
            }
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
    
    public void insert(Integer x) {
        if (set.contains(x)) {
            return;
        }
        
        set.add(x);
        root.treeInsert(x);
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
        
        return root.getPredecessor(x);
    }
    
    public Integer getSuccessor(Integer x) {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for successor integer key in empty VanEmdeBoasTreeMap.");
        }
        
        return root.getSuccessor(x);
    }
    
    public Integer getMaximum() {
        if (set.isEmpty()) {
            throw new NoSuchElementException(
            "Asking for maximum integer key in empty VanEmdeBoasTreeMap.");
        }
        
        return root.getMaximumKey();
    }
    
    public void delete(Integer x) {
        if (!set.contains(x)) {
            return;
        }
        
        set.remove(x);
        root.treeDelete(x);
    }
    
    public int size() {
        return set.size();
    }
    
    public void clear() {
        root = new VEBTree<>(root.universeSize);
        set.clear();
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
                     (requestedUniverseSize << 1);
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
        for (int i = 1; i < 100; i *= 2) {
            System.out.println(i + " -> " + upperSquare(i) + " : " + lowerSquare(i));
        }
        
        VanEmdeBoasTreeMap<Integer> tree = new VanEmdeBoasTreeMap<>(4);
        
        System.out.println("yeah");
    }
}
