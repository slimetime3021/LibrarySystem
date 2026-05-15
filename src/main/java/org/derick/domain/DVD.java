package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;

@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@Getter
@Setter
public class DVD extends Item {
    private String director;
    private int minutes;

    public DVD(String Id, String title, Status status, String director, int minutes) {
        this.director = director;
        this.minutes = minutes;
        super(Id, title, status);
    }

    public DVD(String title, Status status, String director, int minutes) {
        this.director = director;
        this.minutes = minutes;
        super(title, status);
    }

    @Override
    public String save() {
        return String.format("DVD,%s,%s,%s,%s,%d\n", id, title, status, director, minutes);
    }

    public static class DirectorComparator implements Comparator<DVD> {
        @Override
        public int compare(DVD o1, DVD o2) {
            return o1.getDirector().compareTo(o2.getDirector());
        }
    }

    public static class MinutesComparator implements Comparator<DVD> {
        @Override
        public int compare(DVD o1, DVD o2) {
            return o1.getMinutes() - o2.getMinutes();
        }
    }
}
