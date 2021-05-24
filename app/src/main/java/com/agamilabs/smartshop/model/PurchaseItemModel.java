package com.agamilabs.smartshop.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PurchaseItemModel implements Serializable {
    int orgNo;
    int itemNO;
    String itemName;
    double purchaseRate;
    double saleRate;
    double totalAmount;
    String unitid;
    String expirydate;
    String discount_percentage;
    int allIncreaseQty;
    ArrayList<LotsModel> summeryArrayList;
    ArrayList<LotsModel> lotsModelArrayList;
    String taxrate;


    public PurchaseItemModel() {
    }

    public PurchaseItemModel(int orgNo, int itemNO, String itemName, double purchaseRate, double totalAmount, ArrayList<LotsModel> summeryArrayList)
    {
        this.orgNo = orgNo;
        this.itemNO = itemNO;
        this.itemName = itemName;
        this.purchaseRate = purchaseRate;
        this.totalAmount = totalAmount;
        this.summeryArrayList = summeryArrayList;
    }

    public PurchaseItemModel(int orgNo, int itemNO, String itemName, double purchaseRate, double saleRate, double totalAmount, String unitid,
                             String expirydate, String discount_percentage, int allIncreaseQty, ArrayList<LotsModel> summeryArrayList, ArrayList<LotsModel> lotsModelArrayList, String taxrate) {
        this.orgNo = orgNo;
        this.itemNO = itemNO;
        this.itemName = itemName;
        this.purchaseRate = purchaseRate;
        this.totalAmount = totalAmount;
        this.lotsModelArrayList = lotsModelArrayList;
        this.summeryArrayList = summeryArrayList;
        this.saleRate = saleRate;
        this.unitid = unitid;
        this.expirydate = expirydate;
        this.discount_percentage = discount_percentage;
        this.allIncreaseQty = allIncreaseQty;
        this.taxrate = taxrate;
    }

    public String getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(String taxrate) {
        this.taxrate = taxrate;
    }

    public int getAllIncreaseQty() {
        return allIncreaseQty;
    }

    public void setAllIncreaseQty(int allIncreaseQty) {
        this.allIncreaseQty = allIncreaseQty;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public double getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(double saleRate) {
        this.saleRate = saleRate;
    }

    public ArrayList<LotsModel> getSummeryArrayList() {
        return summeryArrayList;
    }

    public void setSummeryArrayList(ArrayList<LotsModel> summeryArrayList) {
        this.summeryArrayList = summeryArrayList;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(int orgNo) {
        this.orgNo = orgNo;
    }

    public int getItemNO() {
        return itemNO;
    }

    public void setItemNO(int itemNO) {
        this.itemNO = itemNO;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(double purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ArrayList<LotsModel> getLotsModelArrayList() {
        return lotsModelArrayList;
    }

    public void setLotsModelArrayList(ArrayList<LotsModel> lotsModelArrayList) {
        this.lotsModelArrayList = lotsModelArrayList;
    }
}
