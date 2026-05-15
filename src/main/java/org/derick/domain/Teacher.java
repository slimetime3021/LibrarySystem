package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    private static final int borrowingLimit = 10;

    public Teacher(String Id, String name, List<Item> borrowedItems) {
        super(Id, name, borrowedItems);
    }

    public Teacher(String name) {
        super(name);
    }

    @Override
    public int getBorrowingLimit() {
        return borrowingLimit;
    }

    @Override
    public String save() {
        return String.format("Teacher,%s,%s,%s\n", Id, name, this.getBorrowedItemsId());
    }
}
