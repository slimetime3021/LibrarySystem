package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper=true)
@Getter
@Setter
public class Magazine extends Item {
    private int issueNumber;
    private String publisher;

    public Magazine(String Id, String title, Status status, int issueNumber, String publisher) {
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        super(Id, title, status);
    }

    public Magazine(String title, Status status, int issueNumber, String publisher) {
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        super(title, status);
    }

    @Override
    public String save() {
        return String.format("Magazine,%s,%s,%s,%d,%s", id, title, status, issueNumber, publisher);
    }

    public static class IssueNumberComparator implements Comparator<Magazine> {
        @Override
        public int compare(Magazine o1, Magazine o2) {
            return o1.getIssueNumber() - o2.getIssueNumber();
        }
    }

    public static class PublisherComparator implements Comparator<Magazine> {
        @Override
        public int compare(Magazine o1, Magazine o2) {
            return o1.getPublisher().compareTo(o2.getPublisher());
        }
    }
}
