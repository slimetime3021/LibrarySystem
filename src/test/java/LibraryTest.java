
import org.derick.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private static final String VALID_ISBN = "9783161484100";

    private Library library;

    @BeforeEach
    void setUp() {
        Item.getIdItem().clear();
        User.getUsers().clear();
        library = new Library();
    }

    // ════════════════════════════════════════════════════════════════
    // borrow()
    // ════════════════════════════════════════════════════════════════

    @Test
    void borrow_student_borrowBook_succeeds() {
        Book book     = new Book("0001", "Java Book", Item.Status.IN_STORE, VALID_ISBN, "Author", "Tech");
        Student alice = new Student("0001", "Alice", new ArrayList<>());

        boolean result = library.borrow(alice, book);

        assertTrue(result);
        assertEquals(Item.Status.BORROWED, book.getStatus());
        assertTrue(alice.getBorrowedItems().contains(book));
    }

    @Test
    void borrow_student_borrowDVD_throwsIllegalArgumentException() {
        DVD dvd       = new DVD("0002", "Film", Item.Status.IN_STORE, "Director", 120);
        Student alice = new Student("0002", "Alice", new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> library.borrow(alice, dvd));
    }

    @Test
    void borrow_student_borrowMagazine_throwsIllegalArgumentException() {
        Magazine mag  = new Magazine("0003", "Mag", Item.Status.IN_STORE, 1, "Pub");
        Student alice = new Student("0003", "Alice", new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> library.borrow(alice, mag));
    }

    @Test
    void borrow_teacher_borrowBook_succeeds() {
        Book book     = new Book("0004", "Math Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Math");
        Teacher prof  = new Teacher("0004", "Prof", new ArrayList<>());

        assertTrue(library.borrow(prof, book));
    }

    @Test
    void borrow_teacher_borrowDVD_succeeds() {
        DVD dvd      = new DVD("0005", "Film", Item.Status.IN_STORE, "Dir", 90);
        Teacher prof = new Teacher("0005", "Prof", new ArrayList<>());

        assertTrue(library.borrow(prof, dvd));
    }

    @Test
    void borrow_teacher_borrowMagazine_succeeds() {
        Magazine mag = new Magazine("0006", "Mag", Item.Status.IN_STORE, 1, "Pub");
        Teacher prof = new Teacher("0006", "Prof", new ArrayList<>());

        assertTrue(library.borrow(prof, mag));
    }

    @Test
    void borrow_studentAtBorrowingLimit_throwsIllegalStateException() {
        Student alice = new Student("0007", "Alice", new ArrayList<>());

        // Fill up to limit (5 books)
        for (int i = 10; i < 15; i++) {
            String id = String.format("%04d", i);
            Book book = new Book(id, "Book " + i, Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
            library.borrow(alice, book);
        }

        Book extra = new Book("0099", "Extra Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        assertThrows(IllegalStateException.class, () -> library.borrow(alice, extra));
    }

    @Test
    void borrow_teacherAtBorrowingLimit_throwsIllegalStateException() {
        Teacher prof = new Teacher("0008", "Prof", new ArrayList<>());

        // Fill up to limit (10 items)
        for (int i = 20; i < 30; i++) {
            String id = String.format("%04d", i);
            Book book = new Book(id, "Book " + i, Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
            library.borrow(prof, book);
        }

        Book extra = new Book("0098", "Extra Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        assertThrows(IllegalStateException.class, () -> library.borrow(prof, extra));
    }

    @Test
    void borrow_setsItemStatusToBorrowed() {
        Book book     = new Book("0050", "Status Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        Teacher prof  = new Teacher("0050", "Prof", new ArrayList<>());

        library.borrow(prof, book);

        assertEquals(Item.Status.BORROWED, book.getStatus());
    }

    // ════════════════════════════════════════════════════════════════
    // returnItem()
    // ════════════════════════════════════════════════════════════════

    @Test
    void returnItem_succeeds_setsStatusToInStore() {
        Book book     = new Book("0060", "Return Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        Teacher prof  = new Teacher("0060", "Prof", new ArrayList<>());

        library.borrow(prof, book);
        boolean result = library.returnItem(prof, book);

        assertTrue(result);
        assertEquals(Item.Status.IN_STORE, book.getStatus());
        assertFalse(prof.getBorrowedItems().contains(book));
    }

    @Test
    void returnItem_userHasNoBorrowedItems_throwsIllegalArgumentException() {
        Book book    = new Book("0070", "Any Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");
        Teacher prof = new Teacher("0070", "Prof", new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> library.returnItem(prof, book));
    }

    @Test
    void returnItem_userDidNotBorrowThatItem_throwsIllegalArgumentException() {
        Book book1   = new Book("0071", "Book 1", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        Book book2   = new Book("0072", "Book 2", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        Teacher prof = new Teacher("0071", "Prof", new ArrayList<>());

        library.borrow(prof, book1);

        assertThrows(IllegalArgumentException.class, () -> library.returnItem(prof, book2));
    }

    @Test
    void returnItem_removesItemFromBorrowedList() {
        Book book    = new Book("0080", "Borrowed Book", Item.Status.IN_STORE, VALID_ISBN, "Auth", "Genre");
        Teacher prof = new Teacher("0080", "Prof", new ArrayList<>());

        library.borrow(prof, book);
        library.returnItem(prof, book);

        assertFalse(prof.getBorrowedItems().contains(book));
    }

    // ════════════════════════════════════════════════════════════════
    // searchByTitleBorrowable()
    // ════════════════════════════════════════════════════════════════

    @Test
    void searchByTitleBorrowable_existingInStoreItem_returnsItem() {
        Book book = new Book("0090", "The Great Gatsby", Item.Status.IN_STORE, VALID_ISBN, "F. Scott Fitzgerald", "Classic");

        Item found = library.searchByTitleBorrowable("The Great Gatsby");

        assertSame(book, found);
    }

    @Test
    void searchByTitleBorrowable_caseInsensitive_returnsItem() {
        new Book("0091", "1984", Item.Status.IN_STORE, VALID_ISBN, "Orwell", "Dystopia");

        assertDoesNotThrow(() -> library.searchByTitleBorrowable("1984"));
        assertDoesNotThrow(() -> library.searchByTitleBorrowable("1984".toLowerCase()));
    }

    @Test
    void searchByTitleBorrowable_itemIsBorrowed_throwsIllegalStateException() {
        new Book("0092", "Borrowed Book", Item.Status.BORROWED, VALID_ISBN, "Auth", "Genre");

        assertThrows(IllegalStateException.class, () ->
                library.searchByTitleBorrowable("Borrowed Book"));
    }

    @Test
    void searchByTitleBorrowable_itemNotFound_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () ->
                library.searchByTitleBorrowable("Nonexistent Title"));
    }

    // ════════════════════════════════════════════════════════════════
    // searchByAuthorBorrowable()
    // ════════════════════════════════════════════════════════════════

    @Test
    void searchByAuthorBorrowable_book_findsItemByAuthor() {
        Book book = new Book("0100", "Some Book", Item.Status.IN_STORE, VALID_ISBN, "Jane Austen", "Classic");

        Item found = library.searchByAuthorBorrowable("Jane Austen");

        assertSame(book, found);
    }

    @Test
    void searchByAuthorBorrowable_dvd_findsItemByDirector() {
        DVD dvd = new DVD("0101", "Some Film", Item.Status.IN_STORE, "Stanley Kubrick", 141);

        Item found = library.searchByAuthorBorrowable("Stanley Kubrick");

        assertSame(dvd, found);
    }

    @Test
    void searchByAuthorBorrowable_magazine_findsItemByPublisher() {
        Magazine mag = new Magazine("0102", "Some Mag", Item.Status.IN_STORE, 5, "Hearst");

        Item found = library.searchByAuthorBorrowable("Hearst");

        assertSame(mag, found);
    }

    @Test
    void searchByAuthorBorrowable_itemIsBorrowed_throwsIllegalStateException() {
        new Book("0103", "Borrowed Book", Item.Status.BORROWED, VALID_ISBN, "Hidden Author", "Genre");

        assertThrows(IllegalStateException.class, () ->
                library.searchByAuthorBorrowable("Hidden Author"));
    }

    @Test
    void searchByAuthorBorrowable_authorNotFound_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () ->
                library.searchByAuthorBorrowable("No Such Author"));
    }
}
