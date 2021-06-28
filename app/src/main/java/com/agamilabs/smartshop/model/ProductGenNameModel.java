package com.agamilabs.smartshop.model;

public class ProductGenNameModel {
    String genericid;
    String genericname;

    public ProductGenNameModel() {
    }

    public ProductGenNameModel(String genericid, String genericname) {
        this.genericid = genericid;
        this.genericname = genericname;
    }

    public String getGenericid() {
        return genericid;
    }

    public void setGenericid(String genericid) {
        this.genericid = genericid;
    }

    public String getGenericname() {
        return genericname;
    }

    public void setGenericname(String genericname) {
        this.genericname = genericname;
    }
}
