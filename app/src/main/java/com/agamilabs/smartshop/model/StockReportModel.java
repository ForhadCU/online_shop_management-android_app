package com.agamilabs.smartshop.model;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class StockReportModel {

    int orgno;
    int itemno;
    String reorderpoint;
    int remainingqty;
    int stockinqty;
    int stockoutqty;
    double initialqty;
    double prate;
    double salerate;
    double stockamount;
    double saleamount;
    String itemname;
    ArrayList<LotsModel> lotsModelArrayList;

    public StockReportModel() {
    }

    public StockReportModel(int orgno, int itemno, String reorderpoint, int remainingqty, int stockinqty, int stockoutqty, double initialqty, double prate,
                            double salerate, double stockamount, double saleamount, String itemname, ArrayList<LotsModel> lotsModelArrayList) {
        this.orgno = orgno;
        this.itemno = itemno;
        this.reorderpoint = reorderpoint;
        this.remainingqty = remainingqty;
        this.stockinqty = stockinqty;
        this.stockoutqty = stockoutqty;
        this.initialqty = initialqty;
        this.prate = prate;
        this.salerate = salerate;
        this.stockamount = stockamount;
        this.saleamount = saleamount;
        this.itemname = itemname;
        this.lotsModelArrayList = lotsModelArrayList;
    }

    public ArrayList<LotsModel> getLotsModelArrayList() {
        return lotsModelArrayList;
    }

    public void setLotsModelArrayList(ArrayList<LotsModel> lotsModelArrayList) {
        this.lotsModelArrayList = lotsModelArrayList;
    }

    public int getOrgno() {
        return orgno;
    }

    public void setOrgno(int orgno) {
        this.orgno = orgno;
    }

    public int getItemno() {
        return itemno;
    }

    public void setItemno(int itemno) {
        this.itemno = itemno;
    }

    public String getReorderpoint() {
        return reorderpoint;
    }

    public void setReorderpoint(String reorderpoint) {
        this.reorderpoint = reorderpoint;
    }

    public int getRemainingqty() {
        return remainingqty;
    }

    public void setRemainingqty(int remainingqty) {
        this.remainingqty = remainingqty;
    }

    public int getStockinqty() {
        return stockinqty;
    }

    public void setStockinqty(int stockinqty) {
        this.stockinqty = stockinqty;
    }

    public int getStockoutqty() {
        return stockoutqty;
    }

    public void setStockoutqty(int stockoutqty) {
        this.stockoutqty = stockoutqty;
    }

    public double getInitialqty() {
        return initialqty;
    }

    public void setInitialqty(double initialqty) {
        this.initialqty = initialqty;
    }

    public double getPrate() {
        return prate;
    }

    public void setPrate(double prate) {
        this.prate = prate;
    }

    public double getSalerate() {
        return salerate;
    }

    public void setSalerate(double salerate) {
        this.salerate = salerate;
    }

    public double getStockamount() {
        return stockamount;
    }

    public void setStockamount(double stockamount) {
        this.stockamount = stockamount;
    }

    public double getSaleamount() {
        return saleamount;
    }

    public void setSaleamount(double saleamount) {
        this.saleamount = saleamount;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    /*   public String itemno;
    public String itemname;
    public String initialqty, remainingqty, salerate, prate, stockamount;
    public String stockinqty, stockoutqty;
    public String reorderpoint;

    public StockReportModel() {
    }

    public Field[] getAllFields() {
        return this.getClass().getDeclaredFields();
    }


    public String getItemno() {
        return itemno;
    }

    public String getItemname() {
        return itemname;
    }

    public String getReorderpoint() {
        return reorderpoint;
    }

    public String getInitialqty() {
        return initialqty;
    }

    public String getRemainingqty() {
        return remainingqty;
    }

    public String getSalerate() {
        return salerate;
    }

    public String getPrate() {
        return prate;
    }

    public String getStockamount() {
        return stockamount;
    }

    public String getStockinqty() {
        return stockinqty;
    }

    public String getStockoutqty() {
        return stockoutqty;
    }


    @Override
    public String toString() {
        return "StockReportModel{" +
                "itemno=" + itemno +
                ", reorderpoint=" + reorderpoint +
                ", initialqty=" + initialqty +
                ", remainingqty=" + remainingqty +
                ", salerate=" + salerate +
                ", prate=" + prate +
                ", stockamount=" + stockamount +
                ", itemname='" + itemname + '\'' +
                ", stockinqty=" + stockinqty +
                ", stockoutqty=" + stockoutqty +
                '}';
    }*/
}
