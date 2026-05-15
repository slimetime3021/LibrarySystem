
import org.derick.domain.DVD;
import org.derick.domain.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DVDTest {

    @BeforeEach
    void clearRegistry() {
        Item.getIdItem().clear();
    }

    // ── Constructor tests ─────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsAllFields() {
        DVD dvd = new DVD("0001", "Inception", Item.Status.IN_STORE, "Christopher Nolan", 148);

        assertEquals("0001", dvd.getId());
        assertEquals("Inception", dvd.getTitle());
        assertEquals(Item.Status.IN_STORE, dvd.getStatus());
        assertEquals("Christopher Nolan", dvd.getDirector());
        assertEquals(148, dvd.getMinutes());
    }

    @Test
    void constructor_withAutoId_setsAllFields() {
        DVD dvd = new DVD("The Matrix", Item.Status.BORROWED, "The Wachowskis", 136);

        assertNotNull(dvd.getId());
        assertEquals("The Matrix", dvd.getTitle());
        assertEquals(Item.Status.BORROWED, dvd.getStatus());
        assertEquals("The Wachowskis", dvd.getDirector());
        assertEquals(136, dvd.getMinutes());
    }

    @Test
    void constructor_registersInIdItemMap() {
        DVD dvd = new DVD("0005", "Interstellar", Item.Status.IN_STORE, "Christopher Nolan", 169);
        assertSame(dvd, Item.getIdItem().get("0005"));
    }

    // ── Setter tests ──────────────────────────────────────────────────────────

    @Test
    void setDirector_updatesDirector() {
        DVD dvd = new DVD("0010", "Film", Item.Status.IN_STORE, "Old Director", 90);
        dvd.setDirector("New Director");
        assertEquals("New Director", dvd.getDirector());
    }

    @Test
    void setMinutes_updatesMinutes() {
        DVD dvd = new DVD("0011", "Film", Item.Status.IN_STORE, "Director", 90);
        dvd.setMinutes(120);
        assertEquals(120, dvd.getMinutes());
    }

    // ── save() tests ──────────────────────────────────────────────────────────

    @Test
    void save_returnsCorrectCSVFormat() {
        DVD dvd = new DVD("0020", "Parasite", Item.Status.IN_STORE, "Bong Joon-ho", 132);
        assertEquals("DVD,0020,Parasite,IN_STORE,Bong Joon-ho,132\n", dvd.save());
    }

    @Test
    void save_withBorrowedStatus_includesStatus() {
        DVD dvd = new DVD("0021", "Dune", Item.Status.BORROWED, "Denis Villeneuve", 155);
        assertTrue(dvd.save().contains("BORROWED"));
    }

    // ── Comparator tests ──────────────────────────────────────────────────────

    @Test
    void directorComparator_sortsAlphabetically() {
        DVD a = new DVD("0030", "Film A", Item.Status.IN_STORE, "Anderson", 100);
        DVD b = new DVD("0031", "Film B", Item.Status.IN_STORE, "Zemeckis", 100);

        DVD.DirectorComparator cmp = new DVD.DirectorComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    @Test
    void minutesComparator_sortsByDuration() {
        DVD short_ = new DVD("0032", "Short", Item.Status.IN_STORE, "Dir", 80);
        DVD long_  = new DVD("0033", "Long",  Item.Status.IN_STORE, "Dir", 180);

        DVD.MinutesComparator cmp = new DVD.MinutesComparator();
        assertTrue(cmp.compare(short_, long_) < 0);
        assertTrue(cmp.compare(long_, short_) > 0);
        assertEquals(0, cmp.compare(short_, short_));
    }

    @Test
    void minutesComparator_sameDuration_returnsZero() {
        DVD a = new DVD("0034", "Film C", Item.Status.IN_STORE, "Dir A", 120);
        DVD b = new DVD("0035", "Film D", Item.Status.IN_STORE, "Dir B", 120);

        DVD.MinutesComparator cmp = new DVD.MinutesComparator();
        assertEquals(0, cmp.compare(a, b));
    }

    // ── Equality ──────────────────────────────────────────────────────────────

    @Test
    void equals_sameDVDsAreEqual() {
        DVD a = new DVD("0040", "Film", Item.Status.IN_STORE, "Director", 100);
        Item.getIdItem().remove("0040");
        DVD b = new DVD("0040", "Film", Item.Status.IN_STORE, "Director", 100);

        assertEquals(a, b);
    }

    @Test
    void equals_differentDirector_notEqual() {
        DVD a = new DVD("0041", "Film", Item.Status.IN_STORE, "Director A", 100);
        Item.getIdItem().remove("0041");
        DVD b = new DVD("0041", "Film", Item.Status.IN_STORE, "Director B", 100);

        assertNotEquals(a, b);
    }
}
