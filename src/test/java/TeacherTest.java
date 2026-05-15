
import org.derick.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    private static final String VALID_ISBN = "9783161484100";

    @BeforeEach
    void clearStaticState() {
        Item.getIdItem().clear();
        User.getUsers().clear();
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    @Test
    void constructor_withExplicitId_setsAllFields() {
        Teacher teacher = new Teacher("0001", "Prof. Smith", new ArrayList<>());

        assertEquals("0001", teacher.getId());
        assertEquals("Prof. Smith", teacher.getName());
        assertTrue(teacher.getBorrowedItems().isEmpty());
    }

    @Test
    void constructor_withNameOnly_autoAssignsFormattedId() {
        Teacher teacher = new Teacher("Prof. Jones");

        assertNotNull(teacher.getId());
        assertTrue(teacher.getId().matches("\\d{4}"), "ID should be a zero-padded 4-digit string");
        assertEquals("Prof. Jones", teacher.getName());
        assertTrue(teacher.getBorrowedItems().isEmpty());
    }

    @Test
    void constructor_withExplicitId_advancesNextIdBeyondGivenId() {
        new Teacher("0080", "High ID Teacher", new ArrayList<>());
        Teacher next = new Teacher("Auto Teacher");

        assertTrue(Integer.parseInt(next.getId()) > 80);
    }

    @Test
    void constructor_withBorrowedItems_setsItemList() {
        DVD dvd = new DVD("0001", "Some Film", Item.Status.BORROWED, "Director", 90);
        Teacher teacher = new Teacher("0002", "Prof. Lee", new ArrayList<>(List.of(dvd)));

        assertEquals(1, teacher.getBorrowedItems().size());
        assertTrue(teacher.getBorrowedItems().contains(dvd));
    }

    // ── Borrowing limit ───────────────────────────────────────────────────────

    @Test
    void getBorrowingLimit_returns10() {
        Teacher teacher = new Teacher("0010", "Prof. Kim", new ArrayList<>());
        assertEquals(10, teacher.getBorrowingLimit());
    }

    @Test
    void getBorrowingLimit_isGreaterThanStudentLimit() {
        Teacher teacher = new Teacher("0011", "Prof. Park", new ArrayList<>());
        Student student = new Student("0012", "Student",    new ArrayList<>());

        assertTrue(teacher.getBorrowingLimit() > student.getBorrowingLimit());
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    @Test
    void setName_updatesName() {
        Teacher teacher = new Teacher("0020", "Old Name", new ArrayList<>());
        teacher.setName("New Name");
        assertEquals("New Name", teacher.getName());
    }

    @Test
    void setBorrowedItems_updatesItemList() {
        Teacher teacher = new Teacher("0021", "Prof. Chen", new ArrayList<>());
        Magazine mag = new Magazine("0003", "Journal", Item.Status.IN_STORE, 7, "Publisher");

        teacher.setBorrowedItems(new ArrayList<>(List.of(mag)));

        assertEquals(1, teacher.getBorrowedItems().size());
        assertTrue(teacher.getBorrowedItems().contains(mag));
    }

    // ── getBorrowedItemsId() ──────────────────────────────────────────────────

    @Test
    void getBorrowedItemsId_noItems_returnsEmptyString() {
        Teacher teacher = new Teacher("0030", "Prof. Adams", new ArrayList<>());
        assertEquals("", teacher.getBorrowedItemsId());
    }

    @Test
    void getBorrowedItemsId_oneItem_returnsItsId() {
        Book book = new Book("0004", "One Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Teacher teacher = new Teacher("0031", "Prof. Baker", new ArrayList<>(List.of(book)));

        assertTrue(teacher.getBorrowedItemsId().contains("0004"));
    }

    @Test
    void getBorrowedItemsId_mixedItemTypes_containsAllIds() {
        Book     book = new Book("0005",     "Book",  Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        DVD      dvd  = new DVD("0006",      "Film",  Item.Status.BORROWED, "Dir", 120);
        Magazine mag  = new Magazine("0007", "Mag",   Item.Status.BORROWED, 3, "Pub");

        Teacher teacher = new Teacher("0032", "Prof. Clark", new ArrayList<>(List.of(book, dvd, mag)));
        String ids = teacher.getBorrowedItemsId();

        assertTrue(ids.contains("0005"));
        assertTrue(ids.contains("0006"));
        assertTrue(ids.contains("0007"));
    }

    // ── save() ────────────────────────────────────────────────────────────────

    @Test
    void save_noItems_returnsCorrectFormat() {
        Teacher teacher = new Teacher("0040", "Prof. Davis", new ArrayList<>());
        assertEquals("Teacher,0040,Prof. Davis,\n", teacher.save());
    }

    @Test
    void save_withBorrowedItem_includesItemId() {
        DVD dvd = new DVD("0008", "Lecture Film", Item.Status.BORROWED, "Dir", 60);
        Teacher teacher = new Teacher("0041", "Prof. Evans", new ArrayList<>(List.of(dvd)));

        String saved = teacher.save();
        assertTrue(saved.startsWith("Teacher,0041,Prof. Evans,"));
        assertTrue(saved.contains("0008"));
    }

    @Test
    void save_withMultipleItems_includesAllItemIds() {
        Book book = new Book("0009", "Book",  Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        DVD dvd   = new DVD("0010",  "Film",  Item.Status.BORROWED, "Dir", 100);
        Teacher teacher = new Teacher("0042", "Prof. Fox", new ArrayList<>(List.of(book, dvd)));

        String saved = teacher.save();
        assertTrue(saved.contains("0009"));
        assertTrue(saved.contains("0010"));
    }

    // ── equals() & hashCode() ────────────────────────────────────────────────

    @Test
    void equals_sameIdAndName_returnsTrue() {
        Teacher a = new Teacher("0050", "Prof. Green", new ArrayList<>());
        Teacher b = new Teacher("0050", "Prof. Green", new ArrayList<>());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_differentName_returnsFalse() {
        Teacher a = new Teacher("0051", "Prof. Hall",   new ArrayList<>());
        Teacher b = new Teacher("0051", "Prof. Harris", new ArrayList<>());
        assertNotEquals(a, b);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Teacher a = new Teacher("0052", "Prof. Ivy", new ArrayList<>());
        Teacher b = new Teacher("0053", "Prof. Ivy", new ArrayList<>());
        assertNotEquals(a, b);
    }

    @Test
    void equals_teacherAndStudentWithSameFields_returnsFalse() {
        Teacher teacher = new Teacher("0054", "Sam", new ArrayList<>());
        Student student = new Student("0054", "Sam", new ArrayList<>());
        assertNotEquals(teacher, student);
    }

    @Test
    void equals_null_returnsFalse() {
        Teacher teacher = new Teacher("0055", "Prof. Jay", new ArrayList<>());
        assertNotEquals(null, teacher);
    }

    // ── toString() ────────────────────────────────────────────────────────────

    @Test
    void toString_containsTeacherIdAndName() {
        Teacher teacher = new Teacher("0060", "Prof. King", new ArrayList<>());
        String str = teacher.toString();
        assertTrue(str.contains("0060"));
        assertTrue(str.contains("Prof. King"));
    }
}
