package com.agamilabs.smartshop.model;

public class ProductUnitModel {
    String unitid;
    String unittextshort;
    String unittext;
    String parent_unitid;
    String multiplier;

    public ProductUnitModel() {
    }

    public ProductUnitModel(String unitid, String unittextshort, String unittext, String parent_unitid, String multiplier) {
        this.unitid = unitid;
        this.unittextshort = unittextshort;
        this.unittext = unittext;
        this.parent_unitid = parent_unitid;
        this.multiplier = multiplier;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getUnittextshort() {
        return unittextshort;
    }

    public void setUnittextshort(String unittextshort) {
        this.unittextshort = unittextshort;
    }

    public String getUnittext() {
        return unittext;
    }

    public void setUnittext(String unittext) {
        this.unittext = unittext;
    }

    public String getParent_unitid() {
        return parent_unitid;
    }

    public void setParent_unitid(String parent_unitid) {
        this.parent_unitid = parent_unitid;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return unittextshort;
    }
}
