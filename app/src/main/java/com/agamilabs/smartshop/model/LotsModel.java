package com.agamilabs.smartshop.model;

public class LotsModel {
    int itemno;
    int orgno;
    int lotno;
    String sku;
    int qty;
    String expirydate;
    String updatedExpiryDate;
    boolean isSelected;
    boolean clickControl;
    int qtyIncrease;
    boolean deletable;
    int lastIndexPosition;
    boolean noExpiry  = false;

    public LotsModel() {
    }

    public LotsModel(int itemno, int orgno, int lotno, String sku, int qty, String expirydate, boolean isSelected, boolean clickControl, int qtyIncrease) {
        this.itemno = itemno;
        this.orgno = orgno;
        this.lotno = lotno;
        this.sku = sku;
        this.qty = qty;
        this.expirydate = expirydate;
        this.isSelected = isSelected;
        this.clickControl = clickControl;
        this.qtyIncrease = qtyIncrease;
    }

    public LotsModel(int lotno, String sku, int qty, int qtyIncrease, int lastIndexPosition) {
        this.sku = sku;
        this.qty = qty;
        this.qtyIncrease = qtyIncrease;
        this.lastIndexPosition = lastIndexPosition;
        this.lotno = lotno;
    }

    public LotsModel(int itemno, int orgno, String sku, int qtyIncrease) {
        this.itemno = itemno;
        this.orgno = orgno;
        this.sku = sku;
        this.qtyIncrease = qtyIncrease;
    }

    public String getUpdatedExpiryDate() {
        return updatedExpiryDate;
    }

    public void setUpdatedExpiryDate(String updatedExpiryDate) {
        this.updatedExpiryDate = updatedExpiryDate;
    }

    public boolean isNoExpiry() {
        return noExpiry;
    }

    public void setNoExpiry(boolean noExpiry) {
        this.noExpiry = noExpiry;
    }

    public int getLastIndexPosition() {
        return lastIndexPosition;
    }

    public void setLastIndexPosition(int lastIndexPosition) {
        this.lastIndexPosition = lastIndexPosition;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public int getQtyIncrease() {
        return qtyIncrease;
    }

    public void setQtyIncrease(int qtyIncrease) {
        this.qtyIncrease = qtyIncrease;
    }

    public boolean isClickControl() {
        return clickControl;
    }

    public void setClickControl(boolean clickControl) {
        this.clickControl = clickControl;
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
