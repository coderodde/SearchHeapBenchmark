package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.Weight;

public class IntegerWeight implements Weight<Integer> {

    @Override
    public Integer zero() {
        return 0;
    }

    @Override
    public Integer add(Integer weight1, Integer weight2) {
        return weight1 + weight2;
    }
}
