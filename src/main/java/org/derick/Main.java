package org.derick;

import org.derick.domain.*;

public class Main {
    static void main() {
        Admin admin = new Admin("Rick");
        Book book1 = new Book("JAVA GUIDE", Item.Status.IN_STORE,
                "978-3-16-148410-0", "Kim", "guide");
        Book book2 = new Book("JAVA GUIDE2", Item.Status.LOST,
                "978-1-861-97271-2", "Kim", "guide");
        DVD dvd1 = new DVD("FNF", Item.Status.BORROWED, "Mark", 78);
        DVD dvd2 = new DVD("FNF2", Item.Status.IN_STORE, "Mark", 90);
        Magazine magazine1 = new Magazine("BAt", Item.Status.LOST, 1, "fell");
        Magazine magazine2 = new Magazine("BAt2", Item.Status.BORROWED, 2, "fell");
        admin.reportItems();
        System.out.println(User.getUsers());
        admin.backupItems();
        admin.backupUsers();
    }
}
