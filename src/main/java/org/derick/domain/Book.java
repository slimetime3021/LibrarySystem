package org.derick.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Book extends Item {
    private String ISBN;
    private String author;
    private String genre;

    public Book(String Id, String title, Status status, String ISBN, String author, String genre) {
        if (!isValidISBN(ISBN)) {
            throw new IllegalArgumentException("ISBN is invalid");
        }
        super(Id, title, status);
        this.ISBN = ISBN;
        this.author = author;
        this.genre = genre;
    }

    public Book(String title, Status status, String ISBN, String author, String genre) {
        if (!isValidISBN(ISBN)) {
            throw new IllegalArgumentException("ISBN is invalid");
        }
        super(title, status);
        this.ISBN = ISBN;
        this.author = author;
        this.genre = genre;
    }

    public static boolean isValidISBN(String isbn) {
        isbn = isbn.replace("-", "").replace(" ", "");

        // Check ISBN-10
        if (isbn.length() == 10) {
            return isValidISBN10(isbn);
        }

        // Check ISBN-13
        if (isbn.length() == 13) {
            return isValidISBN13(isbn);
        }

        return false;
    }

    private static boolean isValidISBN10(String isbn) {
        int sum = 0;

        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);

            int value;

            // Last digit can be X
            if (i == 9 && c == 'X') {
                value = 10;
            } else if (Character.isDigit(c)) {
                value = c - '0';
            } else {
                return false;
            }

            sum += value * (10 - i);
        }

        return sum % 11 == 0;
    }

    private static boolean isValidISBN13(String isbn) {
        int sum = 0;

        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);

            if (!Character.isDigit(c)) {
                return false;
            }

            int digit = c - '0';

            // Alternate multiplying by 1 and 3
            if (i % 2 == 0) {
                sum += digit;
            } else {
                sum += digit * 3;
            }
        }

        return sum % 10 == 0;
    }
}
