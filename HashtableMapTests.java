// == CS400 Fall 2024 File Header Information ==
// Name: Holland Hargens
// Email: hhargens@wisc.edu
// Group: <your group's id: P2.1814>
// Lecturer: Gary Dahl
// Notes to Grader:
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class HashtableMapTests {
    /**
     * Tests basic put and get operations
     */
    @Test
    public void testPutAndGet() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("test", 42);
        assertEquals(42, map.get("test"));
        assertEquals(1, map.getSize());
    }

    /**
     * Tests handling of nulls
     */
    @Test
    public void testNullKey() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        assertThrows(NullPointerException.class, () -> map.put(null, 42));
    }

    /**
     * Tests removing elements and size updates
     */
    @Test
    public void testRemoveAndSize() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(2, map.getSize());
        assertEquals(1, map.remove("one"));
        assertEquals(1, map.getSize());
    }

    /**
     * Tests clear operation
     */
    @Test
    public void testClear() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.clear();
        assertEquals(0, map.getSize());
        assertThrows(NoSuchElementException.class, () -> map.get("one"));
    }

    /**
     * Tests capacity behavior and boundary conditions
     */
    @Test
    public void testCapacityAndBoundary() {
        // Test with very small initial capacity
        HashtableMap<Integer, String> map = new HashtableMap<>(1);
        assertEquals(1, map.getCapacity());
        assertEquals(0, map.getSize());

        // Fill up the map
        map.put(42, "forty-two");
        assertEquals(1, map.getSize());
        assertTrue(map.containsKey(42));

        // Test boundary between empty and non-empty states
        map.remove(42);
        assertEquals(0, map.getSize());
        assertFalse(map.containsKey(42));

        // Test adding after removing
        map.put(10, "ten");
        assertEquals(1, map.getSize());
        assertEquals("ten", map.get(10));

        // Test that removed keys are really gone
        assertThrows(NoSuchElementException.class, () -> map.get(42));
    }
}

