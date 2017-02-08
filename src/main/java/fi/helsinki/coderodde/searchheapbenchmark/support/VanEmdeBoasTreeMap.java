package fi.helsinki.coderodde.searchheapbenchmark.support;

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
        int universeSize;
        
        /**
         * The minimum integer key in this tree.
         */
        Integer min;
        
        /**
         * The maximum integer key in this tree.
         */
        Integer max;
        
        /**
         * The summary vEB-tree.
         */
        VEBTree<E> summary;
        
        /**
         * The children vEB-trees of this tree.
         */
        VEBTree<E>[] cluster;
        
        VEBTree(int universeSize) {
            this.universeSize = universeSize;
            
            if (universeSize != MINIMUM_UNIVERSE_SIZE) {
                int upperUniverseSquare = upperSquare(universeSize);
                int lowerUniverseSquare = lowerSquare(universeSize);
                this.summary = new VEBTree<>(upperUniverseSquare);
                this.cluster = new VEBTree[upperUniverseSquare];
                
                for (int i = 0; i != upperUniverseSquare; ++i) {
                    this.cluster[i] = new VEBTree<>(lowerUniverseSquare);
                }
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
            
            return cluster[high(x, universeSize)]
                  .contains(low(x, universeSize));
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
            
            Integer maximumLow = cluster[high(x, universeSize)].getMaximumKey();
            
            if (maximumLow != null && low(x, universeSize) < maximumLow) {
                int offset = cluster[high(x, universeSize)]
                        .getSuccessor(low(x, universeSize));
                
                return index(high(x, universeSize), offset, universeSize);
            }
            
            Integer successorCluster = 
                    summary.getSuccessor(high(x, universeSize));
            
            if (successorCluster == null) {
                return null;
            }
            
            int offset = cluster[successorCluster].getMinimumKey();
            return index(successorCluster, offset, universeSize);
        }
    }
    
    /**
     * Caches the number of elements in this tree.
     */
    private int size;
    
    /**
     * The root tree.
     */
    private VEBTree<E> root;
    
    public VanEmdeBoasTreeMap(int requestedUniverseSize) {
        checkRequestedUniverseSize(requestedUniverseSize);
        requestedUniverseSize = fixUniverseSize(requestedUniverseSize);
        root = new VEBTree<>(requestedUniverseSize);
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
    
    private static int high(int x, int universeSize) {
        return x / lowerSquare(universeSize);
    }
    
    private static int low(int x, int universeSize) {
        return x % lowerSquare(universeSize);
    }
    
    private static int index(int x, int y, int universeSize) {
        return x * lowerSquare(universeSize) + y;
    }
    
    public static void main(String[] args) {
        for (int i = 1; i < 100; i *= 2) {
            System.out.println(i + " -> " + upperSquare(i) + " : " + lowerSquare(i));
        }
        
        VanEmdeBoasTreeMap<Integer> tree = new VanEmdeBoasTreeMap<>(4);
        
        System.out.println("yeah");
    }
}
