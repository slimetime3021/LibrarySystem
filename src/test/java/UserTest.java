
import org.derick.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the User abstract class and its concrete subclasses Student and Teacher.
 * Grouped together because Student/Teacher are thin wrappers around User.
 */
class UserTest {

    private static final String VALID_ISBN = "9783161484100";

    @BeforeEach
    void clearStaticState() {
        Item.getIdItem().clear();
        User.getUsers().clear();
    }

    // ════════════════════════════════════════════════════════════════
    // User (via Student)
    // ════════════════════════════════════════════════════════════════

    @Test
    void user_constructor_withExplicitId_setsFields() {
        Student student = new Student("0001", "Alice", new ArrayList<>());

        assertEquals("0001", student.getId());
        assertEquals("Alice", student.getName());
        assertTrue(student.getBorrowedItems().isEmpty());
    }

    @Test
    void user_constructor_withName_autoAssignsId() {
        Student student = new Student("Bob");

        assertNotNull(student.getId());
        assertTrue(student.getId().matches("\\d{4}"));
        assertEquals("Bob", student.getName());
    }

    @Test
    void user_setName_updatesName() {
        Student student = new Student("0010", "Old Name", new ArrayList<>());
        student.setName("New Name");
        assertEquals("New Name", student.getName());
    }

    @Test
    void user_getBorrowedItemsId_emptyList_returnsEmptyString() {
        Student student = new Student("0020", "Carol", new ArrayList<>());
        assertEquals("", student.getBorrowedItemsId());
    }

    @Test
    void user_getBorrowedItemsId_withItems_returnsCommaSeparatedIds() {
        Book book = new Book("0001", "Title", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        DVD dvd   = new DVD("0002", "Film",  Item.Status.IN_STORE, "Dir", 90);

        Student student = new Student("0030", "Dave", new ArrayList<>(List.of(book, dvd)));
        String ids = student.getBorrowedItemsId();

        assertTrue(ids.contains("0001"));
        assertTrue(ids.contains("0002"));
    }

    // ── User comparators ──────────────────────────────────────────────────────

    @Test
    void idComparator_sortsCorrectly() {
        Student a = new Student("0100", "A", new ArrayList<>());
        Student b = new Student("0200", "B", new ArrayList<>());

        User.IdComparator cmp = new User.IdComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    @Test
    void nameComparator_sortsAlphabetically() {
        Student alice = new Student("0101", "Alice", new ArrayList<>());
        Student zara  = new Student("0102", "Zara",  new ArrayList<>());

        User.NameComparator cmp = new User.NameComparator();
        assertTrue(cmp.compare(alice, zara) < 0);
        assertTrue(cmp.compare(zara, alice) > 0);
        assertEquals(0, cmp.compare(alice, alice));
    }

    // ════════════════════════════════════════════════════════════════
    // Student
    // ════════════════════════════════════════════════════════════════

    @Test
    void student_borrowingLimit_is5() {
        Student student = new Student("0050", "Eve", new ArrayList<>());
        assertEquals(5, student.getBorrowingLimit());
    }

    @Test
    void student_save_returnsCorrectCSVFormat_noItems() {
        Student student = new Student("0060", "Frank", new ArrayList<>());
        String saved = student.save();

        assertTrue(saved.startsWith("Student,0060,Frank,"));
    }

    @Test
    void student_save_includesBorrowedItemIds() {
        Book book = new Book("0003", "Book Title", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Student student = new Student("0070", "Grace", new ArrayList<>(List.of(book)));

        assertTrue(student.save().contains("0003"));
    }

    @Test
    void student_equals_sameStudentsAreEqual() {
        Student a = new Student("0080", "Hank", new ArrayList<>());
        Student b = new Student("0080", "Hank", new ArrayList<>());
        assertEquals(a, b);
    }

    @Test
    void student_equals_differentName_notEqual() {
        Student a = new Student("0081", "Hank",  new ArrayList<>());
        Student b = new Student("0081", "Henry", new ArrayList<>());
        assertNotEquals(a, b);
    }

    // ════════════════════════════════════════════════════════════════
    // Teacher
    // ════════════════════════════════════════════════════════════════

    @Test
    void teacher_borrowingLimit_is10() {
        Teacher teacher = new Teacher("0090", "Prof. Smith", new ArrayList<>());
        assertEquals(10, teacher.getBorrowingLimit());
    }

    @Test
    void teacher_borrowingLimitHigherThanStudent() {
        Student student = new Student("0091", "Student", new ArrayList<>());
        Teacher teacher = new Teacher("0092", "Teacher", new ArrayList<>());
        assertTrue(teacher.getBorrowingLimit() > student.getBorrowingLimit());
    }

    @Test
    void teacher_save_returnsCorrectCSVFormat_noItems() {
        Teacher teacher = new Teacher("0093", "Prof. Jones", new ArrayList<>());
        String saved = teacher.save();

        assertTrue(saved.startsWith("Teacher,0093,Prof. Jones,"));
    }

    @Test
    void teacher_save_includesBorrowedItemIds() {
        DVD dvd = new DVD("0004", "Lecture Film", Item.Status.BORROWED, "Dir", 60);
        Teacher teacher = new Teacher("0094", "Prof. Lee", new ArrayList<>(List.of(dvd)));

        assertTrue(teacher.save().contains("0004"));
    }

    @Test
    void teacher_equals_sameTeachersAreEqual() {
        Teacher a = new Teacher("0095", "Prof. Kim", new ArrayList<>());
        Teacher b = new Teacher("0095", "Prof. Kim", new ArrayList<>());
        assertEquals(a, b);
    }

    @Test
    void teacher_equals_studentWithSameId_notEqual() {
        Student student = new Student("0096", "Same", new ArrayList<>());
        Teacher teacher = new Teacher("0096", "Same", new ArrayList<>());
        // Different runtime types; Lombok @EqualsAndHashCode(callSuper=true) checks class
        assertNotEquals(student, teacher);
    }
}
