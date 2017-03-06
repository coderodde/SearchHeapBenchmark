package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BTreeMapTest {

    private BTreeMap<Integer, Integer> map = new BTreeMap<>(3);
    
    @Before
    public void setUp() {
        map.clear();
    }
    
    public void rootTest() {
        BTreeMap<Integer, Integer> map2 = map.buildDebugTree();
        map2.remove(9);
        System.out.println("removed 9");
        map2.remove(10);
        System.out.println("removed 10");
        map2.remove(11);
        System.out.println("removed 11");
    }

    @Test
    public void debug() {
        map = new BTreeMap<>(2);
        map.put(2, 2);
        map.put(1, 1);
        map.put(3, 3);
        map.remove(2);
        map.remove(3);
        map.remove(1);
    }
    
    @Test
    public void testInsert() {
        // Insert 0, 2, 4, 6.
        for (int i = 0; i < 8; i += 2) {
            map.put(i, 2 * i);
        }
        
        assertEquals(4, map.size());
        map.put(2, -2); // B tree does not allow duplicates.
        assertEquals(4, map.size());

        for (int i = 0; i < 8; i += 2) {
            assertTrue(map.containsKey(i));
        }
        
        for (int i = 1; i < 8; i += 2) {
            assertFalse(map.containsKey(i));
        }
    }

    @Test
    public void testContains() {
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(6));
        assertNull(map.get(2));
        assertNull(map.get(6));
        assertNull(map.get(5));
        
        map.put(6, 12);
        
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(6));
        assertEquals(Integer.valueOf(12), map.get(6));
        
        map.put(2, 11);
        
        assertTrue(map.containsKey(2));
        assertTrue(map.containsKey(6));
        assertEquals(Integer.valueOf(12), map.get(6));
        assertEquals(Integer.valueOf(11), map.get(2));
        
        map.put(6, 9);
        assertEquals(Integer.valueOf(9), map.get(6));
        
        map.remove(6);
        
        assertTrue(map.containsKey(2));
        assertFalse(map.containsKey(6));
        
        map.remove(2);
        
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(6));
    }
    
    @Test
    public void testDelete() {
        int sz = 0;
        
        for (int i : new int[] { 2, 4, 1, 3 }) {
            assertEquals(sz, map.size());
            map.put(i, 2 * i);
            assertEquals(++sz, map.size());
        }
        
        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        
        map.remove(2);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        assertEquals(3, map.size());
        
        map.remove(4);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        map.remove(2); // Cannot remove twice.
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        map.remove(1);
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(1, map.size());
        
        map.remove(3);
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(0, map.size());
    }
    
    @Test
    public void testDeleteNodeWithTwoChildren() {
        map.put(2, 4);
        map.put(1, 2);
        map.put(3, 6);
        assertEquals(Integer.valueOf(4), map.remove(2));
        assertEquals(Integer.valueOf(6), map.remove(3));
    }
    
    @Test
    public void debugTest() {
        BTreeMap<Integer, Integer> map = new BTreeMap<>(2);
        map.put(1, 1);
        map.put(2, 2);
        map.put(0, 0);
        map.put(-1, -1);
        
        map.remove(-1);
        map.remove(1);
        assertEquals(Integer.valueOf(0), map.getMinimumKey());
    }
    
    @Test
    public void smallDebugTest() {
        System.out.println("Find bad remove!");
        BTreeMap<Integer, Integer> m = new BTreeMap<>(2);
        
        while (true) {
            long seed = 1488799272037L; //System.currentTimeMillis();
            Random random = new Random(seed);
            m.clear();
            
            for (int i = 0; i < 10; ++i) {
                int key = random.nextInt(30);
                m.put(key, key);
            }
            
            int i = 0;
            
            while (true) {
                int removeKey = random.nextInt(30);
                
                try {
                    boolean healthy = m.isHealty();
                    if (m.remove(removeKey) != null) {
                        System.out.println(++i + ": " + healthy);
                    }
                    
                    if (!healthy) {
                        System.out.println("unhealthy");
                        return;
                    }
                    
                    if (m.isEmpty()) {
                        System.out.println("PASS!");
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    System.err.println("Seed = " + seed + ", remove key: " + removeKey);
                    return;
                }
            }
        }
    }
    
//    @Test
    public void bruteForceTest() {
//        final int UNIVERSE_SIZE = 50_000;
//        final int ITERATIONS = 100_000;
        final int UNIVERSE_SIZE = 5_000;
        final int ITERATIONS = 10_000;
        
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        Map<Integer, Integer> treeMap = new TreeMap<>();
        BTreeMap<Integer, Integer> map = new BTreeMap<>(5);
        
        System.out.println("Seed = " + seed);
        
        for (int i = 0; i < ITERATIONS; ++i) {
            float coin = random.nextFloat();
            
            if (coin < 0.45f) {
                int newElement = random.nextInt(UNIVERSE_SIZE);
                treeMap.put(newElement, 3 * newElement);
                map.put(newElement, 3 * newElement);
            } else if (coin < 0.55f) {
                int key = random.nextInt(UNIVERSE_SIZE);
                assertEquals(treeMap.get(key), map.get(key));
            } else {
                if (treeMap.isEmpty()) {
                    assertTrue(map.isEmpty());
                } else {
                    int key = random.nextInt(UNIVERSE_SIZE);
                    assertEquals(treeMap.remove(key), map.remove(key));
                }
            }
        }
        
        assertEquals(treeMap.size(), map.size());
    }
}
