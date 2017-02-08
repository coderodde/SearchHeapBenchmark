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
            
            Integer minimumLow = cluster[high(x, universeSize)].getMinimumKey();
            
            if (minimumLow != null && low(x, universeSize) > minimumLow) {
                int offset = cluster[high(x, universeSize)]
                      .getPredecessor(low(x, universeSize));
                return index(high(x, universeSize), offset, universeSize);
            }
            
            Integer predecessorCluster = 
                    summary.getPredecessor(high(x, universeSize));
            
            if (predecessorCluster == null) {
                if (min != null && x > min) {
                    return min;
                }
                
                return null;
            }
            
            int offset = cluster[predecessorCluster].getMaximumKey();
            return index(predecessorCluster, offset, universeSize);
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
                Integer minimum = cluster[high(x, universeSize)]
                        .getMinimumKey();
                
                if (minimum == null) {
                    summary.treeInsert(high(x, universeSize));
                    cluster[high(x, universeSize)]
                            .emptyTreeInsert(low(x, universeSize));
                } else {
                    cluster[high(x, universeSize)]
                            .treeInsert(low(x, universeSize));
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
                x = index(firstCluster,
                          cluster[firstCluster].getMinimumKey(), universeSize);
                min = x;
            } 
            
            cluster[high(x, universeSize)].treeDelete(low(x, universeSize));
            
            if (cluster[high(x, universeSize)].getMinimumKey() == null) {
                summary.treeDelete(high(x, universeSize));
                
                if (x.equals(max)) {
                    Integer summaryMaximum = summary.getMaximumKey();
                    
                    if (summaryMaximum == null) {
                        max = min;
                    } else {
                        max = index(summaryMaximum,
                                    cluster[summaryMaximum].getMaximumKey(),
                                    universeSize);
                    }
                }
            } else if (x.equals(max)) {
                max = index(high(x, universeSize), 
                            cluster[high(x, universeSize)].getMaximumKey(), 
                            universeSize);
            }
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
    
    public void insert(Integer x) {
        root.treeInsert(x);
    }
    
    public boolean contains(Integer x) {
        return root.contains(x);
    }
    
    public Integer getMinimum() {
        return root.getMinimumKey();
    }
    
    public Integer getPredessor(Integer x) {
        return root.getPredecessor(x);
    }
    
    public Integer getSuccessor(Integer x) {
        return root.getSuccessor(x);
    }
    
    public Integer getMaximum() {
        return root.getMaximumKey();
    }
    
    public void delete(Integer x) {
        root.treeDelete(x);
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
