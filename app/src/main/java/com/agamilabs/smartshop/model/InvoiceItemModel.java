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
import java.util.ArrayList;

public class InvoiceItemModel implements Serializable {

    private int orgno;
    private int invoiceno;
    private int itemno;
    private ArrayList<String> item_id;
    private int qty;
    private double rate;
    private String selling_unitid;
    private String prate;
    private String discount_percentage;
    private double taxrate;
    private String expirydate;
    private String itemname;
    private String unit;
    private String hasexpiry;
    private double item_bill;
    private double sale_rate;



    public InvoiceItemModel() {
    }



    public InvoiceItemModel(int itemno, int qty, double rate,String itemname, double item_bill) {
        this.itemno = itemno;
        this.qty = qty;
        this.rate = rate;
        this.itemname = itemname;
        this.item_bill = item_bill;
    }

 /*   public InvoiceItemModel(int itemno, String item_id, int qty, double rate, String discount_percentage, String expirydate, String itemname, double item_bill) {
        this.itemno = itemno;
        this.item_id = item_id;
        this.qty = qty;
        this.rate = rate;
        this.discount_percentage = discount_percentage;
        this.expirydate = expirydate;
        this.itemname = itemname;
        this.item_bill = item_bill;
    }*/
    public InvoiceItemModel(int itemno, ArrayList<String> item_id, int qty, double rate, String discount_percentage, String expirydate, String itemname, double item_bill, double sale_rate) {
        this.itemno = itemno;
        this.item_id = item_id;
        this.qty = qty;
        this.rate = rate;
        this.discount_percentage = discount_percentage;
        this.expirydate = expirydate;
        this.itemname = itemname;
        this.item_bill = item_bill;
        this.sale_rate = sale_rate;
    }

    public InvoiceItemModel(int itemno, ArrayList<String> item_id, int qty, double rate, String discount_percentage, String expirydate, String itemname, double item_bill) {
        this.itemno = itemno;
        this.item_id = item_id;
        this.qty = qty;
        this.rate = rate;
        this.discount_percentage = discount_percentage;
        this.expirydate = expirydate;
        this.itemname = itemname;
        this.item_bill = item_bill;
    }
    /*
    productName, productID, item_id, expirydate, unitid, Double.parseDouble(product_price),
    null, discount_percentage, Double.parseDouble(productQuantity), Double.parseDouble(totalBill)
     */

    public double getSale_rate() {
        return sale_rate;
    }

    public void setSale_rate(double sale_rate) {
        this.sale_rate = sale_rate;
    }

    public int getItemno() {
        return itemno;
    }

    public int getQty() {
        return qty;
    }

    public double getRate() {
        return rate;
    }

    public String getItemname() {
        return itemname;
    }

    public double getItem_bill() {
        return item_bill;
    }

    public int getOrgno() {
        return orgno;
    }

    public int getInvoiceno() {
        return invoiceno;
    }


    public String getSelling_unitid() {
        return selling_unitid;
    }

    public String getPrate() {
        return prate;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public double getTaxrate() {
        return taxrate;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public String getUnit() {
        return unit;
    }

    public String getHasexpiry() {
        return hasexpiry;
    }

    public void setOrgno(int orgno) {
        this.orgno = orgno;
    }

    public void setInvoiceno(int invoiceno) {
        this.invoiceno = invoiceno;
    }

    public void setItemno(int itemno) {
        this.itemno = itemno;
    }

    public ArrayList<String> getItem_id() {
        return item_id;
    }

    public void setItem_id(ArrayList<String> item_id) {
        this.item_id = item_id;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setSelling_unitid(String selling_unitid) {
        this.selling_unitid = selling_unitid;
    }

    public void setPrate(String prate) {
        this.prate = prate;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public void setTaxrate(double taxrate) {
        this.taxrate = taxrate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setHasexpiry(String hasexpiry) {
        this.hasexpiry = hasexpiry;
    }

    public void setItem_bill(double item_bill) {
        this.item_bill = item_bill;
    }
}

