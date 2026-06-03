package com.kalamkaari.shared.exception;

public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String categoryId) {
        super("Category '" + categoryId + "' cannot be deleted because it is assigned to one or more products");
    }
}
