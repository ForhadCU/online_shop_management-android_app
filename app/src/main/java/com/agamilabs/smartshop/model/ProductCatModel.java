package com.agamilabs.smartshop.model;

public class ProductCatModel {
    String catid;
    String cattext;
    String parentcatid;

    public ProductCatModel() {
    }

    public ProductCatModel(String catid, String cattext, String parentcatid) {
        this.catid = catid;
        this.cattext = cattext;
        this.parentcatid = parentcatid;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCattext() {
        return cattext;
    }

    public void setCattext(String cattext) {
        this.cattext = cattext;
    }

    public String getParentcatid() {
        return parentcatid;
    }

    public void setParentcatid(String parentcatid) {
        this.parentcatid = parentcatid;
    }
}
