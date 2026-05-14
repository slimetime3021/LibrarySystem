import org.derick.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private static final String VALID_ISBN = "9783161484100";

    @BeforeEach
    void clearStaticState() {
        Item.getIdItem().clear();
        User.getUsers().clear();
    }

    // ── Constructor tests ─────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsFields() {
        Admin admin = new Admin("0001", "Alice Admin", new ArrayList<>());

        assertEquals("0001", admin.getId());
        assertEquals("Alice Admin", admin.getName());
        assertTrue(admin.getBorrowedItems().isEmpty());
    }

    @Test
    void constructor_withName_autoAssignsId() {
        Admin admin = new Admin("Bob Admin");

        assertNotNull(admin.getId());
        assertEquals("Bob Admin", admin.getName());
    }

    // ── Borrowing limit ───────────────────────────────────────────────────────

    @Test
    void admin_inheritsDefaultBorrowingLimitOfZero() {
        // Admin does not override getBorrowingLimit(); it inherits User's default of 0
        Admin admin = new Admin("0010", "Carol", new ArrayList<>());
        assertEquals(0, admin.getBorrowingLimit());
    }

    // ── save() ────────────────────────────────────────────────────────────────

    @Test
    void save_returnsCorrectCSVFormat_noItems() {
        Admin admin = new Admin("0020", "Dave Admin", new ArrayList<>());
        String saved = admin.save();

        assertTrue(saved.startsWith("Admin,0020,Dave Admin,"));
    }

    @Test
    void save_includesBorrowedItemIds() {
        Book book = new Book("0001", "Title", Item.Status.BORROWED, VALID_ISBN, "Author", "Genre");
        Admin admin = new Admin("0021", "Eve Admin", new ArrayList<>(List.of(book)));

        assertTrue(admin.save().contains("0001"));
    }

    // ── reportItems() ─────────────────────────────────────────────────────────

    @Test
    void reportItems_printsAllCategories() {
        new Book("0002", "Book Title", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        new DVD("0003", "Film Title", Item.Status.BORROWED, "Dir", 90);
        new Magazine("0004", "Mag Title", Item.Status.LOST, 1, "Pub");

        Admin admin = new Admin("0030", "Reporter", new ArrayList<>());

        // Capture stdout
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        admin.reportItems();

        System.setOut(original);
        String output = out.toString();

        assertTrue(output.contains("In Store Items"),  "Should list In Store items");
        assertTrue(output.contains("Borrowed Items"),  "Should list Borrowed items");
        assertTrue(output.contains("Lost Items"),      "Should list Lost items");
    }

    @Test
    void reportItems_withNoItems_doesNotThrow() {
        Admin admin = new Admin("0031", "Empty Reporter", new ArrayList<>());

        // Suppress stdout
        PrintStream original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));

        assertDoesNotThrow(admin::reportItems);

        System.setOut(original);
    }

    @Test
    void reportItems_inStoreItemAppearsInCorrectCategory() {
        Book book = new Book("0005", "In Store Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");

        Admin admin = new Admin("0032", "Categorizer", new ArrayList<>());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        admin.reportItems();

        System.setOut(original);
        String output = out.toString();

        assertTrue(output.contains("In Store Book"),
                "In-store book title should appear in the output");
    }

    // ── Equality ──────────────────────────────────────────────────────────────

    @Test
    void equals_sameAdminsAreEqual() {
        Admin a = new Admin("0040", "Fred", new ArrayList<>());
        Admin b = new Admin("0040", "Fred", new ArrayList<>());
        assertEquals(a, b);
    }

    @Test
    void equals_differentName_notEqual() {
        Admin a = new Admin("0041", "Fred",    new ArrayList<>());
        Admin b = new Admin("0041", "Freddie", new ArrayList<>());
        assertNotEquals(a, b);
    }

    @Test
    void equals_adminAndStudentWithSameId_notEqual() {
        Admin   admin   = new Admin("0042",   "Same", new ArrayList<>());
        Student student = new Student("0042", "Same", new ArrayList<>());
        assertNotEquals(admin, student);
    }
}
