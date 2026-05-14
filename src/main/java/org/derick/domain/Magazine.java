package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}
