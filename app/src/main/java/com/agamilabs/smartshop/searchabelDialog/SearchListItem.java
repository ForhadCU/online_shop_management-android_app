package com.agamilabs.smartshop.searchabelDialog;

public class SearchListItem {
    int id;
    String title;

    public SearchListItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}