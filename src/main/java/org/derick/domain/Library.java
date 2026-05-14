package org.derick.domain;

import lombok.Getter;
import org.derick.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Getter
public class Library {
    public static void initItems() {
        File file = new File(Constants.itemFilePath);
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] elements = line.split(",");
                switch (elements[0]) {
                    case "Book" -> new Book(elements[1], elements[2], stringToStatus(elements[3]),
                            elements[4],  elements[5], elements[6]);
                    case "DVD" -> new DVD(elements[1], elements[2], stringToStatus(elements[3]), elements[4],
                            Integer.parseInt(elements[5]));
                    case "Magazine" -> new Magazine(elements[1], elements[2], stringToStatus(elements[3]),
                            Integer.parseInt(elements[4]), elements[5]);
                    default -> throw new IllegalStateException("Unexpected value: " + elements[0]);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initUsers() {
        File file = new File(Constants.userFilePath);
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] elements = line.split(",", -1);
                switch (elements[0]) {
                    case "Student" -> new Student(elements[1], elements[2], idToItem(elements));
                    case "Teacher" -> new Teacher(elements[1], elements[2], idToItem(elements));
                    case "Admin" -> new Admin(elements[1], elements[2], idToItem(elements));
                    default -> throw new IllegalStateException("Unexpected value: " + elements[0]);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateItems() {
        File file = new File(Constants.itemFilePath);

        try(FileWriter fileWriter = new FileWriter(file, true)) {
            for (Item item : Item.getIdItem().values()) {
                fileWriter.write(item.save());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUsers() {
        File file = new File(Constants.userFilePath);

        try(FileWriter fileWriter = new FileWriter(file, true)) {
            for (User user : User.getUsers()) {
                fileWriter.write(user.save());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean borrow(User user, Item item) {
        if (user.getBorrowedItems().size() >= user.getBorrowingLimit()) {
            throw new IllegalStateException("User has already borrowed the maximum amount of items.");
        } else if (user instanceof Student && !(item instanceof Book)) {
            throw new IllegalArgumentException("Students may only borrow books");
        }

        item.setStatus(Item.Status.BORROWED);
        user.getBorrowedItems().add(item);

        return  true;
    }

    public boolean returnItem(User user, Item item) {
        if (user.getBorrowedItems().isEmpty()) {
            throw new IllegalArgumentException("User has no borrowed items.");
        } else if (!(user.getBorrowedItems().contains(item))) {
            throw new IllegalArgumentException("User has not borrowed the item.");
        }

        item.setStatus(Item.Status.IN_STORE);
        user.getBorrowedItems().remove(item);

        return  true;
    }

    public Item searchByTitleBorrowable(String title) {
        return Item.getIdItem().values().stream()
                .filter(item -> item.getStatus().equals(Item.Status.IN_STORE))
                .filter(item -> item.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item with title " + title + " not found"));
    }

    public Item searchByAuthorBorrowable(String author) {
        return Item.getIdItem().values().stream()
                .filter(item -> item.getStatus().equals(Item.Status.IN_STORE))
                .filter(item -> Library.getAuthorType(item).equalsIgnoreCase(author))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item with author " + author + " not found"));
    }

    private static Item.Status stringToStatus(String string) {
        return switch (string) {
            case "BORROWED" -> Item.Status.BORROWED;
            case "IN_STORE" -> Item.Status.IN_STORE;
            case "LOST" -> Item.Status.LOST;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }

    private static List<Item> idToItem(String[] elements) {
        final int startIdx = 3;
        List<Item> items = new ArrayList<>();

        for (int i = startIdx; i < elements.length; i++) {
            if (elements[i].isEmpty()) {
                continue;
            }
            items.add(Item.getIdItem().get(elements[i]));
        }

        return items;
    }

    private static String getAuthorType(Item item) {
        return switch (item) {
            case Book book -> book.getAuthor();
            case DVD dvd -> dvd.getDirector();
            case Magazine magazine -> magazine.getPublisher();
            default -> throw new IllegalStateException("Unexpected value: " + item);
        };
    }
}
