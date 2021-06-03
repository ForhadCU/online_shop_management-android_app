package com.agamilabs.smartshop.model;

public class CategoryModel {
    String catName;
    String catId;

    public CategoryModel() {
    }

    public CategoryModel( String catId, String catName) {
        this.catName = catName;
        this.catId = catId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
