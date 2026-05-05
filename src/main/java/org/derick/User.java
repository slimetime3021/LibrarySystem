package org.derick;

import java.util.ArrayList;
import java.util.List;


public abstract class User {
    protected String Id;
    protected String name;
    protected List<Item> borrowedItems;
    protected int borrowingLimit;

    public User(String name, List<Item> borrowedItems) {
        this.name = name;
        this.borrowedItems = new ArrayList<>();
    }
}
