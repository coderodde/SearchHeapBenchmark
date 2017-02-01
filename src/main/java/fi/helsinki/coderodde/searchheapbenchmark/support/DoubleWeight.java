package fi.helsinki.coderodde.searchheapbenchmark.support;

import fi.helsinki.coderodde.searchheapbenchmark.Weight;

public class DoubleWeight implements Weight<Double> {

    @Override
    public Double zero() {
        return 0.0;
    }

    @Override
    public Double add(Double weight1, Double weight2) {
        return weight1 + weight2;
    }
}
