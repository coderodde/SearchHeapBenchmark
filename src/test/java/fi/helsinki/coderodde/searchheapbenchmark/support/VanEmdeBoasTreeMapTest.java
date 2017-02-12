package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.NoSuchElementException;
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
    }

    @Test
    public void testContains() {
        assertFalse(map.contains(2));
        assertFalse(map.contains(6));
        
        map.insert(6);
        
        assertFalse(map.contains(2));
        assertTrue(map.contains(6));
        
        map.insert(2);
        
        assertTrue(map.contains(2));
        assertTrue(map.contains(6));
        
        map.delete(6);
        
        assertTrue(map.contains(2));
        assertFalse(map.contains(6));
        
        map.delete(2);
        
        assertFalse(map.contains(2));
        assertFalse(map.contains(6));
    }

    @Test
    public void testGetMaximum() {
        for (int i = 0; i < 8; ++i) {
            map.insert(i);
            assertEquals(Integer.valueOf(i), map.getMaximum());
        }
        
        for (int i = 7; i >= 0; --i) {
            assertEquals(Integer.valueOf(i), map.getMaximum());
            map.delete(i);
        }
    }

    @Test
    public void testGetMinimum() {
        for (int i = 7; i >= 0; --i) {
            map.insert(i);
            assertEquals(Integer.valueOf(i), map.getMinimum());
        }
        
        for (int i = 0; i < 8; ++i) {
            assertEquals(Integer.valueOf(i), map.getMinimum());
            map.delete(i);
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
        map.insert(3);
        assertNull(map.getPredessor(3));
        
        map.insert(5);
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
        map.insert(5);
        assertNull(map.getSuccessor(5));
        
        map.insert(3);
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
    
    }
}
