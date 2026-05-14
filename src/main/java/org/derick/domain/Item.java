package org.derick.domain;

import lombok.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
public abstract class Item  {
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

    public abstract String save();

    public static class IdComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

    public static class TitleComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    }

    public static class StatusComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getStatus().compareTo(o2.getStatus());
        }
    }
}
