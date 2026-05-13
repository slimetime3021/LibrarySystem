package org.derick.domain;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
public abstract class Item {
    protected String id;
    @Setter protected String title;
    @Setter protected Status status;

    @Getter private static Map<String, Item> idItem = new HashMap<>();
    private static int nextId = 1;

    public Item(String id, String title, Status status) {
        nextId = Math.max(Integer.parseInt(id)+1, nextId);
        this.id = id;
        this.title = title;
        this.status = status;
        idItem.put(id, this);
    }

    public Item(String title, Status status) {
        this(String.format("%04d", nextId),  title, status);
    }

    public enum Status {
        BORROWED, IN_STORE, LOST
    }
}
