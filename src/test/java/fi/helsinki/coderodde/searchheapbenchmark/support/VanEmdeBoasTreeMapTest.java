package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;
import org.junit.Before;
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
    public void testGetMaximum() {
        for (int i = 0; i < 8; ++i) {
            map.put(i, 3 * i);
            assertEquals(Integer.valueOf(3 * i), map.get(i));
            assertEquals(i, map.getMaximumKey());
        }
        
        for (int i = 7; i >= 0; --i) {
            assertEquals(i, map.getMaximumKey());
            map.remove(i);
        }
    }

    @Test
    public void testGetMinimum() {
        for (int i = 7; i >= 0; --i) {
            map.put(i, 3 * i);
            assertEquals(i, map.getMinimumKey());
        }
        
        for (int i = 0; i < 8; ++i) {
            assertEquals(i, map.getMinimumKey());
            map.remove(i);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMinimumThrowsOnEmptyMap() {
        map.getMinimumKey();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMaximumThrowsOnEmptyMap() {
        map.getMaximumKey();
    }
    
    @Test
    public void testGetPredessor() {
        map.put(3, 9);
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getPredessorKey(3));
        
        map.put(5, 15);
        assertEquals(3, map.getPredessorKey(4));
        assertEquals(3, map.getPredessorKey(5));
        assertEquals(5, map.getPredessorKey(6));
        assertEquals(5, map.getPredessorKey(7));
        
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getPredessorKey(3));
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getPredessorKey(2));
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getPredessorKey(1));
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getPredessorKey(0));
    }
    
    @Test
    public void testGetSuccessor() {
        map.put(5, 15);
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getSuccessorKey(5));
        
        map.put(3, 9);
        assertEquals(5, map.getSuccessorKey(4));
        assertEquals(5, map.getSuccessorKey(3));
        assertEquals(3, map.getSuccessorKey(2));
        assertEquals(3, map.getSuccessorKey(1));
        
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getSuccessorKey(5));
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getSuccessorKey(6));
        assertEquals(VanEmdeBoasTreeMap.NIL, map.getSuccessorKey(7));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testGetSuccessorThrowsOnEmptyMap() {
        map.getSuccessorKey(1);
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testGetPredecessorThrowsOnEmptyMap() {
        map.getPredessorKey(1);
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
        
        assertEquals(1, map.getMinimumKey());
        assertEquals(4, map.getMaximumKey());
        
        map.remove(2);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        assertEquals(3, map.size());
        
        assertEquals(1, map.getMinimumKey());
        assertEquals(4, map.getMaximumKey());
        
        map.remove(4);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        assertEquals(1, map.getMinimumKey());
        assertEquals(3, map.getMaximumKey());
        
        map.remove(2); // Cannot remove twice.
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        assertEquals(1, map.getMinimumKey());
        assertEquals(3, map.getMaximumKey());
        
        map.remove(1);
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(1, map.size());
        
        assertEquals(3, map.getMinimumKey());
        assertEquals(3, map.getMaximumKey());
        
        map.remove(3);
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(0, map.size());
    }
    
    @Test
    public void bruteForceTest() {
        final int UNIVERSE_SIZE = 32;
        final int ITERATIONS = 100000;
        
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        Map<Integer, Integer> treeMap = new TreeMap<>();
        VanEmdeBoasTreeMap<Integer> vanMap = 
                new VanEmdeBoasTreeMap<>(UNIVERSE_SIZE);
        
        System.out.println("Seed = " + seed);
        
        for (int i = 0; i < ITERATIONS; ++i) {
            float coin = random.nextFloat();
            
            if (coin < 0.3f) {
                int newElement = random.nextInt(UNIVERSE_SIZE);
                treeMap.put(newElement, newElement * 3);
                vanMap.put(newElement, newElement * 3);
            } else if (coin < 0.6f) {
                int key = random.nextInt(UNIVERSE_SIZE);
                assertEquals(treeMap.get(key), vanMap.get(key));
            } else {
                if (treeMap.isEmpty()) {
                    assertTrue(vanMap.isEmpty());
                } else {
                    int key = random.nextInt(UNIVERSE_SIZE);
                    assertEquals(treeMap.remove(key), vanMap.remove(key));
                }
            }
        }
        
        assertEquals(treeMap.size(), vanMap.size());
    }
}
