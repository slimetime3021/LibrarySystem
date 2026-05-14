
import org.derick.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Library using sample CSV files.
 *
 * items_test.csv layout  (matches Library.initItems parsing):
 *   Book    → Book,id,title,status,ISBN,author,genre
 *   DVD     → DVD,id,title,status,director,minutes
 *   Magazine→ Magazine,id,title,status,issueNumber,publisher
 *
 * users_test.csv layout  (matches Library.initUsers parsing):
 *   Student/Teacher/Admin → Type,id,name,itemId,itemId,...  (trailing comma when no items)
 */
class LibraryCSVIntegrationTest {

    // ── CSV content ───────────────────────────────────────────────────────────

    private static final String ITEMS_CSV = """
            Book,0001,Clean Code,IN_STORE,9783161484100,Robert Martin,Technology
            Book,0002,The Pragmatic Programmer,BORROWED,0306406152,David Thomas,Technology
            Book,0003,Design Patterns,LOST,9783161484100,Gang of Four,Technology
            DVD,0004,Inception,IN_STORE,Christopher Nolan,148
            DVD,0005,The Matrix,BORROWED,The Wachowskis,136
            DVD,0006,Parasite,LOST,Bong Joon-ho,132
            Magazine,0007,National Geographic,IN_STORE,42,Nat Geo Society
            Magazine,0008,Time,BORROWED,101,Time USA LLC
            Magazine,0009,Wired,LOST,5,Conde Nast
            """;

    /**
     * Users reference item IDs that must already exist in Item.idItem.
     * Alice (Student) has borrowed item 0002 (Pragmatic Programmer – BORROWED).
     * Prof. Smith (Teacher) has borrowed items 0005, 0007 (Matrix BORROWED, Nat Geo IN_STORE? — we set statuses
     * as they are in the CSV; the user list just records the association).
     */
    private static final String USERS_CSV = """
            Student,0001,Alice,0002,
            Teacher,0002,Prof. Smith,0005,0007,
            Admin,0003,Bob Admin,
            Student,0004,Carol,
            Teacher,0005,Prof. Jones,0001,
            Admin,0006,Dave Admin,0006,
            """;

    // ── Temp files written before each test ───────────────────────────────────

    @TempDir
    Path tempDir;

    private Path itemsFile;
    private Path usersFile;
    private Library library;

    @BeforeEach
    void setUp() throws Exception {
        // Clear all static state
        Item.getIdItem().clear();
        User.getUsers().clear();

        // Write temp CSVs
        itemsFile = tempDir.resolve("items_test.csv");
        usersFile = tempDir.resolve("users_test.csv");
        Files.writeString(itemsFile, ITEMS_CSV);
        Files.writeString(usersFile, USERS_CSV);

        // Point Constants paths to the temp files via system properties
        // (requires Constants to read from System.getProperty, OR we patch directly)
        // Since Constants uses literals, we call the loaders with a helper that
        // temporarily overrides the paths via reflection.
        patchConstants(itemsFile.toString(), usersFile.toString());

        Library.initItems();
        Library.initUsers();

        library = new Library();
    }

    @AfterEach
    void resetConstants() throws Exception {
        // Restore original paths (tests are self-contained via @TempDir anyway,
        // but good practice to leave Constants clean)
        patchConstants(
                "src/main/resources/items.csv",
                "src/main/resources/users.csv"
        );
    }

