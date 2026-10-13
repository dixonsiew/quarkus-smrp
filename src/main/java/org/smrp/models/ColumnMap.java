package org.smrp.models;

public class ColumnMap {

    private final String field;
    private final String text;

    public ColumnMap(String field, String text) {
        this.field = field;
        this.text = text;
    }

    public String getField() {
        return field;
    }

    public String getText() {
        return text;
    }
}
