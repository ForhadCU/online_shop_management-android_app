package com.agamilabs.smartshop.model;

public class Products {
    private String name;
    private String id;

    public Products() {
    }

    public Products(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
