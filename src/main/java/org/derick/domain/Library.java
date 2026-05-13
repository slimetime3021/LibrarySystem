package org.derick.domain;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private static List<Item> items;
    private static List<User> users;



    public static void initBooks() {
        items = new ArrayList<>();
    }
}
