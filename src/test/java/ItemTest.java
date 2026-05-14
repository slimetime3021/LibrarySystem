
import org.derick.domain.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    // Concrete subclass for testing the abstract Item class
    private static class ConcreteItem extends Item {
        public ConcreteItem(String id, String title, Status status) {
            super(id, title, status);
        }

        public ConcreteItem(String title, Status status) {
            super(title, status);
        }

        @Override
        public String save() {
            return String.format("ConcreteItem,%s,%s,%s", id, title, status);
        }
    }

    @BeforeEach
    void clearRegistry() {
        // Clear the static idItem map before each test to avoid cross-test contamination
        Item.getIdItem().clear();
    }

    // ── Constructor tests ────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsFieldsCorrectly() {
        Item item = new ConcreteItem("0010", "Test Title", Item.Status.IN_STORE);

        assertEquals("0010", item.getId());
        assertEquals("Test Title", item.getTitle());
        assertEquals(Item.Status.IN_STORE, item.getStatus());
    }

    @Test
    void constructor_withExplicitId_registersInIdItemMap() {
        Item item = new ConcreteItem("0042", "Registered Title", Item.Status.IN_STORE);

        assertSame(item, Item.getIdItem().get("0042"));
    }

    @Test
    void constructor_withAutoId_assignsFormattedId() {
        Item.getIdItem().clear();
        Item item = new ConcreteItem("Auto Title", Item.Status.BORROWED);

        assertNotNull(item.getId());
        assertTrue(item.getId().matches("\\d{4}"), "ID should be zero-padded 4-digit string");
    }

    @Test
    void constructor_withExplicitId_advancesNextIdBeyondGivenId() {
        new ConcreteItem("0050", "High ID Item", Item.Status.IN_STORE);
        Item next = new ConcreteItem("Auto After High", Item.Status.IN_STORE);

        int nextIdValue = Integer.parseInt(next.getId());
        assertTrue(nextIdValue > 50, "Next auto-ID should be greater than 50");
    }

    // ── Setter tests ─────────────────────────────────────────────────────────

    @Test
    void setTitle_updatesTitle() {
        Item item = new ConcreteItem("0001", "Old Title", Item.Status.IN_STORE);
        item.setTitle("New Title");

        assertEquals("New Title", item.getTitle());
    }

    @Test
    void setStatus_updatesStatus() {
        Item item = new ConcreteItem("0002", "Some Item", Item.Status.IN_STORE);
        item.setStatus(Item.Status.BORROWED);

        assertEquals(Item.Status.BORROWED, item.getStatus());
    }

    // ── Status enum tests ────────────────────────────────────────────────────

    @Test
    void statusEnum_hasExpectedValues() {
        assertNotNull(Item.Status.valueOf("BORROWED"));
        assertNotNull(Item.Status.valueOf("IN_STORE"));
        assertNotNull(Item.Status.valueOf("LOST"));
    }

    // ── Comparator tests ─────────────────────────────────────────────────────

    @Test
    void idComparator_sortsCorrectly() {
        Item a = new ConcreteItem("0001", "Alpha", Item.Status.IN_STORE);
        Item b = new ConcreteItem("0002", "Beta", Item.Status.IN_STORE);

        Item.IdComparator cmp = new Item.IdComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    @Test
    void titleComparator_sortsAlphabetically() {
        Item a = new ConcreteItem("0003", "Apple", Item.Status.IN_STORE);
        Item b = new ConcreteItem("0004", "Zebra", Item.Status.IN_STORE);

        Item.TitleComparator cmp = new Item.TitleComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    @Test
    void statusComparator_sortsCorrectly() {
        Item borrowed  = new ConcreteItem("0005", "B", Item.Status.BORROWED);
        Item inStore   = new ConcreteItem("0006", "I", Item.Status.IN_STORE);

        Item.StatusComparator cmp = new Item.StatusComparator();
        // BORROWED < IN_STORE lexicographically
        assertTrue(cmp.compare(borrowed, inStore) < 0);
        assertTrue(cmp.compare(inStore, borrowed) > 0);
        assertEquals(0, cmp.compare(borrowed, borrowed));
    }

    // ── Equality / toString ───────────────────────────────────────────────────

    @Test
    void equals_sameFieldValues_returnsTrue() {
        Item a = new ConcreteItem("0007", "Same", Item.Status.LOST);
        // Reset registry so we can create a second item with the same id
        Item.getIdItem().clear();
        Item b = new ConcreteItem("0007", "Same", Item.Status.LOST);

        assertEquals(a, b);
    }

    @Test
    void equals_differentTitle_returnsFalse() {
        Item a = new ConcreteItem("0008", "Title A", Item.Status.IN_STORE);
        Item.getIdItem().remove("0008");
        Item b = new ConcreteItem("0008", "Title B", Item.Status.IN_STORE);

        assertNotEquals(a, b);
    }
}
