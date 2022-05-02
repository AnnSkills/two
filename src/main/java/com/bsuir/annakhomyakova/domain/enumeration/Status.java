package com.bsuir.annakhomyakova.domain.enumeration;

/**
 * The Status enumeration.
 */
public enum Status {
    OPEN("Open"),
    CLOSED("Closed"),
    DUPLICATE("Duplicate"),
    IN_PROGRESS("Progress"),
    REOPENED("Reopened"),
    SOLVED("Solved"),
    VERIFIED("Verified");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
