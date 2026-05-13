package org.derick.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
public abstract class User {
    protected String Id;
    @Setter protected String name;
    @Setter protected List<Item> borrowedItems;

    @Getter private static List<User> users = new ArrayList<>();
    private static int nextId = 1;
    private static final int borrowingLimit = 0;

    public User(String id, String name, List<Item> borrowedItems) {
        nextId = Math.max(nextId, Integer.parseInt(id) + 1);
        Id = id;
        this.name = name;
        this.borrowedItems = borrowedItems;
    }

    public User(String name) {
        this(String.format("%04d", nextId),  name, new ArrayList<>());
    }


    public int getBorrowingLimit(){
        return borrowingLimit;
    }
}
