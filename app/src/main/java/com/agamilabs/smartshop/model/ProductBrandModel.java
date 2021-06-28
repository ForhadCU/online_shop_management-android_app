package com.agamilabs.smartshop.model;

public class ProductBrandModel {
    String brandno;
    String brandname;

    public ProductBrandModel() {
    }

    public ProductBrandModel(String brandno, String brandname) {
        this.brandno = brandno;
        this.brandname = brandname;
    }

    public String getBrandno() {
        return brandno;
    }

    public void setBrandno(String brandno) {
        this.brandno = brandno;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }
}

