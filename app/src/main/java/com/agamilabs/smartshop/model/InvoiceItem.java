/*
package com.agamilabs.smartshop.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private String org_itemNo;
    private String item_name;
    private String item_sku;

    private double qty;
    private double unit_price;
    private double tax;
    private double item_discount;
    private double item_bill;

    public InvoiceItem() {
    }

*/
/*    public InvoiceItem(String item_name, double qty, double item_bill) {
        this.item_name = item_name;
        this.qty = qty;
        this.item_bill = item_bill;
    }*//*


    public InvoiceItem(String item_name, double qty, double unit_price, double item_bill) {
        this.item_name = item_name;
        this.qty = qty;
        this.unit_price = unit_price;
        this.item_bill = item_bill;
    }

    public double getItem_bill() {
        return item_bill;
    }

    public String getOrg_itemNo() {
        return org_itemNo;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setOrg_itemNo(String org_itemNo) {
        this.org_itemNo = org_itemNo;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setItem_sku(String item_sku) {
        this.item_sku = item_sku;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setItem_discount(double item_discount) {
        this.item_discount = item_discount;
    }

    public void setItem_bill(double item_bill) {
        this.item_bill = item_bill;
    }

    public String getItem_sku() {
        return item_sku;
    }

    public double getQty() {
        return qty;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public double getTax() {
        return tax;
    }

    public double getItem_discount() {
        return item_discount;
    }
}


*/


package com.agamilabs.smartshop.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {

    private String itemno;
    private String item_name;
    private String item_id;
    private String expirydate;
    private String unitid;
    private double unitprice;
    private String taxrate;
    private String discount_percentage;
    private double qty;
    private double item_bill;

/*    private String item_name;
    private double qty;
    private double unit_price;
    private double tax;
    private double item_discount;
    private double item_bill;*/

    public InvoiceItem() {
    }

    public InvoiceItem(String item_name, String itemno, String item_id, String expirydate, String unitid, double unitprice, String taxrate, String discount_percentage, double qty, double item_bill) {
        this.itemno = itemno;
        this.item_name = item_name;
        this.item_id = item_id;
        this.expirydate = expirydate;
        this.unitid = unitid;
        this.unitprice = unitprice;
        this.taxrate = taxrate;
        this.discount_percentage = discount_percentage;
        this.qty = qty;
        this.item_bill = item_bill;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItemno() {
        return itemno;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public String getUnitid() {
        return unitid;
    }

    public double getUnitprice() {
        return unitprice;
    }

    public String getTaxrate() {
        return taxrate;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public double getQty() {
        return qty;
    }

    public double getItem_bill() {
        return item_bill;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public void setUnitprice(double unitprice) {
        this.unitprice = unitprice;
    }

    public void setTaxrate(String taxrate) {
        this.taxrate = taxrate;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setItem_bill(double item_bill) {
        this.item_bill = item_bill;
    }
}

