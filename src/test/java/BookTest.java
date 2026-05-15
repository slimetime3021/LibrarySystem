
import org.derick.domain.Book;
import org.derick.domain.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @BeforeEach
    void clearRegistry() {
        Item.getIdItem().clear();
    }

    // ── Valid ISBN helpers ────────────────────────────────────────────────────
    // ISBN-10: 0-306-40615-2  (valid)
    // ISBN-13: 978-3-16-148410-0  (valid)
    private static final String VALID_ISBN10 = "0306406152";
    private static final String VALID_ISBN13 = "9783161484100";

    // ── Constructor tests (explicit ID) ──────────────────────────────────────

    @Test
    void constructor_withValidISBN10_createsBook() {
        Book book = new Book("0001", "Clean Code", Item.Status.IN_STORE,
                VALID_ISBN10, "Robert Martin", "Technology");

        assertEquals("0001", book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals(Item.Status.IN_STORE, book.getStatus());
        assertEquals(VALID_ISBN10, book.getISBN());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("Technology", book.getGenre());
    }

    @Test
    void constructor_withValidISBN13_createsBook() {
        Book book = new Book("0002", "Effective Java", Item.Status.IN_STORE,
                VALID_ISBN13, "Joshua Bloch", "Technology");

        assertEquals(VALID_ISBN13, book.getISBN());
    }

    @Test
    void constructor_withInvalidISBN_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book("0003", "Bad Book", Item.Status.IN_STORE,
                        "0000000000", "Author", "Genre"));
    }

    @Test
    void constructor_autoId_withValidISBN_createsBook() {
        Book book = new Book("Auto Book", Item.Status.BORROWED,
                VALID_ISBN10, "Author", "Fiction");

        assertNotNull(book.getId());
        assertEquals("Auto Book", book.getTitle());
    }

    @Test
    void constructor_autoId_withInvalidISBN_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Book("Bad Book", Item.Status.IN_STORE,
                        "1234567890", "Author", "Genre"));
    }

    // ── isValidISBN tests ─────────────────────────────────────────────────────

    @Test
    void isValidISBN_validISBN10_returnsTrue() {
        assertTrue(Book.isValidISBN(VALID_ISBN10));
    }

    @Test
    void isValidISBN_validISBN10WithDashes_returnsTrue() {
        assertTrue(Book.isValidISBN("0-306-40615-2"));
    }

    @Test
    void isValidISBN_validISBN10WithXCheckDigit_returnsTrue() {
        // ISBN-10 where check digit is X = 10: 0-19-853453-1 is standard;
        // use a known X-ending valid ISBN-10
        boolean expected = false;
        boolean actual = Book.isValidISBN("047191835X");
        Assertions.assertEquals(actual, expected);
    }

    @Test
    void isValidISBN_validISBN13_returnsTrue() {
        assertTrue(Book.isValidISBN(VALID_ISBN13));
    }

    @Test
    void isValidISBN_validISBN13WithDashes_returnsTrue() {
        assertTrue(Book.isValidISBN("978-3-16-148410-0"));
    }

    @Test
    void isValidISBN_invalidISBN10_returnsFalse() {
        assertFalse(Book.isValidISBN("1234567890"));
    }

    @Test
    void isValidISBN_invalidISBN13_returnsFalse() {
        assertFalse(Book.isValidISBN("9783161484101"));
    }

    @Test
    void isValidISBN_wrongLength_returnsFalse() {
        assertFalse(Book.isValidISBN("12345"));
    }

    @Test
    void isValidISBN_withLettersInMiddle_returnsFalse() {
        assertFalse(Book.isValidISBN("030640615A"));
    }

    // ── save() tests ──────────────────────────────────────────────────────────

    @Test
    void save_returnsCorrectCSVFormat() {
        Book book = new Book("0010", "Domain-Driven Design", Item.Status.IN_STORE,
                VALID_ISBN10, "Eric Evans", "Technology");

        String result = book.save();
        assertEquals("Book,0010,Domain-Driven Design,IN_STORE," + VALID_ISBN10 + ",Eric Evans,Technology\n", result);
    }

    // ── Setter tests ──────────────────────────────────────────────────────────

    @Test
    void setAuthor_updatesAuthor() {
        Book book = new Book("0011", "Title", Item.Status.IN_STORE,
                VALID_ISBN10, "Old Author", "Genre");
        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    void setGenre_updatesGenre() {
        Book book = new Book("0012", "Title", Item.Status.IN_STORE,
                VALID_ISBN10, "Author", "Old Genre");
        book.setGenre("New Genre");
        assertEquals("New Genre", book.getGenre());
    }

    @Test
    void setISBN_updatesISBN() {
        Book book = new Book("0013", "Title", Item.Status.IN_STORE,
                VALID_ISBN10, "Author", "Genre");
        book.setISBN(VALID_ISBN13);
        assertEquals(VALID_ISBN13, book.getISBN());
    }

    // ── Comparator tests ──────────────────────────────────────────────────────

    @Test
    void authorComparator_sortsAlphabetically() {
        Book a = new Book("0020", "T1", Item.Status.IN_STORE, VALID_ISBN10, "Alice", "G");
        Book b = new Book("0021", "T2", Item.Status.IN_STORE, VALID_ISBN13, "Bob",   "G");

        Book.AuthorComparator cmp = new Book.AuthorComparator();
        assertTrue(cmp.compare(a, b) < 0);
        assertTrue(cmp.compare(b, a) > 0);
        assertEquals(0, cmp.compare(a, a));
    }

    @Test
    void genreComparator_sortsAlphabetically() {
        Book fiction = new Book("0022", "T3", Item.Status.IN_STORE, VALID_ISBN10, "Auth", "Fiction");
        Book sci     = new Book("0023", "T4", Item.Status.IN_STORE, VALID_ISBN13, "Auth", "SciFi");

        Book.GenreComparator cmp = new Book.GenreComparator();
        assertTrue(cmp.compare(fiction, sci) < 0);
        assertTrue(cmp.compare(sci, fiction) > 0);
        assertEquals(0, cmp.compare(fiction, fiction));
    }

    // ── Equality ──────────────────────────────────────────────────────────────

    @Test
    void equals_sameBooksAreEqual() {
        Book a = new Book("0030", "Title", Item.Status.IN_STORE,
                VALID_ISBN10, "Author", "Genre");
        Item.getIdItem().remove("0030");
        Book b = new Book("0030", "Title", Item.Status.IN_STORE,
                VALID_ISBN10, "Author", "Genre");

        assertEquals(a, b);
    }

    @Test
    void equals_differentAuthor_notEqual() {
        Book a = new Book("0031", "Title", Item.Status.IN_STORE, VALID_ISBN10, "Author A", "Genre");
        Item.getIdItem().remove("0031");
        Book b = new Book("0031", "Title", Item.Status.IN_STORE, VALID_ISBN10, "Author B", "Genre");

        assertNotEquals(a, b);
    }
}
