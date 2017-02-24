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
            assertEquals(Integer.valueOf(i), map.getMaximum());
        }
        
        for (int i = 7; i >= 0; --i) {
            assertEquals(Integer.valueOf(i), map.getMaximum());
            map.remove(i);
        }
    }

    @Test
    public void testGetMinimum() {
        for (int i = 7; i >= 0; --i) {
            map.put(i, 3 * i);
            assertEquals(Integer.valueOf(i), map.getMinimum());
        }
        
        for (int i = 0; i < 8; ++i) {
            assertEquals(Integer.valueOf(i), map.getMinimum());
            map.remove(i);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMinimumThrowsOnEmptyMap() {
        map.getMinimum();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMaximumThrowsOnEmptyMap() {
        map.getMaximum();
    }
    
    @Test
    public void testGetPredessor() {
        map.put(3, 9);
        assertNull(map.getPredessor(3));
        
        map.put(5, 15);
        assertEquals(Integer.valueOf(3), map.getPredessor(4));
        assertEquals(Integer.valueOf(3), map.getPredessor(5));
        assertEquals(Integer.valueOf(5), map.getPredessor(6));
        assertEquals(Integer.valueOf(5), map.getPredessor(7));
        
        assertNull(map.getPredessor(3));
        assertNull(map.getPredessor(2));
        assertNull(map.getPredessor(1));
        assertNull(map.getPredessor(0));
    }
    
    @Test
    public void testGetSuccessor() {
        map.put(5, 15);
        assertNull(map.getSuccessor(5));
        
        map.put(3, 9);
        assertEquals(Integer.valueOf(5), map.getSuccessor(4));
        assertEquals(Integer.valueOf(5), map.getSuccessor(3));
        assertEquals(Integer.valueOf(3), map.getSuccessor(2));
        assertEquals(Integer.valueOf(3), map.getSuccessor(1));
        
        assertNull(map.getSuccessor(5));
        assertNull(map.getSuccessor(6));
        assertNull(map.getSuccessor(7));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testGetSuccessorThrowsOnEmptyMap() {
        map.getSuccessor(1);
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testGetPredecessorThrowsOnEmptyMap() {
        map.getPredessor(1);
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
        
        assertEquals(Integer.valueOf(1), map.getMinimum());
        assertEquals(Integer.valueOf(4), map.getMaximum());
        
        map.remove(2);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertTrue(map.containsKey(4));
        assertEquals(3, map.size());
        
        assertEquals(Integer.valueOf(1), map.getMinimum());
        assertEquals(Integer.valueOf(4), map.getMaximum());
        
        map.remove(4);
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        assertEquals(Integer.valueOf(1), map.getMinimum());
        assertEquals(Integer.valueOf(3), map.getMaximum());
        
        map.remove(2); // Cannot remove twice.
        
        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(2, map.size());
        
        assertEquals(Integer.valueOf(1), map.getMinimum());
        assertEquals(Integer.valueOf(3), map.getMaximum());
        
        map.remove(1);
        
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertTrue(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertEquals(1, map.size());
        
        assertEquals(Integer.valueOf(3), map.getMinimum());
        assertEquals(Integer.valueOf(3), map.getMaximum());
        
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
        System.out.println("yeah");
        assertEquals(treeMap.size(), vanMap.size());
    }
}
