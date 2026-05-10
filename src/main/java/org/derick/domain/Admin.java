package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    public Admin(String Id, String name, List<Item> borrowedItems) {
        super(Id, name, borrowedItems);
    }

    public Admin(String name) {
        super(name);
    }
}