    /** Reflectively patches the final fields in Constants so initItems/initUsers
     *  read from our temp files instead of the hard-coded production paths. */
    private static void patchConstants(String itemPath, String userPath) throws Exception {
        var itemField = org.derick.util.Constants.class.getDeclaredField("itemFilePath");
        var userField = org.derick.util.Constants.class.getDeclaredField("userFilePath");

        itemField.setAccessible(true);
        userField.setAccessible(true);

        // Remove 'final' modifier
        var modifiers = java.lang.reflect.Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(itemField, itemField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        modifiers.setInt(userField, userField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);

        itemField.set(null, itemPath);
        userField.set(null, userPath);
    }

    // ════════════════════════════════════════════════════════════════
    // initItems() — parsing verification
    // ════════════════════════════════════════════════════════════════

    @Test
    void initItems_loadsCorrectNumberOfItems() {
        assertEquals(9, Item.getIdItem().size());
    }

    @Test
    void initItems_parsesBook_correctly() {
        Item item = Item.getIdItem().get("0001");

        assertInstanceOf(Book.class, item);
        Book book = (Book) item;
        assertEquals("Clean Code", book.getTitle());
        assertEquals(Item.Status.IN_STORE, book.getStatus());
        assertEquals("9783161484100", book.getISBN());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("Technology", book.getGenre());
    }

    @Test
    void initItems_parsesBook_withBorrowedStatus() {
        Item item = Item.getIdItem().get("0002");

        assertInstanceOf(Book.class, item);
        assertEquals(Item.Status.BORROWED, item.getStatus());
    }

    @Test
    void initItems_parsesBook_withLostStatus() {
        Item item = Item.getIdItem().get("0003");

        assertInstanceOf(Book.class, item);
        assertEquals(Item.Status.LOST, item.getStatus());
    }

    @Test
    void initItems_parsesDVD_correctly() {
        Item item = Item.getIdItem().get("0004");

        assertInstanceOf(DVD.class, item);
        DVD dvd = (DVD) item;
        assertEquals("Inception", dvd.getTitle());
        assertEquals(Item.Status.IN_STORE, dvd.getStatus());
        assertEquals("Christopher Nolan", dvd.getDirector());
        assertEquals(148, dvd.getMinutes());
    }

    @Test
    void initItems_parsesDVD_withBorrowedStatus() {
        Item item = Item.getIdItem().get("0005");

        assertInstanceOf(DVD.class, item);
        assertEquals(Item.Status.BORROWED, item.getStatus());
        assertEquals("The Wachowskis", ((DVD) item).getDirector());
    }

    @Test
    void initItems_parsesMagazine_correctly() {
        Item item = Item.getIdItem().get("0007");

        assertInstanceOf(Magazine.class, item);
        Magazine mag = (Magazine) item;
        assertEquals("National Geographic", mag.getTitle());
        assertEquals(Item.Status.IN_STORE, mag.getStatus());
        assertEquals(42, mag.getIssueNumber());
        assertEquals("Nat Geo Society", mag.getPublisher());
    }

    @Test
    void initItems_parsesMagazine_withLostStatus() {
        Item item = Item.getIdItem().get("0009");

        assertInstanceOf(Magazine.class, item);
        assertEquals(Item.Status.LOST, item.getStatus());
    }

    @Test
    void initItems_allItemIdsRegistered() {
        Map<String, Item> map = Item.getIdItem();
        for (String id : List.of("0001","0002","0003","0004","0005","0006","0007","0008","0009")) {
            assertTrue(map.containsKey(id), "Missing item ID: " + id);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // initUsers() — parsing verification
    // ════════════════════════════════════════════════════════════════

    @Test
    void initUsers_loadsCorrectNumberOfUsers() {
        assertEquals(6, User.getUsers().size());
    }

    @Test
    void initUsers_parsesStudent_correctly() {
        User user = findUserById("0001");

        assertInstanceOf(Student.class, user);
        assertEquals("Alice", user.getName());
    }

    @Test
    void initUsers_parsesStudent_withBorrowedItem() {
        User user = findUserById("0001");

        assertEquals(1, user.getBorrowedItems().size());
        assertEquals("0002", user.getBorrowedItems().getFirst().getId());
    }

    @Test
    void initUsers_parsesStudent_withNoItems() {
        User user = findUserById("0004");

        assertInstanceOf(Student.class, user);
        assertEquals("Carol", user.getName());
        assertTrue(user.getBorrowedItems().isEmpty());
    }

    @Test
    void initUsers_parsesTeacher_correctly() {
        User user = findUserById("0002");

        assertInstanceOf(Teacher.class, user);
        assertEquals("Prof. Smith", user.getName());
    }

    @Test
    void initUsers_parsesTeacher_withMultipleBorrowedItems() {
        User user = findUserById("0002");

        assertEquals(2, user.getBorrowedItems().size());
        List<String> ids = user.getBorrowedItems().stream()
                .map(Item::getId).toList();
        assertTrue(ids.contains("0005"));
        assertTrue(ids.contains("0007"));
    }

    @Test
    void initUsers_parsesAdmin_correctly() {
        User user = findUserById("0003");

        assertInstanceOf(Admin.class, user);
        assertEquals("Bob Admin", user.getName());
        assertTrue(user.getBorrowedItems().isEmpty());
    }

    @Test
    void initUsers_parsesAdmin_withBorrowedItem() {
        User user = findUserById("0006");

        assertInstanceOf(Admin.class, user);
        assertEquals(1, user.getBorrowedItems().size());
        assertEquals("0006", user.getBorrowedItems().getFirst().getId());
    }

    // ════════════════════════════════════════════════════════════════
    // borrow() — using CSV-loaded data
    // ════════════════════════════════════════════════════════════════

    @Test
    void borrow_studentBorrowsInStoreBook_succeeds() {
        Student carol = (Student) findUserById("0004");  // no items
        Book cleanCode = (Book) Item.getIdItem().get("0001");  // IN_STORE

        assertTrue(library.borrow(carol, cleanCode));
        assertEquals(Item.Status.BORROWED, cleanCode.getStatus());
        assertTrue(carol.getBorrowedItems().contains(cleanCode));
    }

    @Test
    void borrow_studentBorrowsDVD_throwsIllegalArgumentException() {
        Student carol = (Student) findUserById("0004");
        DVD inception = (DVD) Item.getIdItem().get("0004");  // IN_STORE

        assertThrows(IllegalArgumentException.class, () -> library.borrow(carol, inception));
    }

    @Test
    void borrow_studentBorrowsMagazine_throwsIllegalArgumentException() {
        Student carol = (Student) findUserById("0004");
        Magazine natGeo = (Magazine) Item.getIdItem().get("0007");  // IN_STORE

        assertThrows(IllegalArgumentException.class, () -> library.borrow(carol, natGeo));
    }

    @Test
    void borrow_teacherBorrowsDVD_succeeds() {
        Teacher profJones = (Teacher) findUserById("0005");  // has 1 item, limit 10
        DVD inception = (DVD) Item.getIdItem().get("0004");  // IN_STORE

        assertTrue(library.borrow(profJones, inception));
        assertTrue(profJones.getBorrowedItems().contains(inception));
    }

    @Test
    void borrow_teacherBorrowsMagazine_succeeds() {
        Teacher profJones = (Teacher) findUserById("0005");
        Magazine natGeo = (Magazine) Item.getIdItem().get("0007");  // IN_STORE

        assertTrue(library.borrow(profJones, natGeo));
    }

    @Test
    void borrow_studentAlreadyAtLimit_throwsIllegalStateException() {
        // Alice (Student) already has 1 item; fill up to limit of 5
        Student alice = (Student) findUserById("0001");
        for (int i = 10; i < 14; i++) {
            String id = String.format("%04d", i);
            Book extra = new Book(id, "Extra " + i, Item.Status.IN_STORE, "9783161484100", "A", "G");
            library.borrow(alice, extra);
        }
        // Alice now has 5 — one more should fail
        Book overflow = new Book("0099", "Overflow", Item.Status.IN_STORE, "9783161484100", "A", "G");
        assertThrows(IllegalStateException.class, () -> library.borrow(alice, overflow));
    }

    // ════════════════════════════════════════════════════════════════
    // returnItem() — using CSV-loaded data
    // ════════════════════════════════════════════════════════════════

    @Test
    void returnItem_studentReturnsBorrowedBook_succeeds() {
        Student alice = (Student) findUserById("0001");
        Item pragProg = Item.getIdItem().get("0002");  // Alice already has this

        assertTrue(library.returnItem(alice, pragProg));
        assertEquals(Item.Status.IN_STORE, pragProg.getStatus());
        assertFalse(alice.getBorrowedItems().contains(pragProg));
    }

    @Test
    void returnItem_userHasNoItems_throwsIllegalArgumentException() {
        Student carol = (Student) findUserById("0004");  // no borrowed items
        Item cleanCode = Item.getIdItem().get("0001");

        assertThrows(IllegalArgumentException.class, () -> library.returnItem(carol, cleanCode));
    }

    @Test
    void returnItem_userDidNotBorrowThatItem_throwsIllegalArgumentException() {
        Student alice = (Student) findUserById("0001");  // has 0002
        Item cleanCode = Item.getIdItem().get("0001");   // not borrowed by Alice

        assertThrows(IllegalArgumentException.class, () -> library.returnItem(alice, cleanCode));
    }

    // ════════════════════════════════════════════════════════════════
    // searchByTitleBorrowable() — using CSV-loaded data
    // ════════════════════════════════════════════════════════════════

    @Test
    void searchByTitle_inStoreItem_returnsItem() {
        Item found = library.searchByTitleBorrowable("Clean Code");
        assertNotNull(found);
        assertEquals("0001", found.getId());
    }

    @Test
    void searchByTitle_caseInsensitive_returnsItem() {
        assertDoesNotThrow(() -> library.searchByTitleBorrowable("clean code"));
        assertDoesNotThrow(() -> library.searchByTitleBorrowable("CLEAN CODE"));
    }

    @Test
    void searchByTitle_borrowedItem_throwsIllegalStateException() {
        // "The Pragmatic Programmer" (0002) is BORROWED in the CSV
        assertThrows(IllegalStateException.class,
                () -> library.searchByTitleBorrowable("The Pragmatic Programmer"));
    }

    @Test
    void searchByTitle_lostItem_throwsIllegalStateException() {
        // "Design Patterns" (0003) is LOST
        assertThrows(IllegalStateException.class,
                () -> library.searchByTitleBorrowable("Design Patterns"));
    }

    @Test
    void searchByTitle_nonExistentTitle_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class,
                () -> library.searchByTitleBorrowable("No Such Title"));
    }

    // ════════════════════════════════════════════════════════════════
    // searchByAuthorBorrowable() — using CSV-loaded data
    // ════════════════════════════════════════════════════════════════

    @Test
    void searchByAuthor_book_findsItemByAuthor() {
        Item found = library.searchByAuthorBorrowable("Robert Martin");
        assertEquals("0001", found.getId());
    }

    @Test
    void searchByAuthor_dvd_findsItemByDirector() {
        // Inception (0004) is IN_STORE, director = Christopher Nolan
        Item found = library.searchByAuthorBorrowable("Christopher Nolan");
        assertEquals("0004", found.getId());
    }

    @Test
    void searchByAuthor_magazine_findsItemByPublisher() {
        // National Geographic (0007) is IN_STORE, publisher = Nat Geo Society
        Item found = library.searchByAuthorBorrowable("Nat Geo Society");
        assertEquals("0007", found.getId());
    }

    @Test
    void searchByAuthor_authorOfBorrowedItem_throwsIllegalStateException() {
        // "David Thomas" authored The Pragmatic Programmer (0002) which is BORROWED
        assertThrows(IllegalStateException.class,
                () -> library.searchByAuthorBorrowable("David Thomas"));
    }

    @Test
    void searchByAuthor_nonExistentAuthor_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class,
                () -> library.searchByAuthorBorrowable("Unknown Author"));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private User findUserById(String id) {
        return User.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new AssertionError("User not found: " + id));
    }
}
