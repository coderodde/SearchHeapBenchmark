package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AVLTreeMapTest {

    private final AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
    
    @Before
    public void setUp() {
        map.clear();
    }

    @Test
    public void testInsert() {
        // Insert 0, 2, 4, 6.
        for (int i = 0; i < 8; i += 2) {
            map.put(i, 2 * i);
        }
        
        assertEquals(4, map.size());
        map.put(2, -2); // vEB tree does not allow duplicates.
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
    public void bruteForceTest() {
        final int UNIVERSE_SIZE = 1000;
        final int ITERATIONS = 20_000;
        
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        Map<Integer, Integer> treeMap = new TreeMap<>();
        AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
        
        System.out.println("Seed = " + seed);
        
        for (int i = 0; i < ITERATIONS; ++i) {
            float coin = random.nextFloat();
            
            if (coin < 0.45f) {
                int newElement = random.nextInt(UNIVERSE_SIZE);
                treeMap.put(newElement, newElement * 3);
                map.put(newElement, newElement * 3);
            } else if (coin < 0.65f) {
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
