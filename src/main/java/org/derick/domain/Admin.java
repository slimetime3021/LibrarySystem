package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.derick.interfaces.Reportable;

import java.util.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends User implements Reportable {

    public Admin(String Id, String name, List<Item> borrowedItems) {
        super(Id, name, borrowedItems);
    }

    public Admin(String name) {
        super(name);
    }

    @Override
    public String save() {
        return String.format("Admin,%s,%s,%s\n", Id, name, this.getBorrowedItemsId());
    }

    @Override
    public void reportItems() {
        List<Item> borrowedItems = new ArrayList<>();
        List<Item> InStoreItems = new ArrayList<>();
        List<Item> LostItems = new ArrayList<>();

        for (Item item : Item.getIdItem().values()) {
            switch (item.getStatus()) {
                case BORROWED ->  borrowedItems.add(item);
                case LOST ->  LostItems.add(item);
                case IN_STORE -> InStoreItems.add(item);
            }
        }

        System.out.println("In Store Items " + InStoreItems +
                "\nBorrowed Items: " + borrowedItems + "\nLost Items " + LostItems);
    }

    public void backupUsers() {
        Library.updateUsers();
    }

    public void backupItems() {
        Library.updateItems();
    }
}
