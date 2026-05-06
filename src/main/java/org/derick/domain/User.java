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
    protected int borrowingLimit;

    public User(String name, List<Item> borrowedItems) {
        this.name = name;
        this.borrowedItems = new ArrayList<>();
    }
}
