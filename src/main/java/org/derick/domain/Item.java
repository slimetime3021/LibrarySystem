package org.derick.domain;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public abstract class Item {
    protected String Id;
    @Setter protected String title;
    @Setter protected Status status;

    private static int nextId = 1;

    public Item(String title, Status status) {
        this.Id = String.format("%04d", nextId++);
        this.title = title;
        this.status = status;
    }

    public enum Status {
        BORROWED, IN_STORE, LOST
    }
}
