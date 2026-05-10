package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@Getter
@Setter
public class DVD extends Item {
    private String director;
    private int minutes;

    public DVD(String Id, String title, Status status, String director, int minutes) {
        super(Id, title, status);
        this.director = director;
        this.minutes = minutes;
    }

    public DVD(String title, Status status, String director, int minutes) {
        super(title, status);
        this.director = director;
        this.minutes = minutes;
    }
}
