package org.derick.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public abstract class User {
    protected String Id;
    @Setter protected String name;
    @Setter protected List<Item> borrowedItems;

    private static int nextId = 1;
    private static final int borrowingLimit = 0;

    public User(String name, List<Item> borrowedItems) {
        this.Id = String.format("%04d", nextId++);
        this.name = name;
        this.borrowedItems = new ArrayList<>();
    }


    public int getBorrowingLimit(){
        return borrowingLimit;
    }
}
