
import org.derick.domain.Item;
import org.derick.domain.Magazine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagazineTest {

    @BeforeEach
    void clearRegistry() {
        Item.getIdItem().clear();
    }

    // ── Constructor tests ─────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsAllFields() {
        Magazine mag = new Magazine("0001", "National Geographic", Item.Status.IN_STORE, 42, "Nat Geo Society");

        assertEquals("0001", mag.getId());
        assertEquals("National Geographic", mag.getTitle());
        assertEquals(Item.Status.IN_STORE, mag.getStatus());
        assertEquals(42, mag.getIssueNumber());
        assertEquals("Nat Geo Society", mag.getPublisher());
    }

    @Test
    void constructor_withAutoId_setsAllFields() {
        Magazine mag = new Magazine("Time", Item.Status.BORROWED, 101, "Time USA");

        assertNotNull(mag.getId());
        assertEquals("Time", mag.getTitle());
        assertEquals(101, mag.getIssueNumber());
        assertEquals("Time USA", mag.getPublisher());
    }

    @Test
    void constructor_registersInIdItemMap() {
        Magazine mag = new Magazine("0010", "Wired", Item.Status.IN_STORE, 5, "Condé Nast");
        assertSame(mag, Item.getIdItem().get("0010"));
    }

    // ── Setter tests ──────────────────────────────────────────────────────────

    @Test
    void setIssueNumber_updatesIssueNumber() {
        Magazine mag = new Magazine("0020", "Forbes", Item.Status.IN_STORE, 1, "Forbes Media");
        mag.setIssueNumber(99);
        assertEquals(99, mag.getIssueNumber());
    }

    @Test
    void setPublisher_updatesPublisher() {
        Magazine mag = new Magazine("0021", "Vogue", Item.Status.IN_STORE, 12, "Old Publisher");
        mag.setPublisher("Condé Nast");
        assertEquals("Condé Nast", mag.getPublisher());
    }

    // ── save() tests ──────────────────────────────────────────────────────────

    @Test
    void save_returnsCorrectCSVFormat() {
        Magazine mag = new Magazine("0030", "Popular Science", Item.Status.IN_STORE, 7, "Bonnier Corp");
        assertEquals("Magazine,0030,Popular Science,IN_STORE,7,Bonnier Corp\n", mag.save());
    }

    @Test
    void save_withLostStatus_includesStatus() {
        Magazine mag = new Magazine("0031", "Lost Mag", Item.Status.LOST, 3, "Publisher");
        assertTrue(mag.save().contains("LOST"));
    }

    // ── Comparator tests ──────────────────────────────────────────────────────

    @Test
    void issueNumberComparator_sortsByIssueNumberAscending() {
        Magazine low  = new Magazine("0040", "Mag A", Item.Status.IN_STORE, 1,  "Pub");
        Magazine high = new Magazine("0041", "Mag B", Item.Status.IN_STORE, 99, "Pub");

        Magazine.IssueNumberComparator cmp = new Magazine.IssueNumberComparator();
        assertTrue(cmp.compare(low, high) < 0);
        assertTrue(cmp.compare(high, low) > 0);
        assertEquals(0, cmp.compare(low, low));
    }

    @Test
    void issueNumberComparator_sameIssueNumber_returnsZero() {
        Magazine a = new Magazine("0042", "Mag C", Item.Status.IN_STORE, 5, "Pub A");
        Magazine b = new Magazine("0043", "Mag D", Item.Status.IN_STORE, 5, "Pub B");

        Magazine.IssueNumberComparator cmp = new Magazine.IssueNumberComparator();
        assertEquals(0, cmp.compare(a, b));
    }

    @Test
    void publisherComparator_sortsAlphabetically() {
        Magazine a = new Magazine("0050", "Mag E", Item.Status.IN_STORE, 1, "Aardvark Press");
        Magazine b = new Magazine("0051", "Mag F", Item.Status.IN_STORE, 1, "Zenith Media");

        Magazine.PublisherComparator cmp = new Magazine.PublisherComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    // ── Equality ──────────────────────────────────────────────────────────────

    @Test
    void equals_sameMagazinesAreEqual() {
        Magazine a = new Magazine("0060", "Mag", Item.Status.IN_STORE, 3, "Pub");
        Item.getIdItem().remove("0060");
        Magazine b = new Magazine("0060", "Mag", Item.Status.IN_STORE, 3, "Pub");

        assertEquals(a, b);
    }

    @Test
    void equals_differentIssueNumber_notEqual() {
        Magazine a = new Magazine("0061", "Mag", Item.Status.IN_STORE, 1, "Pub");
        Item.getIdItem().remove("0061");
        Magazine b = new Magazine("0061", "Mag", Item.Status.IN_STORE, 2, "Pub");

        assertNotEquals(a, b);
    }
}
