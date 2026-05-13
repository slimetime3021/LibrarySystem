package org.derick.domain;

import lombok.Getter;
import org.derick.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Getter
public class Library {
    @Getter private static Map<String, Item> idItem;
    @Getter private static List<User> users;



    public static void initItems() {
        idItem = new HashMap<>();
        File file = new File(Constants.itemFilePath);
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] elements = line.split(",");
                Item item = switch (elements[0]) {
                    case "Book" -> new Book(elements[1], elements[2], stringToStatus(elements[3]),
                            elements[4],  elements[5], elements[6]);
                    case "DVD" -> new DVD(elements[1], elements[2], stringToStatus(elements[3]), elements[4],
                            Integer.parseInt(elements[5]));
                    case "Magazine" -> new Magazine(elements[1], elements[2], stringToStatus(elements[3]),
                            Integer.parseInt(elements[4]), elements[5]);
                    default -> throw new IllegalStateException("Unexpected value: " + elements[0]);
                };
                idItem.put(item.getId(), item);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initUsers() {
        users = new ArrayList<>();
        File file = new File(Constants.userFilePath);
        try(Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] elements = line.split(",");
                User user = switch (elements[0]) {
                    case "Student" -> new Student(elements[1], elements[2], idToItem(elements));
                    case "Teacher" -> new Teacher(elements[1], elements[2], idToItem(elements));
                    case "Admin" -> new Admin(elements[1], elements[2], idToItem(elements));
                    default -> throw new IllegalStateException("Unexpected value: " + elements[0]);
                };
                users.add(user);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        final int startIdx = 2;
        List<Item> items = new ArrayList<>();

        for (int i = startIdx; i < elements.length; i++) {
            items.add(idItem.get(elements[i]));
        }

        return items;
    }
}
