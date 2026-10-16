package org.smrp.models;

public record ColumnMap(String field, String text) {

    public String getField() {
        return field;
    }

    public String getText() {
        return text;
    }
}
