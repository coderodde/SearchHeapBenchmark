package fi.helsinki.coderodde.searchheapbenchmark;

public interface Weight<W extends Comparable<? super W>> {

    /**
     * Returns the weight representing zero.
     * 
     * @return the zero weight. 
     */
    public W zero();
    
    /**
     * Returns the sum of the two input weights.
     * 
     * @param weight1 the first weight.
     * @param weight2 the second weight.
     * @return the sum or the two input weights.
     */
    public W add(W weight1, W weight2);
}
