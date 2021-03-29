package com.agamilabs.smartshop.model;

public class LotsModel {
    int itemno;
    int orgno;
    int lotno;
    String sku;
    int qty;
    String expirydate;
    boolean isSelected;

    public LotsModel() {
    }

    public LotsModel(int itemno, int orgno, int lotno, String sku, int qty, String expirydate, boolean isSelected) {
        this.itemno = itemno;
        this.orgno = orgno;
        this.lotno = lotno;
        this.sku = sku;
        this.qty = qty;
        this.expirydate = expirydate;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getItemno() {
        return itemno;
    }

    public void setItemno(int itemno) {
        this.itemno = itemno;
    }

    public int getOrgno() {
        return orgno;
    }

    public void setOrgno(int orgno) {
        this.orgno = orgno;
    }

    public int getLotno() {
        return lotno;
    }

    public void setLotno(int lotno) {
        this.lotno = lotno;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }
}
