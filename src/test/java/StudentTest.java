
import org.derick.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private static final String VALID_ISBN = "9783161484100";

    @BeforeEach
    void clearStaticState() {
        Item.getIdItem().clear();
        User.getUsers().clear();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsAllFields() {
        Student student = new Student("0001", "Alice", new ArrayList<>());

        assertEquals("0001", student.getId());
        assertEquals("Alice", student.getName());
        assertTrue(student.getBorrowedItems().isEmpty());
    }

    @Test
    void constructor_withNameOnly_autoAssignsFormattedId() {
        Student student = new Student("Bob");

        assertNotNull(student.getId());
        assertTrue(student.getId().matches("\\d{4}"), "ID should be a zero-padded 4-digit string");
        assertEquals("Bob", student.getName());
        assertTrue(student.getBorrowedItems().isEmpty());
    }

    @Test
    void constructor_withExplicitId_advancesNextIdBeyondGivenId() {
        new Student("0050", "High ID Student", new ArrayList<>());
        Student next = new Student("Auto Student");

        assertTrue(Integer.parseInt(next.getId()) > 50);
    }

    @Test
    void constructor_withBorrowedItems_setsItemList() {
        Book book = new Book("0001", "Some Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Student student = new Student("0002", "Carol", new ArrayList<>(List.of(book)));

        assertEquals(1, student.getBorrowedItems().size());
        assertTrue(student.getBorrowedItems().contains(book));
    }

    // ── Borrowing limit ───────────────────────────────────────────────────────

    @Test
    void getBorrowingLimit_returns5() {
        Student student = new Student("0010", "Dave", new ArrayList<>());
        assertEquals(5, student.getBorrowingLimit());
    }

    @Test
    void getBorrowingLimit_isLessThanTeacherLimit() {
        Student student = new Student("0011", "Eve",  new ArrayList<>());
        Teacher teacher = new Teacher("0012", "Prof", new ArrayList<>());

        assertTrue(student.getBorrowingLimit() < teacher.getBorrowingLimit());
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    @Test
    void setName_updatesName() {
        Student student = new Student("0020", "Old Name", new ArrayList<>());
        student.setName("New Name");
        assertEquals("New Name", student.getName());
    }

    @Test
    void setBorrowedItems_updatesItemList() {
        Student student = new Student("0021", "Frank", new ArrayList<>());
        Book book = new Book("0003", "New Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");

        student.setBorrowedItems(new ArrayList<>(List.of(book)));

        assertEquals(1, student.getBorrowedItems().size());
        assertTrue(student.getBorrowedItems().contains(book));
    }

    // ── getBorrowedItemsId() ──────────────────────────────────────────────────

    @Test
    void getBorrowedItemsId_noItems_returnsEmptyString() {
        Student student = new Student("0030", "Grace", new ArrayList<>());
        assertEquals("", student.getBorrowedItemsId());
    }

    @Test
    void getBorrowedItemsId_oneItem_returnsItsId() {
        Book book = new Book("0004", "Solo Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Student student = new Student("0031", "Hank", new ArrayList<>(List.of(book)));

        assertTrue(student.getBorrowedItemsId().contains("0004"));
    }

    @Test
    void getBorrowedItemsId_multipleItems_containsAllIds() {
        Book book = new Book("0005", "Book",  Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        DVD dvd   = new DVD("0006",  "Film",  Item.Status.BORROWED, "Dir", 90);
        Student student = new Student("0032", "Ivy", new ArrayList<>(List.of(book, dvd)));

        String ids = student.getBorrowedItemsId();
        assertTrue(ids.contains("0005"));
        assertTrue(ids.contains("0006"));
    }

    // ── save() ────────────────────────────────────────────────────────────────

    @Test
    void save_noItems_returnsCorrectFormat() {
        Student student = new Student("0040", "Jack", new ArrayList<>());
        assertEquals("Student,0040,Jack,", student.save());
    }

    @Test
    void save_withBorrowedItem_includesItemId() {
        Book book = new Book("0007", "Saved Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Student student = new Student("0041", "Kate", new ArrayList<>(List.of(book)));

        String saved = student.save();
        assertTrue(saved.startsWith("Student,0041,Kate,"));
        assertTrue(saved.contains("0007"));
    }

    // ── equals() & hashCode() ────────────────────────────────────────────────

    @Test
    void equals_sameIdAndName_returnsTrue() {
        Student a = new Student("0050", "Leo", new ArrayList<>());
        Student b = new Student("0050", "Leo", new ArrayList<>());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentName_returnsFalse() {
        Student a = new Student("0051", "Leo",   new ArrayList<>());
        Student b = new Student("0051", "Leon",  new ArrayList<>());
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Student a = new Student("0052", "Mia", new ArrayList<>());
        Student b = new Student("0053", "Mia", new ArrayList<>());
        assertNotEquals(a, b);
    }

    @Test
    void equals_studentAndTeacherWithSameFields_returnsFalse() {
        Student student = new Student("0054", "Sam", new ArrayList<>());
        Teacher teacher = new Teacher("0054", "Sam", new ArrayList<>());
        assertNotEquals(student, teacher);
    }

    @Test
    void equals_null_returnsFalse() {
        Student student = new Student("0055", "Nina", new ArrayList<>());
        assertNotEquals(null, student);
    }

    // ── toString() ────────────────────────────────────────────────────────────

    @Test
    void toString_containsStudentIdAndName() {
        Student student = new Student("0060", "Omar", new ArrayList<>());
        String str = student.toString();
        assertTrue(str.contains("0060"));
        assertTrue(str.contains("Omar"));
    }
}
