package fi.helsinki.coderodde.searchheapbenchmark.support;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class VanEmdeBoasTreeMapTest {

    private final VanEmdeBoasTreeMap<Integer> map = 
            new VanEmdeBoasTreeMap(8);
    @Before
    public void setUp() {
        map.clear();
    }

    @Test
    public void testInsert() {
        for (int i = 0; i < 8; i += 2) {
            map.insert(i);
        }

        for (int i = 0; i < 8; i += 2) {
            assertTrue(map.contains(i));
        }
        
        for (int i = 1; i < 8; i += 2) {
            assertFalse(map.contains(i));
        }
//        for (int i = 0; i < 10; i += 2) {
//            map.insert(i);
//        }
//        
//        for (int i = 0; i < 10; i += 2) {
//            assertTrue(map.contains(i));
//        }
//        
//        for (int i = 1; i < 10; i += 2) {
//            assertFalse(map.contains(i));
//        }
    }

    @Test
    public void testContains() {
        
    }

    @Test
    public void testGetMinimum() {
        for (int i = 0; i < 8; ++i) {
            map.insert(i);
        }
        
        for (int i = 0; i < 8; ++i) {
            assertEquals(Integer.valueOf(i), map.getMinimum());
            map.delete(i);
        }
        
//        for (int i = 1000; i < 2000; ++i) {
//            map.insert(i);
//        }
//        
//        for (int i = 1000; i < 2000; ++i) {
//            System.out.println(i);
//            assertEquals(Integer.valueOf(i), map.getMinimum());
//            map.delete(i);
//        }
    }

    @Test
    public void testGetPredessor() {
    
    }

    @Test
    public void testGetSuccessor() {
    
    }

    @Test
    public void testGetMaximum() {
    
    }

    @Test
    public void testDelete() {
    
    }
}
