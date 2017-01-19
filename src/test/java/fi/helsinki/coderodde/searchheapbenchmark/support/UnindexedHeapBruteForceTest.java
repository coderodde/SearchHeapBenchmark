package fi.helsinki.coderodde.searchheapbenchmark.support;

import org.junit.Test;
import static org.junit.Assert.*;
import tests.UnindexedHeapCorrectnessTest;

public class UnindexedHeapBruteForceTest {
    
    @Test
    public void testUnindexedHeaps() {
        assertTrue(UnindexedHeapCorrectnessTest.test());
    }
}
